package br.com.bluesburger.stock.infra.database;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import br.com.bluesburger.stock.domain.entity.Status;
import br.com.bluesburger.stock.infra.database.entity.OrderStockEntity;
import br.com.bluesburger.stock.infra.database.entity.ProductEntity;
import br.com.bluesburger.stock.support.ApplicationIntegrationSupport;
import jakarta.transaction.Transactional;

@Transactional
class StockAdapterIntegrationTests extends ApplicationIntegrationSupport {
	
	private static final UUID EXISTANT_ORDER_ID = UUID.fromString("ddedf1ab-0b2f-4766-a9fc-104bedc98492");
	private static final UUID UNEXISTANT_ORDER_ID = UUID.fromString("b166f5c4-4deb-43e1-81d0-693c3af7254e");
	
	@Autowired
	private StockRepository stockRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private StockAdapter stockAdapter;
	
	@AfterEach
	void tearDown() {
		stockRepository.deleteAllInBatch();
		productRepository.deleteAllInBatch();
	}
	
	private ProductEntity saveNewProduct() {
		var productEntity = new ProductEntity(null, "Product XPTO", 100);
		return productRepository.save(productEntity);
	}
	
	private OrderStockEntity saveNewStock() {
		var savedProduct = saveNewProduct();		
		var stockEntity = new OrderStockEntity(null, EXISTANT_ORDER_ID, savedProduct);
		return stockRepository.save(stockEntity);
	}

	@Nested
	class GetAll {
		@Test
		void givenExistantOrderStock_WhenGetAll_ThenShouldReturnList() throws Exception {
			var savedStock = saveNewStock();
			
			assertThat(stockAdapter.getAll())
				.isNotNull()
				.isNotEmpty()
				.hasSize(1)
				.anyMatch(p -> p.equals(savedStock));
		}
		
		@Test
		void givenNoOrderStocks_WhenGetAll_ThenShouldReturnEmptyList() {
			assertThat(stockAdapter.getAll())
				.isNotNull()
				.isEmpty();
		}
	}
	
	@Nested
	class GetAllByStatus {
		@Test
		void givenExistantOrderStock_WhenGetAllByStepWithStatus_ThenShouldReturnInList() {
			var savedStock = saveNewStock();
			
			assertThat(stockAdapter.getAllByStatus(Status.PENDING))
				.isNotNull()
				.isNotEmpty()
				.hasSize(1)
				.anyMatch(p -> p.equals(savedStock));
		}
		
		@Test
		void givenExistantOrderStock_WhenGetAllByStepWithOtherStatus_ThenShouldReturnEmptyList() {
			saveNewStock();
			
			assertThat(stockAdapter.getAllByStatus(Status.RESERVED))
				.isNotNull()
				.isEmpty();
		}
	}
	
	@Nested
	class GetById {
		@Test
		void givenExistantOrderStock_WhenGetById_ThenShouldReturn() {
			var savedStock = saveNewStock();
			
			assertThat(stockAdapter.getById(savedStock.getId()))
				.isNotNull()
				.isNotEmpty()
				.isPresent()
				.get()
				.isEqualTo(savedStock);
		}
		
		@Test
		void givenExistantOrderStock_WhenGetByOtherId_ThenShouldReturnEmptyList() {
			saveNewStock();
			
			assertThat(stockAdapter.getById(99L))
				.isNotNull()
				.isEmpty()
				.isNotPresent();
		}
	}
	
	@Nested
	class UpdateStatus {
		@Test
		void givenExistantOrderStock_WhenUpdateStatus_ThenShouldReturnUpdated() {
			var savedStock = saveNewStock();
			
			assertThat(stockAdapter.updateStatus(savedStock.getId(), Status.RESERVED))
				.isNotNull()
				.isNotEmpty()
				.isPresent()
				.get()
				.hasFieldOrPropertyWithValue("id", savedStock.getId())
				.hasFieldOrPropertyWithValue("status", Status.RESERVED);
		}
		
