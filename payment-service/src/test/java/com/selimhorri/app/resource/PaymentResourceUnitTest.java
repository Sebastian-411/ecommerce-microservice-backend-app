package com.selimhorri.app.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.selimhorri.app.domain.PaymentStatus;
import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.dto.PaymentDto;
import com.selimhorri.app.dto.response.collection.DtoCollectionResponse;
import com.selimhorri.app.service.PaymentService;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentResource Unit Tests")
class PaymentResourceUnitTest {
	
	@Mock
	private PaymentService paymentService;
	
	@InjectMocks
	private PaymentResource paymentResource;
	
	private PaymentDto paymentDto;
	
	@BeforeEach
	void setUp() {
		OrderDto orderDto = OrderDto.builder()
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
	}
	
	@Test
	@DisplayName("Test 1: Should return all payments with 200 status")
	void testFindAll_ShouldReturnOkWithList() {
		// Given
		List<PaymentDto> payments = Arrays.asList(paymentDto);
		when(paymentService.findAll()).thenReturn(payments);
		
		// When
		ResponseEntity<DtoCollectionResponse<PaymentDto>> response = paymentResource.findAll();
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(1, response.getBody().getCollection().size());
		verify(paymentService).findAll();
	}
	
	@Test
	@DisplayName("Test 2: Should return payment by id with 200 status")
	void testFindById_ShouldReturnOkWithPayment() {
		// Given
		String paymentId = "1";
		when(paymentService.findById(anyInt())).thenReturn(paymentDto);
		
		// When
		ResponseEntity<PaymentDto> response = paymentResource.findById(paymentId);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(1, response.getBody().getPaymentId());
		verify(paymentService).findById(anyInt());
	}
	
	@Test
	@DisplayName("Test 3: Should save payment and return 200 status")
	void testSave_ShouldReturnOkWithSavedPayment() {
		// Given
		when(paymentService.save(any(PaymentDto.class))).thenReturn(paymentDto);
		
		// When
		ResponseEntity<PaymentDto> response = paymentResource.save(paymentDto);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(false, response.getBody().getIsPayed());
		verify(paymentService).save(any(PaymentDto.class));
	}
	
	@Test
	@DisplayName("Test 4: Should update payment and return 200 status")
	void testUpdate_ShouldReturnOkWithUpdatedPayment() {
		// Given
		PaymentDto updatedDto = PaymentDto.builder()
			.paymentId(1)
			.orderDto(paymentDto.getOrderDto())
			.isPayed(true)
			.paymentStatus(PaymentStatus.COMPLETED)
			.build();
		when(paymentService.update(any(PaymentDto.class))).thenReturn(updatedDto);
		
		// When
		ResponseEntity<PaymentDto> response = paymentResource.update(updatedDto);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(true, response.getBody().getIsPayed());
		assertEquals(PaymentStatus.COMPLETED, response.getBody().getPaymentStatus());
		verify(paymentService).update(any(PaymentDto.class));
	}
	
	@Test
	@DisplayName("Test 5: Should delete payment and return true")
	void testDeleteById_ShouldReturnOkWithTrue() {
		// Given
		String paymentId = "1";
		
		// When
		ResponseEntity<Boolean> response = paymentResource.deleteById(paymentId);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(true, response.getBody());
		verify(paymentService).deleteById(anyInt());
	}
}

