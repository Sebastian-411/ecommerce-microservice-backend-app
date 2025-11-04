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
@DisplayName("E2E Test 4: Payment Processing Flow")
class PaymentProcessingE2ETest {
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	private String getBaseUrl() {
		return "http://localhost:" + port;
	}
	
	@Test
	@DisplayName("E2E Test 4: Complete payment processing flow after order placement")
	void testPaymentProcessing() {
		// Step 1: Create user
		UserDto user = UserDto.builder()
			.firstName("Payment")
			.lastName("User")
			.email("payment.user@example.com")
			.phone("1234567890")
			.build();
		
		ResponseEntity<UserDto> userResponse = restTemplate.postForEntity(
			getBaseUrl() + "/api/users",
			user,
			UserDto.class
		);
		
		assertEquals(HttpStatus.OK, userResponse.getStatusCode());
		
		// Step 2: Create product
		ProductDto product = ProductDto.builder()
			.productTitle("Payment Test Product")
			.priceUnit(199.99)
			.quantity(100)
			.build();
		
		ResponseEntity<ProductDto> productResponse = restTemplate.postForEntity(
			getBaseUrl() + "/api/products",
			product,
			ProductDto.class
		);
		
		assertEquals(HttpStatus.OK, productResponse.getStatusCode());
		
		// Step 3: Create order
		OrderDto order = OrderDto.builder()
			.orderDesc("Payment processing test")
			.orderFee(productResponse.getBody().getPriceUnit())
			.build();
		
		ResponseEntity<OrderDto> orderResponse = restTemplate.postForEntity(
			getBaseUrl() + "/api/orders",
			order,
			OrderDto.class
		);
		
		assertEquals(HttpStatus.OK, orderResponse.getStatusCode());
		OrderDto createdOrder = orderResponse.getBody();
		assertNotNull(createdOrder);
		assertEquals("Payment processing test", createdOrder.getOrderDesc());
		
		// Step 4: Update order (simulating payment processing)
		OrderDto updatedOrder = OrderDto.builder()
			.orderId(createdOrder.getOrderId())
			.orderDesc("Payment processed")
			.orderFee(createdOrder.getOrderFee())
			.build();
		
		ResponseEntity<OrderDto> updateResponse = restTemplate.postForEntity(
			getBaseUrl() + "/api/orders",
			updatedOrder,
			OrderDto.class
		);
		
		// Verify payment processing
		assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
		assertNotNull(updateResponse.getBody());
	}
}

