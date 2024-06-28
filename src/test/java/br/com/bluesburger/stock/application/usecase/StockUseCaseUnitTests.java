package br.com.bluesburger.stock.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.bluesburger.stock.application.dto.order.OrderItem;
import br.com.bluesburger.stock.application.dto.order.ReserveOrderRequest;
import br.com.bluesburger.stock.application.dto.order.ScheduleOrderRequest;
import br.com.bluesburger.stock.domain.entity.Status;
import br.com.bluesburger.stock.infra.database.ProductAdapter;
import br.com.bluesburger.stock.infra.database.StockAdapter;
import br.com.bluesburger.stock.infra.database.entity.OrderStockEntity;
import br.com.bluesburger.stock.infra.database.entity.ProductEntity;

@ExtendWith(MockitoExtension.class)
class StockUseCaseUnitTests {

	@Mock
	StockAdapter stockAdapter;
	
	@Mock
	ProductAdapter productAdapter;
	
	@InjectMocks
	StockUseCase stockUseCase;

	@Test
	void givenUnexistantPendingOrderWithTwoItems_WhenRequestReserveOrder_ThenShouldDecrementStockQuantity() throws Exception {
		
		// given
		var orderId = "qwerty-asdfgh-zxcvbn-yuiopl";
		var items = List.of(new OrderItem(1L), new OrderItem(2L));
		var command = new ReserveOrderRequest(orderId, items);
		
		int quantityProduct1 = 100, quantityProduct2 = 50;
		var product1 = new ProductEntity("Produto 1", quantityProduct1);
		var product2 = new ProductEntity("Produto 2", quantityProduct2);
		
		when(productAdapter.findById(1L)).thenReturn(Optional.of(product1));
		when(productAdapter.findById(2L)).thenReturn(Optional.of(product2));
		
		when(stockAdapter.findFirstByOrderIdAndStatusOrderByCreatedTimeDesc(orderId, Status.PENDING))
			.thenReturn(Optional.empty());
		
		when(stockAdapter.save(any(OrderStockEntity.class)))
			.thenReturn(
					new OrderStockEntity(1L, null, null, Status.RESERVED, orderId, product1), 
					new OrderStockEntity(2L, null, null, Status.RESERVED, orderId, product2)
			);
		
		// when
		var stocks = stockUseCase.reserveOrder(command);
		
		// then
		assertThat(stocks).hasFieldOrPropertyWithValue("orderId", orderId);
		assertThat(stocks.getItems()).isNotEmpty().hasSize(2);
		assertThat(stocks.getItems().get(0)).hasFieldOrPropertyWithValue("quantity", --quantityProduct1);
		assertThat(stocks.getItems().get(1)).hasFieldOrPropertyWithValue("quantity", --quantityProduct2);
		
		verify(productAdapter).findById(items.get(0).getId());
		verify(productAdapter).findById(items.get(1).getId());
		verify(stockAdapter, times(2)).findFirstByOrderIdAndStatusOrderByCreatedTimeDesc(orderId, Status.PENDING);
		verify(stockAdapter, times(2)).save(any(OrderStockEntity.class));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	void givenExistantPendingOrderWithTwoItems_WhenRequestReserveOrder_ThenShouldDecrementStockQuantity() throws Exception {
		
		// given
		var orderId = "qwerty-asdfgh-zxcvbn-yuiopl";
		var items = List.of(new OrderItem(1L), new OrderItem(2L));
		var command = new ReserveOrderRequest(orderId, items);
		
		int quantityProduct1 = 100, quantityProduct2 = 50;
		var product1 = new ProductEntity("Produto 1", quantityProduct1);
		var product2 = new ProductEntity("Produto 2", quantityProduct2);
		
		when(productAdapter.findById(1L)).thenReturn(Optional.of(product1));
		when(productAdapter.findById(2L)).thenReturn(Optional.of(product2));
		
		var entity1 = new OrderStockEntity(1L, null, null, Status.RESERVED, orderId, product1);
		var entity2 = new OrderStockEntity(2L, null, null, Status.RESERVED, orderId, product2);
		when(stockAdapter.findFirstByOrderIdAndStatusOrderByCreatedTimeDesc(orderId, Status.PENDING))
			.thenReturn(Optional.of(entity1), Optional.of(entity2));
		
		when(stockAdapter.save(any(OrderStockEntity.class)))
			.thenReturn(entity1, entity2);
		
		// when
		var stocks = stockUseCase.reserveOrder(command);
		
		// then
		assertThat(stocks).hasFieldOrPropertyWithValue("orderId", orderId);
		assertThat(stocks.getItems()).isNotEmpty().hasSize(2);
		assertThat(stocks.getItems().get(0)).hasFieldOrPropertyWithValue("quantity", --quantityProduct1);
		assertThat(stocks.getItems().get(1)).hasFieldOrPropertyWithValue("quantity", --quantityProduct2);
		
		verify(productAdapter).findById(items.get(0).getId());
		verify(productAdapter).findById(items.get(1).getId());
		verify(stockAdapter, times(2)).findFirstByOrderIdAndStatusOrderByCreatedTimeDesc(orderId, Status.PENDING);
		verify(stockAdapter, times(2)).save(any(OrderStockEntity.class));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	void givenExistantReservedOrder_WhenRequestScheduledOrder_ThenShouldSaveScheduledOrder() throws Exception {
		// given
		var orderId = "qwerty-asdfgh-zxcvbn-yuiopl";
		var command = new ScheduleOrderRequest(orderId);
		
		int quantityProduct1 = 100, quantityProduct2 = 50;
		var product1 = new ProductEntity("Produto 1", quantityProduct1);
		var product2 = new ProductEntity("Produto 2", quantityProduct2);
		
		var stockEntity = new OrderStockEntity(1L, null, null, Status.SCHEDULED, orderId, product1);
		var stockEntity2 = new OrderStockEntity(2L, null, null, Status.SCHEDULED, orderId, product2);
		when(stockAdapter.findFirstByOrderIdAndStatusOrderByCreatedTimeDesc(orderId, Status.RESERVED))
			.thenReturn(Optional.of(stockEntity), Optional.of(stockEntity2));
		
		when(stockAdapter.save(any(OrderStockEntity.class)))
			.thenReturn(
					new OrderStockEntity(1L, null, null, Status.SCHEDULED, orderId, product1), 
					new OrderStockEntity(2L, null, null, Status.SCHEDULED, orderId, product2)
			);
		
		// when
		var stocks = stockUseCase.scheduleOrder(command);
		
		// then
		assertThat(stocks).hasFieldOrPropertyWithValue("orderId", orderId);
		assertThat(stocks).hasFieldOrPropertyWithValue("status", Status.SCHEDULED);
		
		verify(stockAdapter).findFirstByOrderIdAndStatusOrderByCreatedTimeDesc(orderId, Status.RESERVED);
		verify(stockAdapter).save(any(OrderStockEntity.class));
	}
	
	@Test
	void givenExistantOrder_WhenRequestCancelOrder_ThenShouldUpdateStockAsCanceled() throws Exception {
		// given
		var orderId = "qwerty-asdfgh-zxcvbn-yuiopl";
		
		int quantityProduct1 = 100;
		var product1 = new ProductEntity("Produto 1", quantityProduct1);
		var stockEntity = new OrderStockEntity(1L, null, null, Status.SCHEDULED, orderId, product1);
		when(stockAdapter.findFirstByOrderIdOrderByCreatedTimeDesc(orderId))
			.thenReturn(Optional.of(stockEntity));
		when(stockAdapter.save(any(OrderStockEntity.class)))
			.thenReturn(stockEntity.withStatus(Status.CANCELED));
		
		// when
		var optionalStock = stockUseCase.cancelOrder(orderId);
		
		// then
		assertThat(optionalStock).isPresent();
		assertThat(optionalStock).get()
			.hasFieldOrPropertyWithValue("orderId", orderId);
	}
}
