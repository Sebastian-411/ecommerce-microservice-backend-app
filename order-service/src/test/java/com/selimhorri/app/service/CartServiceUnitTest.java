package com.selimhorri.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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

import com.selimhorri.app.domain.Cart;
import com.selimhorri.app.dto.CartDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.exception.wrapper.CartNotFoundException;
import com.selimhorri.app.repository.CartRepository;
import com.selimhorri.app.service.impl.CartServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartService Unit Tests")
class CartServiceUnitTest {
	
	@Mock
	private CartRepository cartRepository;
	
	@Mock
	private RestTemplate restTemplate;
	
	@InjectMocks
	private CartServiceImpl cartService;
	
	private CartDto cartDto;
	private Cart cart;
	private UserDto userDto;
	
	@BeforeEach
	void setUp() {
		cartDto = CartDto.builder()
			.cartId(1)
			.userId(1)
			.build();
		
		cart = Cart.builder()
			.cartId(1)
			.userId(1)
			.build();
		
		userDto = UserDto.builder()
			.userId(1)
			.firstName("John")
			.lastName("Doe")
			.email("john.doe@example.com")
			.build();
	}
	
	@Test
	@DisplayName("Test 1: Should find all carts successfully")
	void testFindAll_ShouldReturnListOfCarts() {
		// Given
		List<Cart> carts = Arrays.asList(cart);
		when(cartRepository.findAll()).thenReturn(carts);
		when(restTemplate.getForObject(anyString(), eq(UserDto.class))).thenReturn(userDto);
		
		// When
		List<CartDto> result = cartService.findAll();
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.size());
		verify(cartRepository).findAll();
		verify(restTemplate).getForObject(anyString(), eq(UserDto.class));
	}
	
	@Test
	@DisplayName("Test 2: Should find cart by ID successfully")
	void testFindById_ShouldReturnCartDto() {
		// Given
		Integer cartId = 1;
		when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
		when(restTemplate.getForObject(anyString(), eq(UserDto.class))).thenReturn(userDto);
		
		// When
		CartDto result = cartService.findById(cartId);
		
		// Then
		assertNotNull(result);
		assertEquals(cartId, result.getCartId());
		verify(cartRepository).findById(cartId);
		verify(restTemplate).getForObject(anyString(), eq(UserDto.class));
	}
	
	@Test
	@DisplayName("Test 3: Should throw exception when cart not found by ID")
	void testFindById_WhenCartNotFound_ShouldThrowException() {
		// Given
		Integer cartId = 99;
		when(cartRepository.findById(cartId)).thenReturn(Optional.empty());
		
		// When / Then
		assertThrows(CartNotFoundException.class, () -> cartService.findById(cartId));
		verify(cartRepository).findById(cartId);
	}
	
	@Test
	@DisplayName("Test 4: Should save cart successfully")
	void testSave_ShouldReturnSavedCartDto() {
		// Given
		when(cartRepository.save(any(Cart.class))).thenReturn(cart);
		
		// When
		CartDto result = cartService.save(cartDto);
		
		// Then
		assertNotNull(result);
		assertEquals(cartDto.getCartId(), result.getCartId());
		verify(cartRepository).save(any(Cart.class));
	}
	
	@Test
	@DisplayName("Test 5: Should update cart successfully")
	void testUpdate_ShouldReturnUpdatedCartDto() {
		// Given
		CartDto updatedDto = CartDto.builder()
			.cartId(1)
			.userId(2)
			.build();
		
		Cart updatedCart = Cart.builder()
			.cartId(1)
			.userId(2)
			.build();
		
		when(cartRepository.save(any(Cart.class))).thenReturn(updatedCart);
		
		// When
		CartDto result = cartService.update(updatedDto);
		
		// Then
		assertNotNull(result);
		assertEquals(updatedDto.getCartId(), result.getCartId());
		verify(cartRepository).save(any(Cart.class));
	}
	
	@Test
	@DisplayName("Test 6: Should delete cart by ID successfully")
	void testDeleteById_ShouldDeleteCart() {
		// Given
		Integer cartId = 1;
		when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
		
		// When
		cartService.deleteById(cartId);
		
		// Then
		verify(cartRepository).deleteById(cartId);
	}
}

