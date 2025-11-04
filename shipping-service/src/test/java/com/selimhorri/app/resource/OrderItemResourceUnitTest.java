package com.selimhorri.app.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
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

import com.selimhorri.app.domain.id.OrderItemId;
import com.selimhorri.app.dto.OrderItemDto;
import com.selimhorri.app.dto.response.collection.DtoCollectionResponse;
import com.selimhorri.app.service.OrderItemService;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderItemResource Unit Tests")
class OrderItemResourceUnitTest {
	
	@Mock
	private OrderItemService orderItemService;
	
	@InjectMocks
	private OrderItemResource orderItemResource;
	
	private OrderItemDto orderItemDto;
	private OrderItemId orderItemId;
	
	@BeforeEach
	void setUp() {
		orderItemId = new OrderItemId();
		orderItemId.setOrderId(1);
		orderItemId.setProductId(100);
		
		orderItemDto = OrderItemDto.builder()
			.orderId(1)
			.productId(100)
			.orderedQuantity(2)
			.build();
	}
	
	@Test
	@DisplayName("Test 1: Should return all order items with 200 status")
	void testFindAll_ShouldReturnOkWithList() {
		// Given
		List<OrderItemDto> orderItems = Arrays.asList(orderItemDto);
		when(orderItemService.findAll()).thenReturn(orderItems);
		
		// When
		ResponseEntity<DtoCollectionResponse<OrderItemDto>> response = orderItemResource.findAll();
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(1, response.getBody().getCollection().size());
		verify(orderItemService).findAll();
	}
	
	@Test
	@DisplayName("Test 2: Should return order item by path variables with 200 status")
	void testFindById_WithPathVariables_ShouldReturnOkWithOrderItem() {
		// Given
		String orderId = "1";
		String productId = "100";
		when(orderItemService.findById(any(OrderItemId.class))).thenReturn(orderItemDto);
		
		// When
		ResponseEntity<OrderItemDto> response = orderItemResource.findById(orderId, productId);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(1, response.getBody().getOrderId());
		assertEquals(100, response.getBody().getProductId());
		verify(orderItemService).findById(any(OrderItemId.class));
	}
	
	@Test
	@DisplayName("Test 3: Should return order item by request body with 200 status")
	void testFindById_WithRequestBody_ShouldReturnOkWithOrderItem() {
		// Given
		when(orderItemService.findById(any(OrderItemId.class))).thenReturn(orderItemDto);
		
		// When
		ResponseEntity<OrderItemDto> response = orderItemResource.findById(orderItemId);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(1, response.getBody().getOrderId());
		verify(orderItemService).findById(any(OrderItemId.class));
	}
	
	@Test
	@DisplayName("Test 4: Should save order item and return 200 status")
	void testSave_ShouldReturnOkWithSavedOrderItem() {
		// Given
		when(orderItemService.save(any(OrderItemDto.class))).thenReturn(orderItemDto);
		
		// When
		ResponseEntity<OrderItemDto> response = orderItemResource.save(orderItemDto);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(2, response.getBody().getOrderedQuantity());
		verify(orderItemService).save(any(OrderItemDto.class));
	}
	
	@Test
	@DisplayName("Test 5: Should update order item and return 200 status")
	void testUpdate_ShouldReturnOkWithUpdatedOrderItem() {
		// Given
		OrderItemDto updatedDto = OrderItemDto.builder()
			.orderId(1)
			.productId(100)
			.orderedQuantity(5)
			.build();
		when(orderItemService.update(any(OrderItemDto.class))).thenReturn(updatedDto);
		
		// When
		ResponseEntity<OrderItemDto> response = orderItemResource.update(updatedDto);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(5, response.getBody().getOrderedQuantity());
		verify(orderItemService).update(any(OrderItemDto.class));
	}
	
	@Test
	@DisplayName("Test 6: Should delete order item by path variables and return true")
	void testDeleteById_WithPathVariables_ShouldReturnOkWithTrue() {
		// Given
		String orderId = "1";
		String productId = "100";
		
		// When
		ResponseEntity<Boolean> response = orderItemResource.deleteById(orderId, productId);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(true, response.getBody());
		verify(orderItemService).deleteById(any(OrderItemId.class));
	}
	
	@Test
	@DisplayName("Test 7: Should delete order item by request body and return true")
	void testDeleteById_WithRequestBody_ShouldReturnOkWithTrue() {
		// Given
		
		// When
		ResponseEntity<Boolean> response = orderItemResource.deleteById(orderItemId);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(true, response.getBody());
		verify(orderItemService).deleteById(any(OrderItemId.class));
	}
}

