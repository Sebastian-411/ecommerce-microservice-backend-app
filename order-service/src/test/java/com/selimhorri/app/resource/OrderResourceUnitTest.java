package com.selimhorri.app.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
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

import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.dto.response.collection.DtoCollectionResponse;
import com.selimhorri.app.service.OrderService;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderResource Unit Tests")
class OrderResourceUnitTest {
	
	@Mock
	private OrderService orderService;
	
	@InjectMocks
	private OrderResource orderResource;
	
	private OrderDto orderDto;
	
	@BeforeEach
	void setUp() {
		orderDto = OrderDto.builder()
			.orderId(1)
			.orderDate(LocalDateTime.now())
			.orderDesc("Test Order")
			.orderFee(299.99)
			.build();
	}
	
	@Test
	@DisplayName("Test 1: Should return all orders with 200 status")
	void testFindAll_ShouldReturnOkWithList() {
		// Given
		List<OrderDto> orders = Arrays.asList(orderDto);
		when(orderService.findAll()).thenReturn(orders);
		
		// When
		ResponseEntity<DtoCollectionResponse<OrderDto>> response = orderResource.findAll();
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(1, response.getBody().getCollection().size());
		verify(orderService).findAll();
	}
	
	@Test
	@DisplayName("Test 2: Should return order by ID with 200 status")
	void testFindById_ShouldReturnOkWithOrder() {
		// Given
		String orderId = "1";
		when(orderService.findById(anyInt())).thenReturn(orderDto);
		
		// When
		ResponseEntity<OrderDto> response = orderResource.findById(orderId);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(orderDto.getOrderId(), response.getBody().getOrderId());
		verify(orderService).findById(anyInt());
	}
	
	@Test
	@DisplayName("Test 3: Should save order and return 200 status")
	void testSave_ShouldReturnOkWithSavedOrder() {
		// Given
		when(orderService.save(any(OrderDto.class))).thenReturn(orderDto);
		
		// When
		ResponseEntity<OrderDto> response = orderResource.save(orderDto);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		verify(orderService).save(any(OrderDto.class));
	}
	
	@Test
	@DisplayName("Test 4: Should update order and return 200 status")
	void testUpdate_ShouldReturnOkWithUpdatedOrder() {
		// Given
		OrderDto updatedDto = OrderDto.builder()
			.orderId(1)
			.orderDesc("Updated Order")
			.orderFee(399.99)
			.build();
		when(orderService.update(any(OrderDto.class))).thenReturn(updatedDto);
		
		// When
		ResponseEntity<OrderDto> response = orderResource.update(updatedDto);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Updated Order", response.getBody().getOrderDesc());
		verify(orderService).update(any(OrderDto.class));
	}
	
	@Test
	@DisplayName("Test 5: Should update order with ID and return 200 status")
	void testUpdateWithId_ShouldReturnOkWithUpdatedOrder() {
		// Given
		String orderId = "1";
		OrderDto updatedDto = OrderDto.builder()
			.orderId(1)
			.orderDesc("Updated Order")
			.orderFee(399.99)
			.build();
		when(orderService.update(anyInt(), any(OrderDto.class))).thenReturn(updatedDto);
		
		// When
		ResponseEntity<OrderDto> response = orderResource.update(orderId, updatedDto);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		verify(orderService).update(anyInt(), any(OrderDto.class));
	}
	
	@Test
	@DisplayName("Test 6: Should delete order and return true")
	void testDeleteById_ShouldReturnOkWithTrue() {
		// Given
		String orderId = "1";
		
		// When
		ResponseEntity<Boolean> response = orderResource.deleteById(orderId);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		verify(orderService).deleteById(anyInt());
	}
}

