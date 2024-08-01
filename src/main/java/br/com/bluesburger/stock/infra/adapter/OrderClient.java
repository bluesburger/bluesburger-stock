package br.com.bluesburger.stock.infra.adapter;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import br.com.bluesburger.stock.infra.adapter.dto.OrderDto;

@FeignClient(name = "bluesburguer-order")
public interface OrderClient {

	@GetMapping("/api/order/{orderId}")
	OrderDto getById(@PathVariable("orderId") String orderId);
}
