package com.selimhorri.app.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.selimhorri.app.domain.Cart;
import com.selimhorri.app.domain.Order;
import com.selimhorri.app.dto.CartDto;
import com.selimhorri.app.dto.OrderDto;

@DisplayName("OrderMappingHelper Unit Tests")
class OrderMappingHelperUnitTest {
	
	private Order order;
	private OrderDto orderDto;
	private Cart cart;
	private CartDto cartDto;
	
	@BeforeEach
	void setUp() {
		cart = Cart.builder()
			.cartId(1)
			.userId(1)
			.build();
		
		order = Order.builder()
			.orderId(1)
			.orderDate(LocalDateTime.now())
			.orderDesc("Test Order")
			.orderFee(299.99)
			.cart(cart)
			.build();
		
		cartDto = CartDto.builder()
			.cartId(1)
			.userId(1)
			.build();
		
		orderDto = OrderDto.builder()
			.orderId(1)
			.orderDate(LocalDateTime.now())
			.orderDesc("Test Order")
			.orderFee(299.99)
			.cartDto(cartDto)
			.build();
	}
	
	@Test
	@DisplayName("Test 1: Should map Order to OrderDto correctly")
	void testMapOrderToOrderDto() {
		// When
		OrderDto result = OrderMappingHelper.map(order);
		
		// Then
		assertNotNull(result);
		assertEquals(order.getOrderId(), result.getOrderId());
		assertEquals(order.getOrderDesc(), result.getOrderDesc());
		assertEquals(order.getOrderFee(), result.getOrderFee());
		assertNotNull(result.getCartDto());
		assertEquals(order.getCart().getCartId(), result.getCartDto().getCartId());
	}
	
	@Test
	@DisplayName("Test 2: Should map OrderDto to Order correctly")
	void testMapOrderDtoToOrder() {
		// When
		Order result = OrderMappingHelper.map(orderDto);
		
		// Then
		assertNotNull(result);
		assertEquals(orderDto.getOrderId(), result.getOrderId());
		assertEquals(orderDto.getOrderDesc(), result.getOrderDesc());
		assertEquals(orderDto.getOrderFee(), result.getOrderFee());
		assertNotNull(result.getCart());
		assertEquals(orderDto.getCartDto().getCartId(), result.getCart().getCartId());
	}
}

