package br.com.bluesburger.stock.application.sqs.events;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.fasterxml.jackson.core.JsonProcessingException;

import br.com.bluesburger.stock.domain.entity.Status;
import br.com.bluesburger.stock.infra.sqs.SqsQueueSupport;
import br.com.bluesburger.stock.infra.sqs.events.OrderOrderedEventPublisher;

@TestPropertySource(
		properties = { 
			"cloud.aws.endpoint.uri=http://localhost:1234",
			"cloud.aws.accountId=1234567890"
		})
@ExtendWith(MockitoExtension.class)
class OrderOrderedEventPublisherUnitTests<T extends OrderEvent> {
	
	private UUID ORDER_ID = UUID.fromString("f498e2c7-6cc9-4c61-9a69-5473cadf08c1");
	
	@Mock
	private AmazonSQS amazonSQS;
	
	@Mock
	private SqsQueueSupport<OrderOrderedEvent> sqsQueueSupport;
	
	private OrderOrderedEventPublisher publisher;
	
	@BeforeEach
	void setUp() {
		publisher = new OrderOrderedEventPublisher("queue-teste.fifo", amazonSQS, sqsQueueSupport);
	}

	@Test
	void shouldPublish() throws JsonProcessingException {
		var event = new OrderOrderedEvent(ORDER_ID.toString(), Status.PENDING);
		
		var request = new SendMessageRequest("http://localhost:123456", """
				{
					"id": "qwerty"
				}
				""");
		doReturn(request)
			.when(sqsQueueSupport)
			.createRequest(event, "queue-teste.fifo", ORDER_ID.toString());
		
		String messageIdReturned = "qwerty-qwerty-qwerty-qwerty";
		var messageResult = new SendMessageResult();
		messageResult.setMessageId(messageIdReturned);

		doReturn(messageResult)
			.when(amazonSQS)
			.sendMessage(request);
		
		assertThat(publisher.publish(event))
			.isNotNull()
			.isPresent()
			.get()
			.isEqualTo(messageIdReturned);
	}
	
	@Test
	void shouldPublish_ThenShouldCatchJsonProcessingExceptionAndReturnEmpty() throws JsonProcessingException {
		var event = new OrderOrderedEvent(ORDER_ID.toString(), Status.PENDING);
		
		doThrow(JsonProcessingException.class)
			.when(sqsQueueSupport)
			.createRequest(event, "queue-teste.fifo", ORDER_ID.toString());
		
		assertThat(publisher.publish(event))
			.isNotNull()
			.isNotPresent();
	}
}
