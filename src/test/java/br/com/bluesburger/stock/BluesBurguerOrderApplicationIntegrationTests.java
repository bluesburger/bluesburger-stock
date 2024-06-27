package br.com.bluesburger.stock;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import br.com.bluesburger.stock.support.ApplicationIntegrationSupport;

class BluesBurguerOrderApplicationIntegrationTests extends ApplicationIntegrationSupport {

	@Test
	void contextLoad() {
		assertThat(super.hashCode()).isNotZero();
	}
}
