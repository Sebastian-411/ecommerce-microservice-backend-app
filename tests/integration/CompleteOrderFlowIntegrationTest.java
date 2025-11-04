package com.selimhorri.app.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.dto.UserDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Complete Order Flow Integration Tests")
class CompleteOrderFlowIntegrationTest {
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	private String getBaseUrl() {
		return "http://localhost:" + port;
	}
	
	@Test
	@DisplayName("Integration Test 4: Complete order flow - User creates order with products")
	void testCompleteOrderFlow() {
		// Step 1: Create User
		UserDto userDto = UserDto.builder()
			.firstName("OrderFlow")
			.lastName("User")
			.email("orderflow.user@example.com")
			.phone("1234567890")
			.build();
		
		ResponseEntity<UserDto> userResponse = restTemplate.postForEntity(
			getBaseUrl() + "/api/users",
			userDto,
			UserDto.class
		);
		
		assertEquals(HttpStatus.OK, userResponse.getStatusCode());
		UserDto createdUser = userResponse.getBody();
		assertNotNull(createdUser);
		
		// Step 2: Create Products
		ProductDto product1 = ProductDto.builder()
			.productTitle("Product 1")
			.priceUnit(29.99)
			.quantity(100)
			.build();
		
		ProductDto product2 = ProductDto.builder()
			.productTitle("Product 2")
			.priceUnit(39.99)
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
		
		// Step 3: Create Order
		Double totalAmount = product1Response.getBody().getPriceUnit()
			+ product2Response.getBody().getPriceUnit();
		
		OrderDto orderDto = OrderDto.builder()
			.orderDesc("Complete order flow test")
			.orderFee(totalAmount)
			.build();
		
		ResponseEntity<OrderDto> orderResponse = restTemplate.postForEntity(
			getBaseUrl() + "/api/orders",
			orderDto,
			OrderDto.class
		);
		
		// Step 4: Verify Order
		assertEquals(HttpStatus.OK, orderResponse.getStatusCode());
		OrderDto createdOrder = orderResponse.getBody();
		assertNotNull(createdOrder);
		assertEquals("Complete order flow test", createdOrder.getOrderDesc());
		assertEquals(totalAmount, createdOrder.getOrderFee());
	}
	
	@Test
	@DisplayName("Integration Test 5: Multiple services communication - User, Product, Order")
	void testMultipleServicesCommunication() {
		// Create entities across multiple services
		Map<String, Object> results = new HashMap<>();
		
		// Create User
		UserDto user = UserDto.builder()
			.firstName("MultiService")
			.lastName("Test")
			.email("multiservice@example.com")
			.build();
		
		ResponseEntity<UserDto> userResp = restTemplate.postForEntity(
			getBaseUrl() + "/api/users",
			user,
			UserDto.class
		);
		results.put("user", userResp.getBody());
		
		// Create Product
		ProductDto product = ProductDto.builder()
			.productTitle("MultiService Product")
			.priceUnit(89.99)
			.build();
		
		ResponseEntity<ProductDto> productResp = restTemplate.postForEntity(
			getBaseUrl() + "/api/products",
			product,
			ProductDto.class
		);
		results.put("product", productResp.getBody());
		
		// Create Order
		OrderDto order = OrderDto.builder()
			.orderDesc("MultiService order")
			.orderFee(89.99)
			.build();
		
		ResponseEntity<OrderDto> orderResp = restTemplate.postForEntity(
			getBaseUrl() + "/api/orders",
			order,
			OrderDto.class
		);
		results.put("order", orderResp.getBody());
		
		// Verify all communications
		assertEquals(HttpStatus.OK, userResp.getStatusCode());
		assertEquals(HttpStatus.OK, productResp.getStatusCode());
		assertEquals(HttpStatus.OK, orderResp.getStatusCode());
		
		assertNotNull(results.get("user"));
		assertNotNull(results.get("product"));
		assertNotNull(results.get("order"));
	}
}

