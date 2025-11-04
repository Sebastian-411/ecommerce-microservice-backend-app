package com.selimhorri.app.e2e;

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
import com.selimhorri.app.dto.UserDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("E2E Test 3: Complete Order Placement Flow")
class CompleteOrderE2ETest {
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	private String getBaseUrl() {
		return "http://localhost:" + port;
	}
	
	@Test
	@DisplayName("E2E Test 3: User creates account, adds products to cart, and places order")
	void testCompleteOrderPlacement() {
		// Step 1: User registration
		UserDto user = UserDto.builder()
			.firstName("OrderUser")
			.lastName("E2E")
			.email("orderuser.e2e@example.com")
			.phone("1234567890")
			.build();
		
		ResponseEntity<UserDto> userResponse = restTemplate.postForEntity(
			getBaseUrl() + "/api/users",
			user,
			UserDto.class
		);
		
		assertEquals(HttpStatus.OK, userResponse.getStatusCode());
		UserDto registeredUser = userResponse.getBody();
		assertNotNull(registeredUser);
		
		// Step 2: Browse and select products
		ProductDto product1 = ProductDto.builder()
			.productTitle("Order Product 1")
			.priceUnit(49.99)
			.quantity(100)
			.build();
		
		ProductDto product2 = ProductDto.builder()
			.productTitle("Order Product 2")
			.priceUnit(29.99)
			.quantity(100)
			.build();
		
		ResponseEntity<ProductDto> product1Response = restTemplate.postForEntity(
			getBaseUrl() + "/api/products",
			product1,
			ProductDto.class
		);
		
		ResponseEntity<ProductDto> product2Response = restTemplate.postForEntity(
			getBaseUrl() + "/api/products",
			product2,
			ProductDto.class
		);
		
		assertEquals(HttpStatus.OK, product1Response.getStatusCode());
		assertEquals(HttpStatus.OK, product2Response.getStatusCode());
		
		// Step 3: Place order
		Double orderTotal = product1Response.getBody().getPriceUnit()
			+ product2Response.getBody().getPriceUnit();
		
		OrderDto order = OrderDto.builder()
			.orderDesc("Order placement test")
			.orderFee(orderTotal)
			.build();
		
		ResponseEntity<OrderDto> orderResponse = restTemplate.postForEntity(
			getBaseUrl() + "/api/orders",
			order,
			OrderDto.class
		);
		
		// Verify order placement
		assertEquals(HttpStatus.OK, orderResponse.getStatusCode());
		OrderDto placedOrder = orderResponse.getBody();
		assertNotNull(placedOrder);
		assertEquals("Order placement test", placedOrder.getOrderDesc());
		assertEquals(orderTotal, placedOrder.getOrderFee());
	}
}

