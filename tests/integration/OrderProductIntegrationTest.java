package com.selimhorri.app.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.dto.ProductDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Order-Product Service Integration Tests")
class OrderProductIntegrationTest {
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	private String getBaseUrl() {
		return "http://localhost:" + port;
	}
	
	@Test
	@DisplayName("Integration Test 3: Order Service should interact with Product Service")
	void testOrderProductInteraction() {
		// Given - Create a product first
		ProductDto productDto = ProductDto.builder()
			.productTitle("Order Test Product")
			.priceUnit(49.99)
			.quantity(50)
			.build();
		
		ResponseEntity<ProductDto> productResponse = restTemplate.postForEntity(
			getBaseUrl() + "/api/products",
			productDto,
			ProductDto.class
		);
		
		assertEquals(HttpStatus.OK, productResponse.getStatusCode());
		ProductDto createdProduct = productResponse.getBody();
		assertNotNull(createdProduct);
		
		// When - Create an order that references the product
		OrderDto orderDto = OrderDto.builder()
			.orderDesc("Order for product test")
			.orderFee(49.99)
			.build();
		
		ResponseEntity<OrderDto> orderResponse = restTemplate.postForEntity(
			getBaseUrl() + "/api/orders",
			orderDto,
			OrderDto.class
		);
		
		// Then - Verify order creation
		assertEquals(HttpStatus.OK, orderResponse.getStatusCode());
		assertNotNull(orderResponse.getBody());
	}
}

