package com.selimhorri.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import com.selimhorri.app.constant.AppConstant;
import com.selimhorri.app.domain.Payment;
import com.selimhorri.app.domain.PaymentStatus;
import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.dto.PaymentDto;
import com.selimhorri.app.exception.wrapper.PaymentNotFoundException;
import com.selimhorri.app.repository.PaymentRepository;
import com.selimhorri.app.service.impl.PaymentServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService Unit Tests")
class PaymentServiceUnitTest {
	
	@Mock
	private PaymentRepository paymentRepository;
	
	@Mock
	private RestTemplate restTemplate;
	
	@InjectMocks
	private PaymentServiceImpl paymentService;
	
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
	@DisplayName("Test 1: Should find all payments successfully")
	void testFindAll_ShouldReturnListOfPayments() {
		// Given
		List<Payment> payments = Arrays.asList(payment, payment);
		when(paymentRepository.findAll()).thenReturn(payments);
		when(restTemplate.getForObject(anyString(), any(Class.class)))
			.thenReturn(orderDto);
		
		// When
		List<PaymentDto> result = paymentService.findAll();
		
		// Then
		assertNotNull(result);
		assertEquals(2, result.size());
		verify(paymentRepository).findAll();
	}
	
	@Test
	@DisplayName("Test 2: Should find payment by id successfully")
	void testFindById_ShouldReturnPaymentDto() {
		// Given
		Integer paymentId = 1;
		when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
		when(restTemplate.getForObject(anyString(), any(Class.class)))
			.thenReturn(orderDto);
		
		// When
		PaymentDto result = paymentService.findById(paymentId);
		
		// Then
		assertNotNull(result);
		assertEquals(paymentId, result.getPaymentId());
		assertEquals(PaymentStatus.NOT_STARTED, result.getPaymentStatus());
		verify(paymentRepository).findById(paymentId);
	}
	
	@Test
	@DisplayName("Test 3: Should throw exception when payment not found by id")
	void testFindById_WhenPaymentNotFound_ShouldThrowException() {
		// Given
		Integer paymentId = 999;
		when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());
		
		// When & Then
		assertThrows(PaymentNotFoundException.class, () -> {
			paymentService.findById(paymentId);
		});
		verify(paymentRepository).findById(paymentId);
	}
	
	@Test
	@DisplayName("Test 4: Should save payment successfully")
	void testSave_ShouldReturnSavedPaymentDto() {
		// Given
		when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
		
		// When
		PaymentDto result = paymentService.save(paymentDto);
		
		// Then
		assertNotNull(result);
		assertEquals(paymentDto.getPaymentId(), result.getPaymentId());
		assertEquals(paymentDto.getIsPayed(), result.getIsPayed());
		verify(paymentRepository).save(any(Payment.class));
	}
	
	@Test
	@DisplayName("Test 5: Should update payment successfully")
	void testUpdate_ShouldReturnUpdatedPaymentDto() {
		// Given
		PaymentDto updatedDto = PaymentDto.builder()
			.paymentId(1)
			.orderDto(orderDto)
			.isPayed(true)
			.paymentStatus(PaymentStatus.COMPLETED)
			.build();
		Payment updatedPayment = Payment.builder()
			.paymentId(1)
			.orderId(1)
			.isPayed(true)
			.paymentStatus(PaymentStatus.COMPLETED)
			.build();
		
		when(paymentRepository.save(any(Payment.class))).thenReturn(updatedPayment);
		
		// When
		PaymentDto result = paymentService.update(updatedDto);
		
		// Then
		assertNotNull(result);
		assertEquals(true, result.getIsPayed());
		assertEquals(PaymentStatus.COMPLETED, result.getPaymentStatus());
		verify(paymentRepository).save(any(Payment.class));
	}
	
	@Test
	@DisplayName("Test 6: Should delete payment by id successfully")
	void testDeleteById_ShouldDeletePayment() {
		// Given
		Integer paymentId = 1;
		
		// When
		paymentService.deleteById(paymentId);
		
		// Then
		verify(paymentRepository).deleteById(paymentId);
	}
	
	@Test
	@DisplayName("Test 7: Should populate order DTO when finding all")
	void testFindAll_ShouldPopulateOrderDto() {
		// Given
		List<Payment> payments = Arrays.asList(payment);
		when(paymentRepository.findAll()).thenReturn(payments);
		when(restTemplate.getForObject(
			AppConstant.DiscoveredDomainsApi.ORDER_SERVICE_API_URL + "/1", 
			OrderDto.class))
			.thenReturn(orderDto);
		
		// When
		List<PaymentDto> result = paymentService.findAll();
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.size());
		assertNotNull(result.get(0).getOrderDto());
		assertEquals(1, result.get(0).getOrderDto().getOrderId());
		verify(paymentRepository).findAll();
	}
	
	@Test
	@DisplayName("Test 8: Should populate order DTO when finding by id")
	void testFindById_ShouldPopulateOrderDto() {
		// Given
		Integer paymentId = 1;
		when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
		when(restTemplate.getForObject(
			AppConstant.DiscoveredDomainsApi.ORDER_SERVICE_API_URL + "/1", 
			OrderDto.class))
			.thenReturn(orderDto);
		
		// When
		PaymentDto result = paymentService.findById(paymentId);
		
		// Then
		assertNotNull(result);
		assertNotNull(result.getOrderDto());
		assertEquals("Test Order", result.getOrderDto().getOrderDesc());
		verify(paymentRepository).findById(paymentId);
	}
	
	@Test
	@DisplayName("Test 9: Should handle payment with COMPLETED status")
	void testSave_WithCompletedStatus_ShouldReturnPaymentDto() {
		// Given
		PaymentDto completedDto = PaymentDto.builder()
			.paymentId(2)
			.orderDto(orderDto)
			.isPayed(true)
			.paymentStatus(PaymentStatus.COMPLETED)
			.build();
		Payment completedPayment = Payment.builder()
			.paymentId(2)
			.orderId(1)
			.isPayed(true)
			.paymentStatus(PaymentStatus.COMPLETED)
			.build();
		
		when(paymentRepository.save(any(Payment.class))).thenReturn(completedPayment);
		
		// When
		PaymentDto result = paymentService.save(completedDto);
		
		// Then
		assertNotNull(result);
		assertEquals(true, result.getIsPayed());
		assertEquals(PaymentStatus.COMPLETED, result.getPaymentStatus());
		verify(paymentRepository).save(any(Payment.class));
	}
}

