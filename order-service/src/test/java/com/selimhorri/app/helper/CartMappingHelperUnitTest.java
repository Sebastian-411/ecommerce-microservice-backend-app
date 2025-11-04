package com.selimhorri.app.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.selimhorri.app.domain.Cart;
import com.selimhorri.app.dto.CartDto;

@DisplayName("CartMappingHelper Unit Tests")
class CartMappingHelperUnitTest {
	
	private Cart cart;
	private CartDto cartDto;
	
	@BeforeEach
	void setUp() {
		cart = Cart.builder()
			.cartId(1)
			.userId(1)
			.build();
		
		cartDto = CartDto.builder()
			.cartId(1)
			.userId(1)
			.build();
	}
	
	@Test
	@DisplayName("Test 1: Should map Cart to CartDto correctly")
	void testMapCartToCartDto() {
		// When
		CartDto result = CartMappingHelper.map(cart);
		
		// Then
		assertNotNull(result);
		assertEquals(cart.getCartId(), result.getCartId());
		assertEquals(cart.getUserId(), result.getUserId());
		assertNotNull(result.getUserDto());
		assertEquals(cart.getUserId(), result.getUserDto().getUserId());
	}
	
	@Test
	@DisplayName("Test 2: Should map CartDto to Cart correctly")
	void testMapCartDtoToCart() {
		// When
		Cart result = CartMappingHelper.map(cartDto);
		
		// Then
		assertNotNull(result);
		assertEquals(cartDto.getCartId(), result.getCartId());
		assertEquals(cartDto.getUserId(), result.getUserId());
	}
}

