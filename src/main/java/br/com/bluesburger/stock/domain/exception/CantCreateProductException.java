package br.com.bluesburger.stock.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.NoArgsConstructor;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Impossível criar produto")
@NoArgsConstructor
public class CantCreateProductException extends RuntimeException {

	private static final long serialVersionUID = -4350600952610627028L;
}
