package br.com.bluesburger.stock.application.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import br.com.bluesburger.stock.application.dto.order.OrderItem;
import br.com.bluesburger.stock.application.dto.order.ReserveOrderRequest;
import br.com.bluesburger.stock.application.dto.order.ReserveOrderResponse;
import br.com.bluesburger.stock.application.usecase.StockUseCase;
import br.com.bluesburger.stock.domain.exception.CantCreateProductException;
import br.com.bluesburger.stock.infra.database.StockAdapter;
import br.com.bluesburger.stock.infra.database.entity.OrderStockEntity;
import br.com.bluesburger.stock.infra.database.entity.ProductEntity;
import br.com.bluesburger.stock.support.ApplicationIntegrationSupport;

class StockControllerIntegrationTests extends ApplicationIntegrationSupport {
	
	private static final UUID EXISTANT_ORDER_ID = UUID.fromString("ddedf1ab-0b2f-4766-a9fc-104bedc98492");

	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@MockBean
	private StockAdapter stockAdapter;
	
	@MockBean
	private StockUseCase stockUseCase;
	
	private MockMvc mockMvc;
	
	@BeforeEach
	void setup() throws Exception {
	    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
	}
	
	@AfterEach
	void tearDown() {
		
	}
	
	@Nested
	class GetAll {
		@Test
		void givenNoStocks_whenMockMVC_thenReturnsEmptyArray() throws Exception {
			doReturn(List.of())
				.when(stockAdapter)
				.getAll();
			
			mockMvc
					.perform(get("/api/stock"))
				    .andExpect(status().isOk())
				    .andExpect(jsonPath("$").isArray())
				    .andExpect(jsonPath("$").isEmpty());
		}
		
		@Test
		void givenSomeStocks_whenMockMVC_thenReturnArrayOfStocks() throws Exception {
			var productEntity = new ProductEntity(1L, "Product XPTO", 100);
			productEntity.defineEan();
			var stockEntity = new OrderStockEntity(1L, null, EXISTANT_ORDER_ID, productEntity);
			doReturn(List.of(stockEntity))
				.when(stockAdapter)
				.getAll();
			
			mockMvc
					.perform(get("/api/stock"))
				    .andExpect(status().isOk())
				    .andExpect(jsonPath("$").isArray())
				    .andExpect(jsonPath("$").isNotEmpty())
				    .andExpect(jsonPath("$[0].id").value(equalTo(1)))
				    .andExpect(jsonPath("$[0].status").value(equalTo("PENDING")))
				    .andExpect(jsonPath("$[0].orderId").value(equalTo(EXISTANT_ORDER_ID.toString())));
		}
	}
	
	@Nested
	class GetById {
		@Test
		void givenAStock_whenMockMVC_thenReturnStock() throws Exception {
			var productEntity = new ProductEntity(2L, "Product XPTO", 100);
			productEntity.defineEan();
			var stockEntity = new OrderStockEntity(1L, null, EXISTANT_ORDER_ID, productEntity);
			doReturn(Optional.of(stockEntity))
				.when(stockAdapter)
				.getById(stockEntity.getId());
			
			mockMvc
					.perform(get("/api/stock/" + stockEntity.getId()))
				    .andExpect(status().isOk())
				    .andExpect(jsonPath("$.id").value(equalTo(1)))
				    .andExpect(jsonPath("$.status").value(equalTo("PENDING")))
				    .andExpect(jsonPath("$.orderId").value(equalTo(EXISTANT_ORDER_ID.toString())));
		}
		
		@Test
		void givenNoStock_whenMockMVC_thenReturnHttpStatusNotFound() throws Exception {
			long orderId = 3L;
			doReturn(Optional.empty())
				.when(stockAdapter)
				.getById(orderId);
			
			mockMvc
					.perform(get("/api/stock/" + orderId))
				    .andExpect(status().isNotFound());
		}
	}
	
	@Nested
	class ReserveOrderNested {
		@Test
		void givenNewOrder_WhenMockMVCPost_thenReturnCreatedProduct() throws Exception {
			List<OrderItem> items = List.of(new OrderItem(2L, 1));
			doReturn(new ReserveOrderResponse(EXISTANT_ORDER_ID.toString(), items))
				.when(stockUseCase)
				.reserveOrder(any(ReserveOrderRequest.class));
			
			mockMvc
					.perform(post("/api/stock")
							.contentType(MediaType.APPLICATION_JSON_VALUE)
							.content("""
									{
										"orderId": "ddedf1ab-0b2f-4766-a9fc-104bedc98492",
										"items": [{
											"id": 2,
											"quantity": 1
										}]
									}
									""")
							.characterEncoding("utf-8"))
				    .andExpect(status().isOk());
		}
		
		@Test
		void givenNewOrder_WhenMockMVCPostAndThrowsError_ThenReturnHandledException() throws Exception {
			doReturn(null)
				.when(stockUseCase)
				.reserveOrder(any(ReserveOrderRequest.class));
		
			mockMvc
				.perform(post("/api/product")
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.content("""
								{
									"orderId": "ddedf1ab-0b2f-4766-a9fc-104bedc98492",
									"items": [{
										"id": 2,
										"quantity": 1
									}]
								}
								""")
						.characterEncoding("utf-8"))
			    .andExpect(status().isNotFound())
			    .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(CantCreateProductException.class))
			    .andExpect(status().reason("Impossível criar produto"));;
		}
	}
}
