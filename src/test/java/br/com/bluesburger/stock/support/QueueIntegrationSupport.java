package br.com.bluesburger.stock.support;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.localstack.LocalStackContainer.Service;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import lombok.extern.slf4j.Slf4j;

//@TestPropertySource(
//		properties = { 
//			"cloud.aws.endpoint.uri=http://localhost:1234",
//			"cloud.aws.accountId=1234567890"
//		})
@Slf4j
@Import(SqsTestConfig.class)
@Testcontainers
@DirtiesContext
public class QueueIntegrationSupport extends ApplicationIntegrationSupport {

	@ClassRule
	public static final DockerImageName LOCALSTACK_IMG_NAME = 
			DockerImageName.parse("localstack/localstack:3.0");
	
	private static Network network = Network.newNetwork();
	
	@Container
	static LocalStackContainer localstackInDockerNetwork = new LocalStackContainer(
			LOCALSTACK_IMG_NAME
		)
			.withEnv("DEBUG", "1")
			.withEnv("SQS_ENDPOINT_STRATEGY", "domain")
			.withEnv("LOCALSTACK_HOST", "127.0.0.1")
		    .withNetwork(network)
		    .withNetworkAliases("notthis", "localstack")
			.withServices(Service.SQS);
	
	static final String BUCKET_NAME = UUID.randomUUID().toString();
	
	protected static List<String> TEST_QUEUES = List.of(
			"d29e1ed0-fc99-48d9-81da-6c092651e5e0", 
			"48905674-4e2e-4503-a49d-15c7306f0fe1", 
			"d98c39f7-5118-43ba-afc9-027aa2809d53",
			"ae04dd1e-7d6a-4899-bb82-5610692e753f",
			"7aa41475-7aaa-47ad-964d-72794e734849"
	);
	
	@DynamicPropertySource
	static void overrideProperties(DynamicPropertyRegistry registry) {
		registry.add("cloud.aws.sqs.listener.auto-startup", () -> "true");
		registry.add("cloud.aws.region.static", localstackInDockerNetwork.getRegion()::toString);
	    registry.add("cloud.aws.credentials.access-key", localstackInDockerNetwork.getAccessKey()::toString);
	    registry.add("cloud.aws.credentials.secret-key", localstackInDockerNetwork.getSecretKey()::toString);
	    registry.add("cloud.aws.end-point.uri", localstackInDockerNetwork.getEndpointOverride(Service.SQS)::toString);
	    
	    registry.add("queue-order-stock-command", () -> TEST_QUEUES.get(0));
	    registry.add("queue-schedule-order-command", () -> TEST_QUEUES.get(1));
	    registry.add("queue-cancel-order-stock-command", () -> TEST_QUEUES.get(2));
	    registry.add("queue.order.ordered-event", () -> TEST_QUEUES.get(3));
	    registry.add("queue.order.scheduled-event", () -> TEST_QUEUES.get(4));
	}

	@BeforeAll
	static void beforeAll() throws IOException, InterruptedException {
		TEST_QUEUES.forEach(queueName -> {
			try {
				log.info("Creating queue {}", queueName);
				localstackInDockerNetwork.execInContainer(
						"awslocal",
						"sqs",
						"create-queue",
						"--queue-name",
						queueName
						// , "--attributes FifoQueue=true"
				);
			} catch (UnsupportedOperationException | IOException | InterruptedException e) {
				log.error("Falha ao tentar criar queue {}", queueName, e);
			}
		});
		
		try {
			log.info("Listando queues");
			localstackInDockerNetwork.execInContainer(
					"awslocal",
					"sqs",
					"list-queues"
			);
		} catch (UnsupportedOperationException | IOException | InterruptedException e) {
			log.error("Falha ao tentar listar queues", e);
		}
	}
	
	public Integer getPort() {
		return localstackInDockerNetwork.getFirstMappedPort();
	}
}
