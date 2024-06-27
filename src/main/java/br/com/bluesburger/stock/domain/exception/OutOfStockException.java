package br.com.bluesburger.stock.domain.exception;

public class OutOfStockException extends RuntimeException {

	private static final long serialVersionUID = 5601438410667427236L;

	public OutOfStockException() {
		super();
	}
	
	public OutOfStockException(Exception e) {
		super(e);
	}
}
