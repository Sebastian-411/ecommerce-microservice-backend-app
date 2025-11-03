package com.selimhorri.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
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

import com.selimhorri.app.domain.Order;
import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.exception.wrapper.OrderNotFoundException;
import com.selimhorri.app.repository.OrderRepository;
import com.selimhorri.app.service.impl.OrderServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Unit Tests")
class OrderServiceUnitTest {
	
	@Mock
	private OrderRepository orderRepository;
	
	@InjectMocks
	private OrderServiceImpl orderService;
	
	private OrderDto orderDto;
	private Order order;
	
	@BeforeEach
	void setUp() {
		orderDto = OrderDto.builder()
			.orderId(1)
			.orderDate(LocalDateTime.now())
			.orderDesc("Test Order")
			.orderFee(299.99)
			.build();
		
		order = Order.builder()
			.orderId(1)
			.orderDate(LocalDateTime.now())
			.orderDesc("Test Order")
			.orderFee(299.99)
			.build();
	}
	
	@Test
	@DisplayName("Test 1: Should find all orders successfully")
	void testFindAll_ShouldReturnListOfOrders() {
		// Given
		List<Order> orders = Arrays.asList(order, order);
		when(orderRepository.findAll()).thenReturn(orders);
		
		// When
		List<OrderDto> result = orderService.findAll();
		
		// Then
		assertNotNull(result);
		assertEquals(2, result.size());
		verify(orderRepository).findAll();
	}
	
	@Test
	@DisplayName("Test 2: Should find order by id successfully")
	void testFindById_ShouldReturnOrderDto() {
		// Given
		Integer orderId = 1;
		when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
		
		// When
		OrderDto result = orderService.findById(orderId);
		
		// Then
		assertNotNull(result);
		assertEquals(orderId, result.getOrderId());
		assertEquals("Test Order", result.getOrderDesc());
		verify(orderRepository).findById(orderId);
	}
	
	@Test
	@DisplayName("Test 3: Should throw exception when order not found")
	void testFindById_WhenOrderNotFound_ShouldThrowException() {
		// Given
		Integer orderId = 999;
		when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
		
		// When & Then
		assertThrows(OrderNotFoundException.class, () -> {
			orderService.findById(orderId);
		});
		verify(orderRepository).findById(orderId);
	}
	
	@Test
	@DisplayName("Test 4: Should save order successfully")
	void testSave_ShouldReturnSavedOrderDto() {
		// Given
		when(orderRepository.save(any(Order.class))).thenReturn(order);
		
		// When
		OrderDto result = orderService.save(orderDto);
		
		// Then
		assertNotNull(result);
		assertEquals(orderDto.getOrderDesc(), result.getOrderDesc());
		verify(orderRepository).save(any(Order.class));
	}
	
	@Test
	@DisplayName("Test 5: Should update order successfully")
	void testUpdate_ShouldReturnUpdatedOrderDto() {
		// Given
		OrderDto updatedDto = OrderDto.builder()
			.orderId(1)
			.orderDesc("Updated Order")
			.orderFee(399.99)
			.build();
		
		Order updatedOrder = Order.builder()
			.orderId(1)
			.orderDesc("Updated Order")
			.orderFee(399.99)
			.build();
		
		when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);
		
		// When
		OrderDto result = orderService.update(updatedDto);
		
		// Then
		assertNotNull(result);
		assertEquals("Updated Order", result.getOrderDesc());
		verify(orderRepository).save(any(Order.class));
	}
	
	@Test
	@DisplayName("Test 6: Should update order with ID successfully")
	void testUpdateWithId_ShouldReturnUpdatedOrderDto() {
		// Given
		Integer orderId = 1;
		OrderDto updatedDto = OrderDto.builder()
			.orderId(1)
			.orderDesc("Updated Order with ID")
			.orderFee(499.99)
			.build();
		
		when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
		when(orderRepository.save(any(Order.class))).thenReturn(order);
		
		// When
		OrderDto result = orderService.update(orderId, updatedDto);
		
		// Then
		assertNotNull(result);
		verify(orderRepository).findById(orderId);
		verify(orderRepository).save(any(Order.class));
	}
	
	@Test
	@DisplayName("Test 7: Should delete order by ID successfully")
	void testDeleteById_ShouldDeleteOrder() {
		// Given
		Integer orderId = 1;
		when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
		
		// When
		orderService.deleteById(orderId);
		
		// Then
		verify(orderRepository).findById(orderId);
		verify(orderRepository).delete(any(Order.class));
	}
}

