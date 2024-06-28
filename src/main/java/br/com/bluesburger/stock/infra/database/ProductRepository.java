package br.com.bluesburger.stock.infra.database;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import br.com.bluesburger.stock.infra.database.entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
	
	Optional<ProductEntity> findFirstByName(String name);
	
	Optional<ProductEntity> findFirstByEan(String ean);
	
	@Modifying
	@Transactional
	@Query(value = "delete from ProductEntity o where o.ID = ?1")
	void deleteById(Long id);
}
