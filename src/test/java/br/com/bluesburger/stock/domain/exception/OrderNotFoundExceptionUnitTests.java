package br.com.bluesburger.stock.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderNotFoundExceptionUnitTests {

	@Test
	void shouldInstance() {
		assertThat(new OrderNotFoundException()).isNotNull();
	}
}
