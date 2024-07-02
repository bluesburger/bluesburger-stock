package br.com.bluesburger.stock.infra.database.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.bluesburger.stock.domain.entity.Status;

@ExtendWith(MockitoExtension.class)
class OrderStockEntityUnitTests {
	
	private static final UUID ORDER_ID = UUID.fromString("ddedf1ab-0b2f-4766-a9fc-104bedc98492");
	
	private ProductEntity product;
	
	@BeforeEach
	void setUp() {
		product = new ProductEntity(1L, "Produto XPTO", 50);
	}
	
	@Test
	void givenOneProduct_WhenReserveNewStock_ThenShouldChangeStatusAndReserveProduct() {
		// given
		var newStock = new OrderStockEntity(Status.PENDING, ORDER_ID, product);
		
		// when
		var reservedStock = newStock.reserve();
		
		// then
		assertThat(reservedStock)
			.hasFieldOrPropertyWithValue("status", Status.RESERVED);
		assertThat(reservedStock.getProduct())
			.hasFieldOrPropertyWithValue("quantity", 49);
	}
	
	@Test
	void givenOneProduct_WhenReserveExistantStock_ThenShouldChangeStatusAndReserveProduct() {
		// given
		var existantStock = new OrderStockEntity(1L, Status.PENDING, ORDER_ID, product);
		
		// when
		var reservedStock = existantStock.reserve();
		
		// then
		assertThat(reservedStock)
			.hasFieldOrPropertyWithValue("status", Status.RESERVED);
		assertThat(reservedStock.getProduct())
			.hasFieldOrPropertyWithValue("quantity", 49);
	}
	
	@Test
	void givenOneProduct_WhenScheduleExistantStock_ThenShouldChangeStatus() {
		// given
		var existantStock = new OrderStockEntity(1L, Status.PENDING, ORDER_ID, product);
		
		// when
		var reservedStock = existantStock.reserve();
		var scheduledStock = reservedStock.schedule();
		
		// then
		assertThat(scheduledStock)
			.hasFieldOrPropertyWithValue("status", Status.SCHEDULED);
		assertThat(scheduledStock.getProduct())
			.hasFieldOrPropertyWithValue("quantity", 49);
	}
	
	@Test
	void whenInstanceStockWithoutStatus_ThenShouldDefineDefaultStatus() {
		// given
		var newStock = new OrderStockEntity(null, ORDER_ID, product);
		
		// then
		assertThat(newStock)
			.isNotNull()
			.hasFieldOrPropertyWithValue("status", Status.PENDING);
	}
	
	@Test
	void givenProductWithNullId_WhenInstanceStock_ThenShouldThrowException() {
		var unexistantProduct = new ProductEntity(null, "Unexistant Product", 1000);
		// given
		assertThrows(IllegalArgumentException.class, () -> new OrderStockEntity(1L, Status.PENDING, ORDER_ID, unexistantProduct));
	}
	
	@Test
	void givenProductWithZeroId_WhenInstanceStock_ThenShouldThrowException() {
		var unexistantProduct = new ProductEntity(0L, "Unexistant Product", 1000);
		// given
		assertThrows(IllegalArgumentException.class, () -> new OrderStockEntity(1L, Status.PENDING, ORDER_ID, unexistantProduct));
	}
	
	@Test
	void givenProductWithNegativeId_WhenInstanceStock_ThenShouldThrowException() {
		long productId = -1;
		var unexistantProduct = new ProductEntity(productId, "Unexistant Product", 1000);
		// given
		assertThrows(IllegalArgumentException.class, () -> new OrderStockEntity(1L, Status.PENDING, ORDER_ID, unexistantProduct));
	}
	
	@Test
	void givenPendingStock_WhenScheduleBeforeReserveStock_ThenShouldThrowException() {
		// given
		var existantStock = new OrderStockEntity(1L, Status.PENDING, ORDER_ID, product);
		
		// when/then
		assertThrows(IllegalStateException.class, () -> existantStock.schedule());
	}
	
	@Test
	void whenTryInstanceStockWithoutOrderId_ThenShouldThrowException() {
		assertThrows(NullPointerException.class, () -> new OrderStockEntity(Status.PENDING, null, product));
	}
	
	@Test
	void whenTryInstanceStockWithoutProduct_ThenShouldThrowException() {
		assertThrows(NullPointerException.class, () -> new OrderStockEntity(Status.PENDING, ORDER_ID, null));
	}
	
	@Test
	void whenTryInstanceStockWithoutOrderIdAndProduct_ThenShouldThrowException() {
		assertThrows(NullPointerException.class, () -> new OrderStockEntity(Status.PENDING, null, null));
	}
}
