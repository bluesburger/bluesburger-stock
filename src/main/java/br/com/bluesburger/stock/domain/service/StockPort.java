package br.com.bluesburger.stock.domain.service;

import java.util.List;
import java.util.Optional;

import br.com.bluesburger.stock.domain.entity.Status;
import br.com.bluesburger.stock.infra.database.entity.OrderStockEntity;

public interface StockPort {
	
	List<OrderStockEntity> getAll();

	List<OrderStockEntity> getAllByStatus(Status step);
	
	Optional<OrderStockEntity> getById(Long stockId);

	Optional<OrderStockEntity> updateStatus(Long stockId, Status fase);
	
	Optional<OrderStockEntity> updateStatusByOrderId(String orderId, Status status);
	
	Optional<OrderStockEntity> findFirstByOrderIdOrderByCreatedTimeDesc(String orderId);
	
	Optional<OrderStockEntity> findFirstByOrderIdAndStatusOrderByCreatedTimeDesc(String orderId, Status status);
	
	OrderStockEntity save(OrderStockEntity entity);
}
