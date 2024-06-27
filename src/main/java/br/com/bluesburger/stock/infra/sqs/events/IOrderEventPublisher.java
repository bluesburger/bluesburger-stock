package br.com.bluesburger.stock.infra.sqs.events;

import java.util.Optional;

import br.com.bluesburger.stock.application.sqs.events.OrderEvent;

public interface IOrderEventPublisher<T extends OrderEvent> {

	Optional<String> publish(T event);
}
