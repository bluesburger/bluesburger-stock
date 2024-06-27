package br.com.bluesburger.stock.application.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.bluesburger.stock.application.dto.order.StockDto;
import br.com.bluesburger.stock.application.dto.order.StockMapper;
import br.com.bluesburger.stock.domain.exception.OrderNotFoundException;
import br.com.bluesburger.stock.infra.database.StockAdapter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/invoice")
@RequiredArgsConstructor
public class StockController {
	
	private final StockAdapter stockAdapter;
	
	private final StockMapper stockMapper;
	
	@ApiResponses(value = { 
	  @ApiResponse(responseCode = "200", description = "List of stock", 
	    content = { @Content(mediaType = "application/json", 
	      schema = @Schema(allOf = StockDto.class)) })
	})
	@GetMapping
	public List<StockDto> getAll() {
		return stockAdapter.getAll().stream()
			.map(stockMapper::toStockDto)
			.toList();
	}

	@ApiResponses(value = { 
	  @ApiResponse(responseCode = "200", description = "Stock found with id", 
	    content = {@Content(
	    		mediaType = "application/json", 
	    		schema = @Schema(implementation = StockDto.class))}),
	  @ApiResponse(responseCode = "404", description = "Stock not found",
	  	content = { @Content(mediaType = "application/json") })
	})
	@GetMapping("/{stockId}")
	public StockDto getById(@PathVariable Long stockId) {
		return stockAdapter.getById(stockId)
			.map(stockMapper::toStockDto)
			.orElseThrow(OrderNotFoundException::new);
	}
}
