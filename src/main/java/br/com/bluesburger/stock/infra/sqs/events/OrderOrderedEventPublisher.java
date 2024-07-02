package br.com.bluesburger.stock.infra.sqs.events;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.AmazonSQS;

import br.com.bluesburger.stock.application.sqs.events.OrderOrderedEvent;
import br.com.bluesburger.stock.infra.sqs.SqsQueueSupport;
import lombok.ToString;

@ToString(callSuper = true)
@Service
public class OrderOrderedEventPublisher extends OrderEventPublisher<OrderOrderedEvent> {

	public OrderOrderedEventPublisher(@Value("${queue.order.ordered-event:order-ordered-event.fifo}") String queueName,
			AmazonSQS amazonSQS, SqsQueueSupport<OrderOrderedEvent> sqsQueueSupport) {
		super(queueName, amazonSQS, sqsQueueSupport);
	}

}
