package br.com.bluesburger.stock.infra.sqs.events;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.fasterxml.jackson.core.JsonProcessingException;

import br.com.bluesburger.stock.application.sqs.events.OrderEvent;
import br.com.bluesburger.stock.infra.sqs.SqsQueueSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public abstract class OrderEventPublisher<T extends OrderEvent> implements IOrderEventPublisher<T> {
	
	private final String queueName;
	
    private final AmazonSQS amazonSQS;
	
	private final SqsQueueSupport<T> sqsQueueSupport;

    @Override
    public Optional<String> publish(T event) {
    	
        try {
        	var request = sqsQueueSupport.createRequest(event, queueName, event.getOrderId());
        	log.info("Publishing event {} in SQS queue {}", event, request.getQueueUrl());
            var result = amazonSQS.sendMessage(request);
            return Optional.ofNullable(result.getMessageId());
        } catch (JsonProcessingException e) {
        	log.error("JsonProcessingException e : {}", e.getMessage(), e);
        } catch (Exception e) {
        	log.error("Exception ocurred while pushing event to sqs : {}", e.getMessage(), e);
        }
        return Optional.empty();
    }
}
