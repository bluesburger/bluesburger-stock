package br.com.bluesburger.stock.application.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.bluesburger.stock.domain.entity.Status;
import br.com.bluesburger.stock.infra.database.ProductRepository;
import br.com.bluesburger.stock.infra.database.StockRepository;
import br.com.bluesburger.stock.infra.database.entity.OrderStockEntity;
import br.com.bluesburger.stock.infra.database.entity.ProductEntity;
import br.com.bluesburger.stock.support.ApplicationIntegrationSupport;
import jakarta.transaction.Transactional;

@Transactional
class StockMapperIntegrationTests extends ApplicationIntegrationSupport {
	
	private UUID ORDER_ID = UUID.fromString("f498e2c7-6cc9-4c61-9a69-5473cadf08c1");
	
	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private StockRepository stockRepository;
	
	@Autowired
	private StockMapper stockMapper;
	
	private ProductEntity saveNewProduct() {
		var newProduct = new ProductEntity("Product XPTO", 1);
		return productRepository.save(newProduct);
	}
	
	private OrderStockEntity saveNewStock() {
		var product = saveNewProduct();
		var stockEntity = new OrderStockEntity(ORDER_ID.toString(), product);
		return stockRepository.save(stockEntity);
	}
	
	@Test
	void givenExistantStock_WhenToStockDto_ThenShouldReturnDto() {
		// given
		var savedStock = saveNewStock();
		
		// when
		var dto = stockMapper.toStockDto(savedStock);
		
		// then
		assertThat(dto).isNotNull()
			.hasFieldOrPropertyWithValue("id", savedStock.getId())
			.hasFieldOrPropertyWithValue("status", savedStock.getStatus())
			.hasFieldOrPropertyWithValue("orderId", savedStock.getOrderId());
	}
	
	@Test
	void givenExistantStock_WhenToStockEntity_ThenShouldReturnEntity() {
		// given
		var savedStock = saveNewStock();
		
		// when
		var stockEntity = stockMapper.toStockEntity(new StockDto(savedStock.getId(), Status.PENDING, ORDER_ID.toString()));
		
		// then
		assertThat(stockEntity).isNotNull()
			.hasFieldOrPropertyWithValue("id", savedStock.getId());
	}
}
