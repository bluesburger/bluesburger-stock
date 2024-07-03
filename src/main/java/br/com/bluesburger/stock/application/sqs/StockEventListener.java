package br.com.bluesburger.stock.application.sqs;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.aws.messaging.listener.Acknowledgment;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import br.com.bluesburger.stock.application.dto.order.ReserveOrderRequest;
import br.com.bluesburger.stock.application.dto.order.ScheduleOrderRequest;
import br.com.bluesburger.stock.application.sqs.commands.CancelOrderStockCommand;
import br.com.bluesburger.stock.application.sqs.commands.OrderScheduledCommand;
import br.com.bluesburger.stock.application.sqs.commands.OrderStockCommand;
import br.com.bluesburger.stock.application.sqs.events.OrderOrderedEvent;
import br.com.bluesburger.stock.application.sqs.events.OrderScheduledEvent;
import br.com.bluesburger.stock.application.usecase.StockUseCase;
import br.com.bluesburger.stock.domain.entity.Status;
import br.com.bluesburger.stock.infra.sqs.events.OrderEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "cloud.aws.sqs.listener.auto-startup", havingValue = "true")
public class StockEventListener {
	
	private final StockUseCase stockUseCase;
	
	private final OrderEventPublisher<OrderOrderedEvent> orderOrderedEventPublisher;
	
	private final OrderEventPublisher<OrderScheduledEvent> orderScheculedEventPublisher;
	
	@SqsListener(value = "${queue-order-stock-command:queue-order-stock-command.fifo}", deletionPolicy = SqsMessageDeletionPolicy.NEVER)
	public void handle(@Payload OrderStockCommand command, Acknowledgment ack) {
		log.info("Command received: {}", command.toString());
		var request = new ReserveOrderRequest(command.getOrderId(), command.getItems());

		stockUseCase.reserveOrder(request);
		var event = new OrderOrderedEvent(command.getOrderId(), Status.RESERVED);
		if (orderOrderedEventPublisher.publish(event).isPresent()) {
			ack.acknowledge();
		}
	}
	
	@SqsListener(value = "${queue-schedule-order-command:queue-schedule-order-command.fifo}", deletionPolicy = SqsMessageDeletionPolicy.NEVER)
	public void handle(@Payload OrderScheduledCommand command, Acknowledgment ack) {
		log.info("Command received: {}", command.toString());
		var request = new ScheduleOrderRequest(command.getOrderId());
		
		var scheduled = stockUseCase.scheduleOrder(request);
		var event = new OrderScheduledEvent(scheduled.getOrderId(), scheduled.getStatus());
		orderScheculedEventPublisher.publish(event)
			.ifPresent(id -> ack.acknowledge());
	}

	@SqsListener(value = "${queue-cancel-order-stock-command:queue-cancel-order-stock-command.fifo}", deletionPolicy = SqsMessageDeletionPolicy.NEVER)
	public void handle(@Payload CancelOrderStockCommand command, Acknowledgment ack) {
		log.info("Command received: {}", command.toString());
		stockUseCase.cancelOrder(command.getOrderId())
			.ifPresent(stock -> ack.acknowledge());
	}
}
