package br.com.bluesburger.stock.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.bluesburger.stock.domain.exception.CpfInvalidException;

@ExtendWith(MockitoExtension.class)
class CpfUnitTests {

	@Test
	void shouldInstance() {
		String cpfValue = "819.788.190-13";
		assertThat(new Cpf(cpfValue))
			.hasFieldOrPropertyWithValue("value", cpfValue)
			.hasToString(cpfValue);
	}
	
	@Test
	void whenInstanceWithNullValue_ThenShouldThrowHandledException() {
		assertThrows(CpfInvalidException.class, () -> new Cpf(null));
	}
}
