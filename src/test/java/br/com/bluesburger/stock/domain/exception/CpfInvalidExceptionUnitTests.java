package br.com.bluesburger.stock.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.bluesburger.stock.domain.exception.CpfInvalidException;

@ExtendWith(MockitoExtension.class)
class CpfInvalidExceptionUnitTests {

	@Test
	void shouldInstance() {
		assertThat(new CpfInvalidException()).isNotNull();
	}
}
