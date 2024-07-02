package br.com.bluesburger.stock.application.usecase;

import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.bluesburger.stock.application.dto.order.OrderItem;
import br.com.bluesburger.stock.application.dto.order.ReserveOrderRequest;
import br.com.bluesburger.stock.application.dto.order.ReserveOrderResponse;
import br.com.bluesburger.stock.application.dto.order.ScheduleOrderRequest;
import br.com.bluesburger.stock.application.dto.order.ScheduleOrderResponse;
import br.com.bluesburger.stock.domain.entity.Status;
import br.com.bluesburger.stock.domain.entity.Stock;
import br.com.bluesburger.stock.domain.exception.OrderNotFoundException;
import br.com.bluesburger.stock.infra.database.ProductAdapter;
import br.com.bluesburger.stock.infra.database.StockAdapter;
import br.com.bluesburger.stock.infra.database.entity.OrderStockEntity;
import br.com.bluesburger.stock.infra.database.entity.ProductEntity;
import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockUseCase {
	private final StockAdapter stockAdapter;
	private final ProductAdapter productAdapter;

	@Transactional(dontRollbackOn = EntityExistsException.class)
	public ReserveOrderResponse reserveOrder(ReserveOrderRequest command) {
		var items = command.getItems().stream()
			.map(OrderItem::getId)
			.map(productAdapter::findById)
			.flatMap(Optional::stream)
			.map(ProductEntity::reserve)
			.map(e -> stockAdapter
					.findFirstByOrderIdAndStatusOrderByCreatedTimeDesc(command.getOrderId(), Status.PENDING)
					.orElseGet(() -> new OrderStockEntity(command.getOrderId(), e))
			)
			.map(f -> {
				f.setStatus(Status.RESERVED);
				return f;
			})
			.map(stockAdapter::save)
			.map(saved -> new OrderItem(saved.getId(), saved.getProduct().getQuantity()))
			.toList();
		return new ReserveOrderResponse(command.getOrderId(), items);
	}
	
	@Transactional(dontRollbackOn = EntityExistsException.class)
	public ScheduleOrderResponse scheduleOrder(ScheduleOrderRequest command) {
		return stockAdapter.findFirstByOrderIdAndStatusOrderByCreatedTimeDesc(command.getOrderId(), Status.RESERVED)
			.map(OrderStockEntity::schedule)
			.map(stockAdapter::save)
			.map(e -> new ScheduleOrderResponse(e.getOrderId(), e.getStatus()))
			.orElseThrow(OrderNotFoundException::new);
	}
	
	@Transactional
	public Optional<Stock> cancelOrder(String orderId) {
		return stockAdapter.findFirstByOrderIdOrderByCreatedTimeDesc(orderId)
				.map(stock -> {
					stock.setStatus(Status.CANCELED);
					return stockAdapter.save(stock);
				})
				.map(stock -> new Stock(stock.getOrderId()));
	}
}
