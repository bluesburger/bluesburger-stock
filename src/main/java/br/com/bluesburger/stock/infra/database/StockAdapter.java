package br.com.bluesburger.stock.infra.database;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.bluesburger.stock.domain.entity.Status;
import br.com.bluesburger.stock.domain.service.StockPort;
import br.com.bluesburger.stock.infra.database.entity.OrderStockEntity;
import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(Transactional.TxType.SUPPORTS)
public class StockAdapter implements StockPort {

	private final StockRepository stockRepository;

	public List<OrderStockEntity> getAll() {
		return stockRepository.findAll();
	}
	
	public List<OrderStockEntity> getAllByStep(Status status) {
		return stockRepository.findAllByStatusOrderByCreatedTime(status);
	}

	public Optional<OrderStockEntity> getById(Long stockId) {
		return stockRepository.findById(stockId);
	}

	@Transactional(
			rollbackOn = IllegalArgumentException.class, 
			dontRollbackOn = EntityExistsException.class)
	@Override
	public Optional<OrderStockEntity> updateStatus(Long stockId, Status status) {
		return getById(stockId)
			.map(order -> {
				order.setStatus(status);
				return stockRepository.save(order);
			});
	}
	
	@Override
	public Optional<OrderStockEntity> updateStatusByOrderId(String orderId, Status status) {
		return stockRepository.findFirstByOrderIdAndStatusOrderByCreatedTimeDesc(orderId, Status.PENDING)
			.map(stock -> {
				stock.setStatus(status);
				return stockRepository.save(stock);
			});
	}
	
	@Override
	public Optional<OrderStockEntity> findFirstByOrderIdOrderByCreatedTimeDesc(String orderId) {
		return stockRepository.findFirstByOrderIdOrderByCreatedTimeDesc(orderId);
	}
	
	@Override
	public Optional<OrderStockEntity> findFirstByOrderIdAndStatusOrderByCreatedTimeDesc(String orderId, Status status) {
		return stockRepository.findFirstByOrderIdAndStatusOrderByCreatedTimeDesc(orderId, status);
	}
	
	@Override
	public OrderStockEntity save(OrderStockEntity entity) {
		return stockRepository.save(entity);
	}
}
