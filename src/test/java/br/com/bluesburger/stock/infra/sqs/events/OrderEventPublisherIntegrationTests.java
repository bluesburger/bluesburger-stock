package br.com.bluesburger.stock.infra.sqs.events;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.bluesburger.stock.application.sqs.events.OrderOrderedEvent;
import br.com.bluesburger.stock.domain.entity.Status;
import br.com.bluesburger.stock.support.ApplicationIntegrationSupport;

class OrderEventPublisherIntegrationTests extends ApplicationIntegrationSupport {
	
	private static final UUID ORDER_ID = UUID.fromString("ddedf1ab-0b2f-4766-a9fc-104bedc98492");

	@Autowired
	private OrderEventPublisher<OrderOrderedEvent> orderOrderedEventPublisher;
	
	@Test
	void whenPublishEvent_ThenShouldReturnId() {
		var event = new OrderOrderedEvent(ORDER_ID.toString(), Status.PENDING);
		var id = orderOrderedEventPublisher.publish(event);
		assertThat(id)
			.isNotNull()
			.isPresent();
	}
}
