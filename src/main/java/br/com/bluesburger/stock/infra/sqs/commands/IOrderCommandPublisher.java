package br.com.bluesburger.stock.infra.sqs.commands;

import java.util.Optional;

import br.com.bluesburger.stock.application.sqs.commands.OrderCommand;

public interface IOrderCommandPublisher<T extends OrderCommand> {

	Optional<String> publish(T command);
}
