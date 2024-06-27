package br.com.bluesburger.stock.support;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import br.com.bluesburger.stock.BluesBurguerStockApplication;

@TestPropertySource("classpath:application-test.properties")
@SpringBootTest(
		classes = { BluesBurguerStockApplication.class },
		properties = { 
				"spring.main.allow-bean-definition-overriding=true",
				"spring.cloud.bus.enabled=false",
				"spring.cloud.consul.enabled=false", 
				"spring.cloud.consul.discovery.enabled=false"
		},
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles({ "test" })
@ContextConfiguration(classes = BluesBurguerStockApplication.class)
public class ApplicationIntegrationSupport {

}
