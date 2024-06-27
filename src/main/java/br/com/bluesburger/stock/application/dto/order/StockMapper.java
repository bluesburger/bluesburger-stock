package br.com.bluesburger.stock.application.dto.order;

import org.springframework.stereotype.Component;

import br.com.bluesburger.stock.infra.database.StockRepository;
import br.com.bluesburger.stock.infra.database.entity.OrderStockEntity;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StockMapper {
	
	private final StockRepository repository;

	public StockDto toStockDto(OrderStockEntity invoice) {
		return new StockDto(invoice.getId(), invoice.getStatus(), invoice.getOrderId());
	}
	
	public OrderStockEntity toStockEntity(StockDto orderDto) {
		return repository.findById(orderDto.getId()).orElse(null);
	}
}
