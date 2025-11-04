package com.selimhorri.app.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.selimhorri.app.domain.Payment;
import com.selimhorri.app.domain.PaymentStatus;
import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.dto.PaymentDto;

@DisplayName("PaymentMappingHelper Unit Tests")
class PaymentMappingHelperUnitTest {
	
	private PaymentDto paymentDto;
	private Payment payment;
	private OrderDto orderDto;
	
	@BeforeEach
	void setUp() {
		orderDto = OrderDto.builder()
			.orderId(1)
			.orderDesc("Test Order")
			.orderFee(199.99)
			.build();
		
		paymentDto = PaymentDto.builder()
			.paymentId(1)
			.orderDto(orderDto)
			.isPayed(false)
			.paymentStatus(PaymentStatus.NOT_STARTED)
			.build();
		
		payment = Payment.builder()
			.paymentId(1)
			.orderId(1)
			.isPayed(false)
			.paymentStatus(PaymentStatus.NOT_STARTED)
			.build();
	}
	
	@Test
	@DisplayName("Test 1: Should map Payment to PaymentDto successfully")
	void testMap_FromPayment_ShouldReturnPaymentDto() {
		// When
		PaymentDto result = PaymentMappingHelper.map(payment);
		
		// Then
		assertNotNull(result);
		assertEquals(payment.getPaymentId(), result.getPaymentId());
		assertEquals(payment.getIsPayed(), result.getIsPayed());
		assertEquals(payment.getPaymentStatus(), result.getPaymentStatus());
		assertNotNull(result.getOrderDto());
		assertEquals(payment.getOrderId(), result.getOrderDto().getOrderId());
	}
	
	@Test
	@DisplayName("Test 2: Should map PaymentDto to Payment successfully")
	void testMap_FromPaymentDto_ShouldReturnPayment() {
		// When
		Payment result = PaymentMappingHelper.map(paymentDto);
		
		// Then
		assertNotNull(result);
		assertEquals(paymentDto.getPaymentId(), result.getPaymentId());
		assertEquals(paymentDto.getIsPayed(), result.getIsPayed());
		assertEquals(paymentDto.getPaymentStatus(), result.getPaymentStatus());
		assertEquals(paymentDto.getOrderDto().getOrderId(), result.getOrderId());
	}
	
	@Test
	@DisplayName("Test 3: Should map Payment with COMPLETED status")
	void testMap_FromPayment_WithCompletedStatus_ShouldReturnPaymentDto() {
		// Given
		Payment completedPayment = Payment.builder()
			.paymentId(2)
			.orderId(2)
			.isPayed(true)
			.paymentStatus(PaymentStatus.COMPLETED)
			.build();
		
		// When
		PaymentDto result = PaymentMappingHelper.map(completedPayment);
		
		// Then
		assertNotNull(result);
		assertEquals(2, result.getPaymentId());
		assertEquals(true, result.getIsPayed());
		assertEquals(PaymentStatus.COMPLETED, result.getPaymentStatus());
	}
	
	@Test
	@DisplayName("Test 4: Should map PaymentDto with COMPLETED status")
	void testMap_FromPaymentDto_WithCompletedStatus_ShouldReturnPayment() {
		// Given
		OrderDto orderDto2 = OrderDto.builder()
			.orderId(2)
			.orderDesc("Second Order")
			.orderFee(299.99)
			.build();
		
		PaymentDto completedDto = PaymentDto.builder()
			.paymentId(2)
			.orderDto(orderDto2)
			.isPayed(true)
			.paymentStatus(PaymentStatus.COMPLETED)
			.build();
		
		// When
		Payment result = PaymentMappingHelper.map(completedDto);
		
		// Then
		assertNotNull(result);
		assertEquals(2, result.getPaymentId());
		assertEquals(true, result.getIsPayed());
		assertEquals(PaymentStatus.COMPLETED, result.getPaymentStatus());
		assertEquals(2, result.getOrderId());
	}
}

