package br.com.bluesburger.stock.application.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.bluesburger.stock.application.dto.CreateStockProduct;
import br.com.bluesburger.stock.application.dto.ProductDto;
import br.com.bluesburger.stock.application.dto.ProductMapper;
import br.com.bluesburger.stock.application.dto.StockDto;
import br.com.bluesburger.stock.domain.exception.CantCreateProductException;
import br.com.bluesburger.stock.domain.exception.OrderNotFoundException;
import br.com.bluesburger.stock.infra.database.ProductAdapter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
	
	private final ProductAdapter adapter;
	
	private final ProductMapper mapper;
	
	@ApiResponses(value = { 
	  @ApiResponse(responseCode = "200", description = "List of stock", 
	    content = { @Content(mediaType = "application/json", 
	      schema = @Schema(allOf = StockDto.class)) })
	})
	@GetMapping
	public List<ProductDto> getAll() {
		return adapter.getAll().stream()
			.map(mapper::toDto)
			.toList();
	}

	@ApiResponses(value = { 
	  @ApiResponse(responseCode = "200", description = "Product found with id", 
	    content = {@Content(
	    		mediaType = "application/json", 
	    		schema = @Schema(implementation = StockDto.class))}),
	  @ApiResponse(responseCode = "404", description = "Product not found",
	  	content = { @Content(mediaType = "application/json") })
	})
	@GetMapping("/{productId}")
	public ProductDto getById(@PathVariable Long productId) {
		return adapter.getById(productId)
			.map(mapper::toDto)
			.orElseThrow(OrderNotFoundException::new);
	}
	
	@ApiResponses(value = { 
	  @ApiResponse(responseCode = "200", description = "Product found with id", 
	    content = {@Content(
	    		mediaType = "application/json", 
	    		schema = @Schema(implementation = StockDto.class))}),
	  @ApiResponse(responseCode = "404", description = "Product not found",
	  	content = { @Content(mediaType = "application/json") })
	})
	@GetMapping("/query")
	public ProductDto getByProperties(@RequestParam String ean) {
		return adapter.getByEan(ean)
			.map(mapper::toDto)
			.orElseThrow(OrderNotFoundException::new);
	}
	
	@ApiResponses(value = { 
			  @ApiResponse(responseCode = "200", description = "New Product URI", 
			    content = {@Content(
			    		mediaType = "application/json", 
			    		schema = @Schema(implementation = StockDto.class))}),
			  @ApiResponse(responseCode = "404", description = "Product not found",
			  	content = { @Content(mediaType = "application/json") })
			})
	@PostMapping
	public ResponseEntity<URI> createStockProduct(@RequestBody CreateStockProduct command) {
		try {
			var productDto = mapper.toDto(adapter.create(command));
			var uri = URI.create("/api/product/" + productDto.getId());
			return ResponseEntity.created(uri).build();
		} catch(Exception e) {
			log.error("Falha ao tentar criar novo estoque", e);
			throw new CantCreateProductException();
		}
	}
}
