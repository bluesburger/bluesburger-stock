package br.com.bluesburger.stock.infra.database;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.bluesburger.stock.application.dto.CreateStockProduct;
import br.com.bluesburger.stock.infra.database.entity.ProductEntity;
import br.com.bluesburger.stock.support.ApplicationIntegrationSupport;
import jakarta.transaction.Transactional;

@Transactional
class ProductAdapterIntegrationTests extends ApplicationIntegrationSupport {
	
	@Autowired
	private StockRepository stockRepository;

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private ProductAdapter productAdapter;
	
	@AfterEach
	void tearDown() {
		stockRepository.deleteAllInBatch();
		productRepository.deleteAllInBatch();
//		TimeUnit.MILLISECONDS.sleep(100L);
	}
	
	@Test
	void givenExistantProduct_WhenFindById_ThenShouldReturnProduct() {
		var productEntity = new ProductEntity(null, "Product XPTO 1", 100);
		var savedProduct = productRepository.save(productEntity);
		
		assertThat(productAdapter.findById(savedProduct.getId()))
			.isNotNull()
			.isPresent()
			.get()
			.hasFieldOrPropertyWithValue("id", savedProduct.getId());
	}
	
	@Test
	void givenExistantProduct_WhenGetAll_ThenShouldReturnListWithProduct() {
		var productEntity = new ProductEntity(null, "Product XPTO 2", 100);
		productRepository.save(productEntity);
		
		assertThat(productAdapter.getAll())
			.isNotNull()
			.isNotEmpty();
	}
	
	@Test
	void givenExistantProduct_WhenGetById_ThenShouldReturnProduct() {
		var productEntity = new ProductEntity(null, "Product XPTO 3", 100);
		var savedProduct = productRepository.save(productEntity);
		
		assertThat(productAdapter.getById(savedProduct.getId()))
			.isNotNull()
			.isNotEmpty()
			.isPresent()
			.get()
			.hasFieldOrPropertyWithValue("id", savedProduct.getId());
	}
	
	@Test
	void givenExistantProduct_WhenGetByEan_ThenShouldReturnProduct() {
		var productEntity = new ProductEntity(null, "Product XPTO 4", 100);
		var savedProduct = productRepository.save(productEntity);
		
		assertThat(productAdapter.getByEan(savedProduct.getEan()))
			.isNotNull()
			.isNotEmpty()
			.isPresent()
			.get()
			.hasFieldOrPropertyWithValue("id", savedProduct.getId());
	}
	
	@Test
	void whenCreateStockProduct_ThenShouldReturnProduct() {
		
		assertThat(productAdapter.create(new CreateStockProduct("Produto QWERTY 5", 50)))
			.isNotNull()
			.hasFieldOrProperty("id")
			.hasFieldOrProperty("createdTime")
			.hasFieldOrProperty("updatedTime")
			.hasFieldOrPropertyWithValue("name", "Produto QWERTY 5")
			.hasFieldOrProperty("ean")
			.hasFieldOrPropertyWithValue("quantity", 50);
	}
}
