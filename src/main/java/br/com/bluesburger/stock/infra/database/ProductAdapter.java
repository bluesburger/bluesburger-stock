package br.com.bluesburger.stock.infra.database;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.bluesburger.stock.application.dto.CreateStockProduct;
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
	
	public List<ProductEntity> getAll() {
		return productRepository.findAll();
	}
	
	public Optional<ProductEntity> getById(Long productId) {
		return productRepository.findById(productId);
	}
	
	public Optional<ProductEntity> getByEan(String ean) {
		return productRepository.findFirstByEan(ean);
	}
	
	public ProductEntity create(CreateStockProduct command) {
		return productRepository.findFirstByName(command.getName())
			.orElseGet(() -> {
				var entity = new ProductEntity(command.getName(), command.getQuantity());
				return productRepository.save(entity);
			});
	}
}
