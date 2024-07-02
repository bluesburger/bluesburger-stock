package br.com.bluesburger.stock.infra.database.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.bluesburger.stock.domain.exception.OutOfStockException;

@ExtendWith(MockitoExtension.class)
class ProductEntityUnitTests {
	
	@Test
	void givenAProduct_WhenReserve_ThenShouldDecrementQuantity() {
		assertThat(new ProductEntity(1L, "Product XPTO", 100).reserve())
			.hasFieldOrPropertyWithValue("quantity", 99);
	}
	
	@Test
	void givenAProductWithZeroQuantity_WhenReserve_ThenShouldThrowException() {
		assertThrows(OutOfStockException.class, () -> new ProductEntity(1L, "Product XPTO", 0).reserve());
	}
	
	@Test
	void givenAProduct_WhenDefineEan_ThenShouldHaveValidValue() throws Exception {
		var entity = new ProductEntity(1L, "Product XPTO", 100);
		entity.defineEan();
		assertThat(entity)
			.hasFieldOrProperty("ean");
	}
	
	@Test
	void whenInstanceProductWithZeroQuantity_ThenShouldHaveValidInstance() {
		// when
		assertThat(new ProductEntity(1L, "Product XPTO", 0))
			.hasFieldOrPropertyWithValue("id", 1L)
			.hasFieldOrPropertyWithValue("name", "Product XPTO")
			.hasFieldOrPropertyWithValue("quantity", 0);
	}
	
	@Test
	void givenAProductWithNullId_WhenDefineEan_ThenShouldHaveValidValue() throws Exception {
		var entity = new ProductEntity(null, "Product XPTO", 100);
		assertThrows(IllegalArgumentException.class, () -> entity.defineEan());
	}
	
	@Test
	void givenAProductWithZeroId_WhenDefineEan_ThenShouldHaveValidValue() throws Exception {
		var entity = new ProductEntity(0L, "Product XPTO", 100);
		assertThrows(IllegalArgumentException.class, () -> entity.defineEan());
	}
	
	@Test
	void givenAProductWithNegativeId_WhenDefineEan_ThenShouldHaveValidValue() throws Exception {
		var entity = new ProductEntity(-1L, "Product XPTO", 100);
		assertThrows(IllegalArgumentException.class, () -> entity.defineEan());
	}

	@Test
	void whenInstanceProductWithNullName_ThenShouldThrowException() {
		// when
		assertThrows(NullPointerException.class, () -> new ProductEntity(null, 100));
	}
	
	@Test
	void whenInstanceProductWithNullQuantity_ThenShouldThrowException() {
		// when
		assertThrows(NullPointerException.class, () -> new ProductEntity("Product XPTO", null));
	}
	
	@Test
	void whenInstanceProductWithEmptyName_ThenShouldThrowException() {
		// when
		assertThrows(IllegalArgumentException.class, () -> new ProductEntity("", 100));
	}
	
	@Test
	void whenInstanceProductWithNegativeQuantity_ThenShouldThrowException() {
		// when
		assertThrows(IllegalArgumentException.class, () -> new ProductEntity("Product XPTO", -1));
	}
}
