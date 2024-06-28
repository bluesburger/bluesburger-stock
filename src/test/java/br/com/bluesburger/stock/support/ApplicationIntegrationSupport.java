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
				"cloud.aws.credentials.access-key=AKIAIOSFODNN7EXAMPLE",
				"cloud.aws.credentials.secret-key=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY",
				"spring.main.allow-bean-definition-overriding=true",
				"spring.cloud.bus.enabled=false",
				"spring.cloud.consul.enabled=false", 
				"spring.cloud.consul.discovery.enabled=false",
				"cloud.aws.region.use-default-aws-region-chain=true",
				"cloud.aws.stack.auto=false",
				"cloud.aws.region.auto=false",
				"cloud.aws.stack=false",
				"cloud.aws.sqs.listener.auto-startup=false"
		},
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles({ "test" })
@ContextConfiguration(classes = BluesBurguerStockApplication.class)
public class ApplicationIntegrationSupport {

	static {
		System.setProperty("aws.region", "us-east-1");
	}
}
