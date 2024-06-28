package br.com.bluesburger.stock.infra.database;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import br.com.bluesburger.stock.domain.entity.Status;
import br.com.bluesburger.stock.infra.database.entity.OrderStockEntity;

public interface StockRepository extends JpaRepository<OrderStockEntity, Long> {

	List<OrderStockEntity> findAllByStatusOrderByCreatedTime(Status step);
	
	Optional<OrderStockEntity> findFirstByOrderIdOrderByCreatedTimeDesc(String orderId);
	
	Optional<OrderStockEntity> findFirstByOrderIdAndStatusOrderByCreatedTimeDesc(String orderId, Status status);
	
	@Modifying
	@Transactional
	@Query(value = "delete from OrderStockEntity o where o.ID = ?1")
	void deleteById(Long id);
}
