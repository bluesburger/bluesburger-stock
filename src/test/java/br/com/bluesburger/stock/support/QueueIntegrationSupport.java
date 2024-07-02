package br.com.bluesburger.stock.support;

import org.springframework.test.context.TestPropertySource;

@TestPropertySource(
		properties = { 
			"cloud.aws.endpoint.uri=http://localhost:1234",
			"cloud.aws.accountId=1234567890"
		})
public class QueueIntegrationSupport extends ApplicationIntegrationSupport {

}
