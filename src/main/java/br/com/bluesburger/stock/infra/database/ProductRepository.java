package br.com.bluesburger.stock.infra.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import br.com.bluesburger.stock.infra.database.entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
	
	@Modifying
	@Transactional
	@Query(value = "delete from ProductEntity o where o.ID = ?1")
	void deleteById(Long id);
}
