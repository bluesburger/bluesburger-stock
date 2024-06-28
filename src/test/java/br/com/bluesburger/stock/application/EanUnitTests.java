package br.com.bluesburger.stock.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EanUnitTests {

	@Test
	void givenPersistedProduct_WhenDefineEan_ShouldValidate() throws Exception {
		Long codigoProduto = 1L;
		String ean = Ean.defineEan(codigoProduto);
		
		assertThat(ean).isNotNull();
		assertThat(Ean.validate(ean)).isTrue();
	}
}
