package com.selimhorri.app.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.selimhorri.app.domain.OrderItem;
import com.selimhorri.app.dto.OrderItemDto;

@DisplayName("OrderItemMappingHelper Unit Tests")
class OrderItemMappingHelperUnitTest {
	
	private OrderItemDto orderItemDto;
	private OrderItem orderItem;
	
	@BeforeEach
	void setUp() {
		orderItemDto = OrderItemDto.builder()
			.orderId(1)
			.productId(100)
			.orderedQuantity(2)
			.build();
		
		orderItem = OrderItem.builder()
			.orderId(1)
			.productId(100)
			.orderedQuantity(2)
			.build();
	}
	
	@Test
	@DisplayName("Test 1: Should map OrderItem to OrderItemDto successfully")
	void testMap_FromOrderItem_ShouldReturnOrderItemDto() {
		// When
		OrderItemDto result = OrderItemMappingHelper.map(orderItem);
		
		// Then
		assertNotNull(result);
		assertEquals(orderItem.getOrderId(), result.getOrderId());
		assertEquals(orderItem.getProductId(), result.getProductId());
		assertEquals(orderItem.getOrderedQuantity(), result.getOrderedQuantity());
		assertNotNull(result.getProductDto());
		assertEquals(orderItem.getProductId(), result.getProductDto().getProductId());
		assertNotNull(result.getOrderDto());
		assertEquals(orderItem.getOrderId(), result.getOrderDto().getOrderId());
	}
	
	@Test
	@DisplayName("Test 2: Should map OrderItemDto to OrderItem successfully")
	void testMap_FromOrderItemDto_ShouldReturnOrderItem() {
		// When
		OrderItem result = OrderItemMappingHelper.map(orderItemDto);
		
		// Then
		assertNotNull(result);
		assertEquals(orderItemDto.getOrderId(), result.getOrderId());
		assertEquals(orderItemDto.getProductId(), result.getProductId());
		assertEquals(orderItemDto.getOrderedQuantity(), result.getOrderedQuantity());
	}
	
	@Test
	@DisplayName("Test 3: Should map OrderItem with different quantities")
	void testMap_FromOrderItem_WithDifferentQuantities_ShouldReturnOrderItemDto() {
		// Given
		OrderItem itemWithQuantity = OrderItem.builder()
			.orderId(2)
			.productId(200)
			.orderedQuantity(10)
			.build();
		
		// When
		OrderItemDto result = OrderItemMappingHelper.map(itemWithQuantity);
		
		// Then
		assertNotNull(result);
		assertEquals(2, result.getOrderId());
		assertEquals(200, result.getProductId());
		assertEquals(10, result.getOrderedQuantity());
	}
	
	@Test
	@DisplayName("Test 4: Should map OrderItemDto with different quantities")
	void testMap_FromOrderItemDto_WithDifferentQuantities_ShouldReturnOrderItem() {
		// Given
		OrderItemDto dtoWithQuantity = OrderItemDto.builder()
			.orderId(3)
			.productId(300)
			.orderedQuantity(15)
			.build();
		
		// When
		OrderItem result = OrderItemMappingHelper.map(dtoWithQuantity);
		
		// Then
		assertNotNull(result);
		assertEquals(3, result.getOrderId());
		assertEquals(300, result.getProductId());
		assertEquals(15, result.getOrderedQuantity());
	}
}

