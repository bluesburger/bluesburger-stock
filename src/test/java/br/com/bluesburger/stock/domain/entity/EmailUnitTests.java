package br.com.bluesburger.stock.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.bluesburger.stock.domain.exception.EmailInvalidException;

@ExtendWith(MockitoExtension.class)
class EmailUnitTests {

	@Test
	void shouldInstance() {
		String emailValue = "email-teste@server.com";
		assertThat(new Email(emailValue))
			.hasFieldOrPropertyWithValue("value", emailValue)
			.hasToString(emailValue);
	}
	
	@Test
	void whenInstanceWithNullValue_ThenShouldThrowHandledException() {
		assertThrows(EmailInvalidException.class, () -> new Email(null));
	}
	
	@Test
	void whenInstanceWithInvalidValue_ThenShouldThrowHandledException() {
		String invalidEmailValue = "server.com";
		assertThrows(EmailInvalidException.class, () -> new Email(invalidEmailValue));
	}
}
