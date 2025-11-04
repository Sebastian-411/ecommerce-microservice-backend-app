package com.selimhorri.app.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import com.selimhorri.app.dto.PaymentDto;
import com.selimhorri.app.domain.PaymentStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Payment-Order Service Integration Tests")
class PaymentOrderIntegrationTest {
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	private String getBaseUrl() {
		return "http://localhost:" + port;
	}
	
	@Test
	@DisplayName("Integration Test: Create order and process payment")
	void testOrderPaymentFlow() {
		// Step 1: Create Order
		OrderDto orderDto = OrderDto.builder()
			.orderDesc("Payment integration test order")
			.orderFee(199.99)
			.build();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<OrderDto> orderRequest = new HttpEntity<>(orderDto, headers);
		
		ResponseEntity<OrderDto> orderResponse = restTemplate.exchange(
			getBaseUrl() + "/api/orders",
			HttpMethod.POST,
			orderRequest,
			OrderDto.class
		);
		
		assertEquals(HttpStatus.OK, orderResponse.getStatusCode());
		OrderDto createdOrder = orderResponse.getBody();
		assertNotNull(createdOrder);
		
		// Step 2: Create Payment for Order
		PaymentDto paymentDto = PaymentDto.builder()
			.orderDto(createdOrder)
			.isPayed(false)
			.paymentStatus(PaymentStatus.NOT_STARTED)
			.build();
		
		HttpEntity<PaymentDto> paymentRequest = new HttpEntity<>(paymentDto, headers);
		
		ResponseEntity<PaymentDto> paymentResponse = restTemplate.exchange(
			getBaseUrl() + "/api/payments",
			HttpMethod.POST,
			paymentRequest,
			PaymentDto.class
		);
		
		// Verify Payment Creation
		assertEquals(HttpStatus.OK, paymentResponse.getStatusCode());
		PaymentDto createdPayment = paymentResponse.getBody();
		assertNotNull(createdPayment);
		assertNotNull(createdPayment.getOrderDto());
		assertEquals(createdOrder.getOrderId(), createdPayment.getOrderDto().getOrderId());
	}
	
	@Test
	@DisplayName("Integration Test: Update payment status after order creation")
	void testUpdatePaymentStatus() {
		// Create Order
		OrderDto orderDto = OrderDto.builder()
			.orderDesc("Payment status update test")
			.orderFee(299.99)
			.build();
		
		ResponseEntity<OrderDto> orderResponse = restTemplate.postForEntity(
			getBaseUrl() + "/api/orders",
			orderDto,
			OrderDto.class
		);
		
		assertEquals(HttpStatus.OK, orderResponse.getStatusCode());
		OrderDto createdOrder = orderResponse.getBody();
		assertNotNull(createdOrder);
		
		// Create Payment
		PaymentDto paymentDto = PaymentDto.builder()
			.orderDto(createdOrder)
			.isPayed(false)
			.paymentStatus(PaymentStatus.NOT_STARTED)
			.build();
		
		ResponseEntity<PaymentDto> createPaymentResponse = restTemplate.postForEntity(
			getBaseUrl() + "/api/payments",
			paymentDto,
			PaymentDto.class
		);
		
		assertEquals(HttpStatus.OK, createPaymentResponse.getStatusCode());
		PaymentDto createdPayment = createPaymentResponse.getBody();
		assertNotNull(createdPayment);
		
		// Update Payment Status
		PaymentDto updatedPayment = PaymentDto.builder()
			.paymentId(createdPayment.getPaymentId())
			.orderDto(createdOrder)
			.isPayed(true)
			.paymentStatus(PaymentStatus.COMPLETED)
			.build();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<PaymentDto> updateRequest = new HttpEntity<>(updatedPayment, headers);
		
		ResponseEntity<PaymentDto> updateResponse = restTemplate.exchange(
			getBaseUrl() + "/api/payments",
			HttpMethod.PUT,
			updateRequest,
			PaymentDto.class
		);
		
		// Verify Update
		assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
		PaymentDto updated = updateResponse.getBody();
		assertNotNull(updated);
		assertTrue(updated.getIsPayed());
		assertEquals(PaymentStatus.COMPLETED, updated.getPaymentStatus());
	}
	
	@Test
	@DisplayName("Integration Test: Retrieve payment by order")
	void testRetrievePaymentByOrder() {
		// Create Order
		OrderDto orderDto = OrderDto.builder()
			.orderDesc("Retrieve payment test")
			.orderFee(149.99)
			.build();
		
		ResponseEntity<OrderDto> orderResponse = restTemplate.postForEntity(
			getBaseUrl() + "/api/orders",
			orderDto,
			OrderDto.class
		);
		
		assertEquals(HttpStatus.OK, orderResponse.getStatusCode());
		OrderDto createdOrder = orderResponse.getBody();
		assertNotNull(createdOrder);
		
		// Create Payment
		PaymentDto paymentDto = PaymentDto.builder()
			.orderDto(createdOrder)
			.isPayed(false)
			.paymentStatus(PaymentStatus.IN_PROGRESS)
			.build();
		
		ResponseEntity<PaymentDto> createPaymentResponse = restTemplate.postForEntity(
			getBaseUrl() + "/api/payments",
			paymentDto,
			PaymentDto.class
		);
		
		assertEquals(HttpStatus.OK, createPaymentResponse.getStatusCode());
		PaymentDto createdPayment = createPaymentResponse.getBody();
		assertNotNull(createdPayment);
		
		// Retrieve Payment
		ResponseEntity<PaymentDto> retrieveResponse = restTemplate.getForEntity(
			getBaseUrl() + "/api/payments/" + createdPayment.getPaymentId(),
			PaymentDto.class
		);
		
		// Verify Retrieval
		assertEquals(HttpStatus.OK, retrieveResponse.getStatusCode());
		PaymentDto retrieved = retrieveResponse.getBody();
		assertNotNull(retrieved);
		assertEquals(createdPayment.getPaymentId(), retrieved.getPaymentId());
		assertNotNull(retrieved.getOrderDto());
		assertEquals(createdOrder.getOrderId(), retrieved.getOrderDto().getOrderId());
	}
}

