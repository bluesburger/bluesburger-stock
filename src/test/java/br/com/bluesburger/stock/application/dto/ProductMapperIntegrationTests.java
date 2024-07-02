package br.com.bluesburger.stock.application.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.bluesburger.stock.infra.database.ProductRepository;
import br.com.bluesburger.stock.infra.database.entity.ProductEntity;
import br.com.bluesburger.stock.support.ApplicationIntegrationSupport;
import jakarta.transaction.Transactional;

@Transactional
class ProductMapperIntegrationTests extends ApplicationIntegrationSupport {

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private ProductMapper productMapper;
	
	private ProductEntity saveNewProduct() {
		var productEntity = new ProductEntity(null, "Product XPTO", 100);
		return productRepository.save(productEntity);
	}
	
	@Test
	void givenProductEntity_WhenToDto_ThenShouldReturnDto() {
		var savedProduct = saveNewProduct();
		assertThat(productMapper.toDto(savedProduct))
			.hasFieldOrPropertyWithValue("id", savedProduct.getId())
			.hasFieldOrPropertyWithValue("name", savedProduct.getName())
			.hasFieldOrPropertyWithValue("ean", savedProduct.getEan())
			.hasFieldOrPropertyWithValue("quantity", savedProduct.getQuantity());
	}
	
	@Test
	void givenExistantProduct_WhenDtoToStockEntity_ThenShouldReturnEntity() {
		var savedProduct = saveNewProduct();
		
		var dto = new ProductDto(savedProduct.getId(), "Product QWERTY", "ean", 2);
		assertThat(productMapper.toStockEntity(dto))
			.isNotNull()
			.isEqualTo(savedProduct);
	}
	
	@Test
	void givenUnexistantProduct_WhenDtoToStockEntity_ThenShouldReturnNull() {
		
		var dto = new ProductDto(123L, "Product QWERTY2", "ean", 1);
		assertThat(productMapper.toStockEntity(dto))
			.isNull();
	}
}
