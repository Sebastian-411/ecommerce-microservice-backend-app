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

import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.dto.response.collection.DtoCollectionResponse;
import com.selimhorri.app.service.ProductService;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductResource Unit Tests")
class ProductResourceUnitTest {
	
	@Mock
	private ProductService productService;
	
	@InjectMocks
	private ProductResource productResource;
	
	private ProductDto productDto;
	
	@BeforeEach
	void setUp() {
		productDto = ProductDto.builder()
			.productId(1)
			.productTitle("Laptop")
			.priceUnit(999.99)
			.quantity(10)
			.build();
	}
	
	@Test
	@DisplayName("Test 1: Should return all products with 200 status")
	void testFindAll_ShouldReturnOkWithList() {
		// Given
		List<ProductDto> products = Arrays.asList(productDto, productDto);
		when(productService.findAll()).thenReturn(products);
		
		// When
		ResponseEntity<DtoCollectionResponse<ProductDto>> response = productResource.findAll();
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(2, response.getBody().getCollection().size());
		verify(productService).findAll();
	}
	
	@Test
	@DisplayName("Test 2: Should return product by id with 200 status")
	void testFindById_ShouldReturnOkWithProduct() {
		// Given
		String productId = "1";
		when(productService.findById(anyInt())).thenReturn(productDto);
		
		// When
		ResponseEntity<ProductDto> response = productResource.findById(productId);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(1, response.getBody().getProductId());
		verify(productService).findById(anyInt());
	}
	
	@Test
	@DisplayName("Test 3: Should save product and return 200 status")
	void testSave_ShouldReturnOkWithSavedProduct() {
		// Given
		when(productService.save(any(ProductDto.class))).thenReturn(productDto);
		
		// When
		ResponseEntity<ProductDto> response = productResource.save(productDto);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		verify(productService).save(any(ProductDto.class));
	}
	
	@Test
	@DisplayName("Test 4: Should update product and return 200 status")
	void testUpdate_ShouldReturnOkWithUpdatedProduct() {
		// Given
		ProductDto updatedDto = ProductDto.builder()
			.productId(1)
			.productTitle("Gaming Laptop")
			.priceUnit(1299.99)
			.build();
		when(productService.update(any(ProductDto.class))).thenReturn(updatedDto);
		
		// When
		ResponseEntity<ProductDto> response = productResource.update(updatedDto);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Gaming Laptop", response.getBody().getProductTitle());
		verify(productService).update(any(ProductDto.class));
	}
	
	@Test
	@DisplayName("Test 5: Should delete product and return true")
	void testDeleteById_ShouldReturnOkWithTrue() {
		// Given
		String productId = "1";
		
		// When
		ResponseEntity<Boolean> response = productResource.deleteById(productId);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		verify(productService).deleteById(anyInt());
	}
}

