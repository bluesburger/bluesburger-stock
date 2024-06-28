package br.com.bluesburger.stock.application.dto;

import org.springframework.stereotype.Component;

import br.com.bluesburger.stock.infra.database.ProductRepository;
import br.com.bluesburger.stock.infra.database.entity.ProductEntity;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductMapper {
	
	private final ProductRepository repository;

	public ProductDto toDto(ProductEntity entity) {
		return new ProductDto(entity.getId(), entity.getName(), entity.getEan(), entity.getQuantity());
	}
	
	public ProductEntity toStockEntity(ProductDto orderDto) {
		return repository.findById(orderDto.getId()).orElse(null);
	}
}
