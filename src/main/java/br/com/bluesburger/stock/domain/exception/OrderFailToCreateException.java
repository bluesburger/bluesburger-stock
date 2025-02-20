package br.com.bluesburger.stock.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Erro ao criar pedido")
public class OrderFailToCreateException extends RuntimeException {

	private static final long serialVersionUID = -4350600952610627028L;
}
