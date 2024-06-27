package br.com.bluesburger.stock.application.dto.order;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReserveOrderRequest implements Serializable {
	
	private static final long serialVersionUID = -621830335594903665L;
	
	private String orderId;
	
	private List<OrderItem> items;
}
