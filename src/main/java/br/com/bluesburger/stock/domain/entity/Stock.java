package br.com.bluesburger.stock.domain.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class Stock {

	@NonNull
	private String orderId;
	
	private List<StockItem> items;
}
