package br.com.bluesburger.stock.infra.database;

import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.bluesburger.stock.infra.database.entity.ProductEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(Transactional.TxType.SUPPORTS)
public class ProductAdapter {
	
	private final ProductRepository productRepository;

	public Optional<ProductEntity> findById(Long id) {
		return productRepository.findById(id);
	}
}
