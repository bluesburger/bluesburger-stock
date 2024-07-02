package br.com.bluesburger.stock.application.sqs.events;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.bluesburger.stock.domain.entity.Status;

@ExtendWith(MockitoExtension.class)
class OrderScheduledEventUnitTests {
	
	private UUID ORDER_ID = UUID.fromString("f498e2c7-6cc9-4c61-9a69-5473cadf08c1");

	@Test
	void whenInstanceWithAllParameters_thenShouldHaveParameters() {
		assertThat(new OrderScheduledEvent(ORDER_ID.toString(), Status.PENDING))
			.hasFieldOrPropertyWithValue("orderId", ORDER_ID.toString())
			.hasFieldOrPropertyWithValue("eventName", OrderScheduledEvent.EVENT_NAME);
	}
	
	@Test
	void whenInstanceWithNullOrderId_thenShouldHaveParameters() {
		assertThrows(NullPointerException.class, () -> new OrderScheduledEvent(null, Status.PENDING));
	}
	
	@Test
	void whenInstanceWithNullStatus_thenShouldHaveParameters() {
		assertThrows(NullPointerException.class, () -> new OrderScheduledEvent(ORDER_ID.toString(), null));
	}
}
