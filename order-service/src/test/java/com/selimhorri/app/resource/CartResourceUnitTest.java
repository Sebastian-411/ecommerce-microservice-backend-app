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

import com.selimhorri.app.dto.CartDto;
import com.selimhorri.app.dto.response.collection.DtoCollectionResponse;
import com.selimhorri.app.service.CartService;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartResource Unit Tests")
class CartResourceUnitTest {
	
	@Mock
	private CartService cartService;
	
	@InjectMocks
	private CartResource cartResource;
	
	private CartDto cartDto;
	
	@BeforeEach
	void setUp() {
		cartDto = CartDto.builder()
			.cartId(1)
			.userId(1)
			.build();
	}
	
	@Test
	@DisplayName("Test 1: Should return all carts with 200 status")
	void testFindAll_ShouldReturnOkWithList() {
		// Given
		List<CartDto> carts = Arrays.asList(cartDto);
		when(cartService.findAll()).thenReturn(carts);
		
		// When
		ResponseEntity<DtoCollectionResponse<CartDto>> response = cartResource.findAll();
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(1, response.getBody().getCollection().size());
		verify(cartService).findAll();
	}
	
	@Test
	@DisplayName("Test 2: Should return cart by ID with 200 status")
	void testFindById_ShouldReturnOkWithCart() {
		// Given
		String cartId = "1";
		when(cartService.findById(anyInt())).thenReturn(cartDto);
		
		// When
		ResponseEntity<CartDto> response = cartResource.findById(cartId);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(cartDto.getCartId(), response.getBody().getCartId());
		verify(cartService).findById(anyInt());
	}
	
	@Test
	@DisplayName("Test 3: Should save cart and return 200 status")
	void testSave_ShouldReturnOkWithSavedCart() {
		// Given
		when(cartService.save(any(CartDto.class))).thenReturn(cartDto);
		
		// When
		ResponseEntity<CartDto> response = cartResource.save(cartDto);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		verify(cartService).save(any(CartDto.class));
	}
	
	@Test
	@DisplayName("Test 4: Should update cart and return 200 status")
	void testUpdate_ShouldReturnOkWithUpdatedCart() {
		// Given
		CartDto updatedDto = CartDto.builder()
			.cartId(1)
			.userId(2)
			.build();
		when(cartService.update(any(CartDto.class))).thenReturn(updatedDto);
		
		// When
		ResponseEntity<CartDto> response = cartResource.update(updatedDto);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		verify(cartService).update(any(CartDto.class));
	}
	
	@Test
	@DisplayName("Test 5: Should delete cart and return true")
	void testDeleteById_ShouldReturnOkWithTrue() {
		// Given
		String cartId = "1";
		
		// When
		ResponseEntity<Boolean> response = cartResource.deleteById(cartId);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		verify(cartService).deleteById(anyInt());
	}
}

