package br.com.bluesburger.stock.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StockItem {

	private Long id;
	
	private Integer quantity;
}
