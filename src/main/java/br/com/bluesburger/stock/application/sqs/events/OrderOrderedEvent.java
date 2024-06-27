package br.com.bluesburger.stock.application.sqs.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.bluesburger.stock.domain.entity.Status;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderOrderedEvent extends OrderEvent {
	
	public static final String EVENT_NAME = "ORDER_ORDERED_EVENT";

	private static final long serialVersionUID = 7702500048926979660L;
	
	@JsonCreator
	public OrderOrderedEvent(@JsonProperty("orderId") String orderId, @JsonProperty("status") Status status) {
		super(orderId, status);
	}
	
	@Override
	public String getEventName() {
		return EVENT_NAME;
	}
}
