package br.com.bluesburger.stock.domain.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Stock {

	private String orderId;
	
	private List<StockItem> items;
}
