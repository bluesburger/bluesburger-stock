package br.com.bluesburger.stock.application.sqs.commands;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.bluesburger.stock.application.dto.order.OrderItem;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class OrderStockCommand implements OrderCommand {

	@NonNull
	@JsonProperty
	@Getter
	private String orderId;
	
	@NonNull
	@JsonProperty
	@Getter
	private List<OrderItem> items;
}
