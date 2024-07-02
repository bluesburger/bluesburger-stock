package br.com.bluesburger.stock.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CpfInvalidExceptionUnitTests {

	@Test
	void shouldInstance() {
		assertThat(new CpfInvalidException()).isNotNull();
	}
	
	@Test
	void shouldInstanceWithException() {
		var ex = new Exception("Mensagem de erro");
		assertThat(new CpfInvalidException(ex)).isNotNull();
	}
}
