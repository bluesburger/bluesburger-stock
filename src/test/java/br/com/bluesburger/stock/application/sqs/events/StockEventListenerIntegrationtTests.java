package br.com.bluesburger.stock.application.sqs.events;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.bluesburger.stock.application.dto.order.OrderItem;
import br.com.bluesburger.stock.application.sqs.commands.CancelOrderStockCommand;
import br.com.bluesburger.stock.application.sqs.commands.OrderCommand;
import br.com.bluesburger.stock.application.sqs.commands.OrderScheduledCommand;
import br.com.bluesburger.stock.application.sqs.commands.OrderStockCommand;
import br.com.bluesburger.stock.domain.entity.Status;
import br.com.bluesburger.stock.infra.database.ProductRepository;
import br.com.bluesburger.stock.infra.database.StockRepository;
import br.com.bluesburger.stock.infra.database.entity.OrderStockEntity;
import br.com.bluesburger.stock.infra.database.entity.ProductEntity;
import br.com.bluesburger.stock.support.QueueIntegrationSupport;

@SuppressWarnings("unused")
class StockEventListenerIntegrationtTests extends QueueIntegrationSupport {
	private static final UUID ORDER_ID = UUID.fromString("ddedf1ab-0b2f-4766-a9fc-104bedc98492");
	private static final Integer MAX_SECONDS = 10;

	@Autowired
	AmazonSQSAsync SQS;

	@Autowired
	ObjectMapper objectMapper;

	@Value("${queue-order-stock-command}")
	private String queueOrderStockCommand;
	
	@Value("${queue-schedule-order-command}")
	private String queueOrderScheduledCommand;
	
	@Value("${queue-cancel-order-stock-command}")
	private String queueOrderStockCancelCommand;
	
	@Autowired
	private StockRepository stockRepository;
	
	@Autowired
	private ProductRepository productRepository;

	@Test
	void whenPublishOrderStockCommandInQueue_ThenShouldHandleAndAck() throws JsonProcessingException {
		// given
		var product = productRepository.save(new ProductEntity("Produto XPTO", 100));
		var stock = stockRepository.save(new OrderStockEntity(Status.PENDING, ORDER_ID, product));
		
		var command = new OrderStockCommand(ORDER_ID.toString(), List.of(new OrderItem(1L)));
		var queueUrl = SQS.getQueueUrl(queueOrderStockCommand).getQueueUrl();
		
		// when
		SQS.sendMessage(createMessageRequest(queueUrl, command));
		
		// then
		await().atMost(MAX_SECONDS, SECONDS).untilAsserted(() -> {
            assertThat(numberOfMessagesInQueue(queueUrl)).isZero();
            assertThat(numberOfMessagesNotVisibleInQueue(queueUrl)).isZero();
		});
	}
	
	@Test
	void whenPublishOrderScheduledCommandInQueue_ThenShouldHandleAndAck() throws JsonProcessingException {
		// given
		var product = productRepository.save(new ProductEntity("Produto XPTO 2", 100));
		var stock = stockRepository.save(new OrderStockEntity(Status.RESERVED, ORDER_ID, product));
		
		var command = new OrderScheduledCommand(ORDER_ID.toString());
		var queueUrl = SQS.getQueueUrl(queueOrderScheduledCommand).getQueueUrl();
		
		// when
		SQS.sendMessage(createMessageRequest(queueUrl, command));
		
		// then
		await().atMost(MAX_SECONDS, SECONDS).untilAsserted(() -> {
            assertThat(numberOfMessagesInQueue(queueUrl)).isZero();
            assertThat(numberOfMessagesNotVisibleInQueue(queueUrl)).isZero();
		});
	}
	
	@Test
	void whenPublishCancelOrderStockCommandInQueue_ThenShouldHandleAndAck() throws JsonProcessingException {
		// given
		var product = productRepository.save(new ProductEntity("Produto XPTO 3", 100));
		var stock = stockRepository.save(new OrderStockEntity(Status.PENDING, ORDER_ID, product));
		
		var command = new CancelOrderStockCommand(ORDER_ID.toString());
		var queueUrl = SQS.getQueueUrl(queueOrderStockCancelCommand).getQueueUrl();
		
		// when
		SQS.sendMessage(createMessageRequest(queueUrl, command));
		
		// then
		await().atMost(MAX_SECONDS, SECONDS).untilAsserted(() -> {
            assertThat(numberOfMessagesInQueue(queueUrl)).isZero();
            assertThat(numberOfMessagesNotVisibleInQueue(queueUrl)).isZero();
		});
	}
	
	private SendMessageRequest createMessageRequest(String queueUrl, OrderCommand command) throws JsonProcessingException {
		return new SendMessageRequest()
			.withQueueUrl(queueUrl)
			.withMessageBody(objectMapper.writeValueAsString(command));
	}
	
	private SendMessageRequest createMessageFifoRequest(String queueUrl, OrderCommand command) throws JsonProcessingException {
		return new SendMessageRequest()
			.withQueueUrl(queueUrl)
			.withMessageDeduplicationId("1")
			.withMessageGroupId("stockevents")
			.withMessageBody(objectMapper.writeValueAsString(command));
	}
	
	private Integer numberOfMessagesInQueue(String consumerQueueName) {
        GetQueueAttributesResult attributes = SQS
                .getQueueAttributes(consumerQueueName, List.of("All"));

        return Integer.parseInt(
                attributes.getAttributes().get("ApproximateNumberOfMessages")
        );
    }

	private Integer numberOfMessagesNotVisibleInQueue(String consumerQueueName) {
		GetQueueAttributesResult attributes = SQS.getQueueAttributes(consumerQueueName, List.of("All"));

		return Integer.parseInt(attributes.getAttributes().get("ApproximateNumberOfMessagesNotVisible"));
	}
}