		@Test
		void givenUnexistantOrderStock_WhenUpdateStatus_ThenShouldReturnEmpty() {
			assertThat(stockAdapter.updateStatus(99L, Status.RESERVED))
				.isNotNull()
				.isEmpty()
				.isNotPresent();
		}
	}
	
	@Nested
	class UpdateStatusByOrderId {
		@Test
		void givenExistantOrderStock_WhenUpdateStatus_ThenShouldReturnUpdated() {
			var savedStock = saveNewStock();
			
			assertThat(stockAdapter.updateStatusByOrderId(savedStock.getOrderId(), Status.RESERVED))
				.isNotNull()
				.isNotEmpty()
				.isPresent()
				.get()
				.hasFieldOrPropertyWithValue("id", savedStock.getId())
				.hasFieldOrPropertyWithValue("status", Status.RESERVED);
		}
		
		@Test
		void givenUnexistantOrderStock_WhenUpdateStatus_ThenShouldReturnEmpty() {
			assertThat(stockAdapter.updateStatusByOrderId(UNEXISTANT_ORDER_ID.toString(), Status.RESERVED))
				.isNotNull()
				.isEmpty()
				.isNotPresent();
		}
	}
	
	@Nested
	class FindFirstByOrderIdByCreatedTimeDesc {
		@Test
		void givenTwoExistantOrderStocks_WhenFindFirstByOrderIdOrderedByCreatedTimeDes_ThenShouldReturnLastSavedStock() {
			var savedStock = saveNewStock();
			var savedStock2 = saveNewStock();
			
			assertThat(stockAdapter.findFirstByOrderIdOrderByCreatedTimeDesc(savedStock.getOrderId()))
				.isNotNull()
				.isNotEmpty()
				.isPresent()
				.get()
				.hasFieldOrPropertyWithValue("id", savedStock2.getId())
				.hasFieldOrPropertyWithValue("status", Status.PENDING);
		}
		
		@Test
		void givenUnexistantOrderStock_WhenFindFirstByOrderId_ThenShouldReturnEmpty() {
			assertThat(stockAdapter.findFirstByOrderIdOrderByCreatedTimeDesc(UNEXISTANT_ORDER_ID.toString()))
				.isNotNull()
				.isEmpty()
				.isNotPresent();
		}
	}
	
	@Nested
	class FindFirstByOrderIdAndStatusOrderByCreatedTimeDesc {
		@Test
		void givenTwoExistantOrderStocks_WhenFindFirstByOrderId_ThenShouldReturnLastSavedStock() {
			var savedStock = saveNewStock();
			var savedStock2 = saveNewStock();
			
			assertThat(stockAdapter.findFirstByOrderIdAndStatusOrderByCreatedTimeDesc(savedStock.getOrderId(), Status.PENDING))
				.isNotNull()
				.isNotEmpty()
				.isPresent()
				.get()
				.hasFieldOrPropertyWithValue("id", savedStock2.getId())
				.hasFieldOrPropertyWithValue("status", Status.PENDING);
		}
		
		@Test
		void givenUnexistantOrderStock_WhenFindFirstByOrderId_ThenShouldReturnEmpty() {
			assertThat(stockAdapter.findFirstByOrderIdAndStatusOrderByCreatedTimeDesc(UNEXISTANT_ORDER_ID.toString(), Status.PENDING))
				.isNotNull()
				.isEmpty()
				.isNotPresent();
		}
	}
	
	@Nested
	class Save {
		@Test
		void whenSaveNewStock_ThenShouldReturnSaved() {
			ProductEntity product = saveNewProduct();
			var saved = stockAdapter.save(new OrderStockEntity(EXISTANT_ORDER_ID.toString(), product));
			assertThat(saved)
				.isNotNull()
				.hasFieldOrProperty("id")
				.hasFieldOrProperty("createdTime")
				.hasFieldOrProperty("updatedTime")
				.hasFieldOrPropertyWithValue("status", Status.PENDING)
				.hasFieldOrPropertyWithValue("product", product);
		}
		
		@Test
		void whenSaveNullStock_ThenShouldThrowError() {
			assertThrows(InvalidDataAccessApiUsageException.class, () -> stockAdapter.save(null));
		}
	}
}
