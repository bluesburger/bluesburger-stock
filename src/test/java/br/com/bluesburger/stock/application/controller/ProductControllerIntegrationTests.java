package br.com.bluesburger.stock.application.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

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

import br.com.bluesburger.stock.application.dto.CreateStockProduct;
import br.com.bluesburger.stock.domain.exception.CantCreateProductException;
import br.com.bluesburger.stock.infra.database.ProductAdapter;
import br.com.bluesburger.stock.infra.database.entity.ProductEntity;
import br.com.bluesburger.stock.support.ApplicationIntegrationSupport;

class ProductControllerIntegrationTests extends ApplicationIntegrationSupport {

	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@MockBean
	private ProductAdapter productAdapter;
	
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
		void givenNoProducts_whenMockMVC_thenReturnsEmptyArray() throws Exception {
			doReturn(List.of())
				.when(productAdapter)
				.getAll();
			
			mockMvc
					.perform(get("/api/product"))
				    .andExpect(status().isOk())
				    .andExpect(jsonPath("$").isArray())
				    .andExpect(jsonPath("$").isEmpty());
		}
		
		@Test
		void givenSomeProducts_whenMockMVC_thenReturnsArrayOfProducts() throws Exception {
			var entity = new ProductEntity(1L, "Product XPTO", 100);
			entity.defineEan();
			doReturn(List.of(entity))
				.when(productAdapter)
				.getAll();
			
			mockMvc
					.perform(get("/api/product"))
				    .andExpect(status().isOk())
				    .andExpect(jsonPath("$").isArray())
				    .andExpect(jsonPath("$").isNotEmpty())
				    .andExpect(jsonPath("$[0].id").value(equalTo(1)))
				    .andExpect(jsonPath("$[0].name").value(equalTo("Product XPTO")))
				    .andExpect(jsonPath("$[0].quantity").value(equalTo(100)))
				    .andExpect(jsonPath("$[0].ean").value(notNullValue(String.class)));
		}
	}
	
	@Nested
	class GetById {
		@Test
		void givenAProduct_whenMockMVC_thenReturnsProduct() throws Exception {
			var entity = new ProductEntity(2L, "Product XPTO", 100);
			entity.defineEan();
			doReturn(Optional.of(entity))
				.when(productAdapter)
				.getById(entity.getId());
			
			mockMvc
					.perform(get("/api/product/" + entity.getId()))
				    .andExpect(status().isOk())
				    .andExpect(jsonPath("$.id").value(equalTo(2)))
				    .andExpect(jsonPath("$.name").value(equalTo("Product XPTO")))
				    .andExpect(jsonPath("$.quantity").value(equalTo(100)))
				    .andExpect(jsonPath("$.ean").value(notNullValue(String.class)));
		}
		
		@Test
		void givenNoProduct_whenMockMVC_thenReturnsHttpStatusNotFound() throws Exception {
			long orderId = 3L;
			doReturn(Optional.empty())
				.when(productAdapter)
				.getById(orderId);
			
			mockMvc
					.perform(get("/api/product/" + orderId))
				    .andExpect(status().isNotFound());
		}
	}
	
	@Nested
	class GetByProperties {
		@Test
		void givenAProduct_whenMockMVC_thenReturnsProduct() throws Exception {
			var entity = new ProductEntity(2L, "Product XPTO", 100);
			entity.defineEan();
			doReturn(Optional.of(entity))
				.when(productAdapter)
				.getByEan(entity.getEan());
			
			mockMvc
					.perform(get("/api/product/query").param("ean", entity.getEan()))
				    .andExpect(status().isOk())
				    .andExpect(jsonPath("$.id").value(equalTo(2)))
				    .andExpect(jsonPath("$.name").value(equalTo("Product XPTO")))
				    .andExpect(jsonPath("$.quantity").value(equalTo(100)))
				    .andExpect(jsonPath("$.ean").value(notNullValue(String.class)));
		}
		
		@Test
		void givenNoProduct_whenMockMVC_thenReturnsProduct() throws Exception {
			String ean = "1234567890";
			doReturn(Optional.empty())
				.when(productAdapter)
				.getByEan(ean);
			
			mockMvc
					.perform(get("/api/product/query").param("ean", ean))
				    .andExpect(status().isNotFound());
		}
	}
	
	@Nested
	class CreateStockProductNested {
		@Test
		void givenNewOrder_WhenMockMVCPost_thenReturnCreatedProduct() throws Exception {
			var entity = new ProductEntity(2L, "Product XPTO", 100);
			entity.defineEan();
			doReturn(entity)
				.when(productAdapter)
				.create(any(CreateStockProduct.class));
			
			mockMvc
					.perform(post("/api/product")
							.contentType(MediaType.APPLICATION_JSON_VALUE)
							.content("""
									{
										"name": "Product XPTO",
										"quantity": 100
									}
									""")
							.characterEncoding("utf-8"))
				    .andExpect(status().isCreated());
		}
		
		@Test
		void givenNewOrder_WhenMockMVCPostAndThrowsError_ThenReturnHandledException() throws Exception {
			doReturn(null)
				.when(productAdapter)
				.create(any(CreateStockProduct.class));
		
			mockMvc
				.perform(post("/api/product")
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.content("""
								{
									"name": "Product XPTO",
									"quantity": 100
								}
								""")
						.characterEncoding("utf-8"))
			    .andExpect(status().isNotFound())
			    .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(CantCreateProductException.class))
			    .andExpect(status().reason("Impossível criar produto"));;
		}
	}
}
