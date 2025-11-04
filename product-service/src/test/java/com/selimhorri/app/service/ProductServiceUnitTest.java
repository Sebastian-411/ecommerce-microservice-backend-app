package com.selimhorri.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

import com.selimhorri.app.domain.Category;
import com.selimhorri.app.domain.Product;
import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.exception.wrapper.ProductNotFoundException;
import com.selimhorri.app.repository.ProductRepository;
import com.selimhorri.app.service.impl.ProductServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Unit Tests")
class ProductServiceUnitTest {
	
	@Mock
	private ProductRepository productRepository;
	
	@InjectMocks
	private ProductServiceImpl productService;
	
	private ProductDto productDto;
	private Product product;
	private Category category;
	private CategoryDto categoryDto;
	
	@BeforeEach
	void setUp() {
		category = Category.builder()
			.categoryId(1)
			.categoryTitle("Electronics")
			.imageUrl("http://example.com/electronics.jpg")
			.build();
		
		categoryDto = CategoryDto.builder()
			.categoryId(1)
			.categoryTitle("Electronics")
			.imageUrl("http://example.com/electronics.jpg")
			.build();
		
		productDto = ProductDto.builder()
			.productId(1)
			.productTitle("Laptop")
			.priceUnit(999.99)
			.quantity(10)
			.categoryDto(categoryDto)
			.build();
		
		product = Product.builder()
			.productId(1)
			.productTitle("Laptop")
			.priceUnit(999.99)
			.quantity(10)
			.category(category)
			.build();
	}
	
	@Test
	@DisplayName("Test 1: Should find all products successfully")
	void testFindAll_ShouldReturnListOfProducts() {
		// Given - Create two different products with different IDs
		Category category2 = Category.builder()
			.categoryId(2)
			.categoryTitle("Computers")
			.imageUrl("http://example.com/computers.jpg")
			.build();
		
		Product product2 = Product.builder()
			.productId(2)
			.productTitle("Desktop")
			.priceUnit(1299.99)
			.quantity(5)
			.category(category2)
			.build();
		
		List<Product> products = Arrays.asList(product, product2);
		when(productRepository.findAll()).thenReturn(products);
		
		// When
		List<ProductDto> result = productService.findAll();
		
		// Then
		assertNotNull(result);
		assertEquals(2, result.size());
		verify(productRepository).findAll();
	}
	
	@Test
	@DisplayName("Test 2: Should find product by id successfully")
	void testFindById_ShouldReturnProductDto() {
		// Given
		Integer productId = 1;
		when(productRepository.findById(productId)).thenReturn(Optional.of(product));
		
		// When
		ProductDto result = productService.findById(productId);
		
		// Then
		assertNotNull(result);
		assertEquals(productId, result.getProductId());
		assertEquals("Laptop", result.getProductTitle());
		verify(productRepository).findById(productId);
	}
	
	@Test
	@DisplayName("Test 3: Should throw exception when product not found")
	void testFindById_WhenProductNotFound_ShouldThrowException() {
		// Given
		Integer productId = 999;
		when(productRepository.findById(productId)).thenReturn(Optional.empty());
		
		// When & Then
		assertThrows(ProductNotFoundException.class, () -> {
			productService.findById(productId);
		});
		verify(productRepository).findById(productId);
	}
	
	@Test
	@DisplayName("Test 4: Should save product successfully")
	void testSave_ShouldReturnSavedProductDto() {
		// Given
		when(productRepository.save(any(Product.class))).thenReturn(product);
		
		// When
		ProductDto result = productService.save(productDto);
		
		// Then
		assertNotNull(result);
		assertEquals(productDto.getProductTitle(), result.getProductTitle());
		verify(productRepository).save(any(Product.class));
	}
	
	@Test
	@DisplayName("Test 5: Should update product successfully")
	void testUpdate_ShouldReturnUpdatedProductDto() {
		// Given
		ProductDto updatedDto = ProductDto.builder()
			.productId(1)
			.productTitle("Gaming Laptop")
			.priceUnit(1299.99)
			.quantity(10)
			.categoryDto(categoryDto)
			.build();
		
		Product updatedProduct = Product.builder()
			.productId(1)
			.productTitle("Gaming Laptop")
			.priceUnit(1299.99)
			.quantity(10)
			.category(category)
			.build();
		
		when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);
		
		// When
		ProductDto result = productService.update(updatedDto);
		
		// Then
		assertNotNull(result);
		assertEquals("Gaming Laptop", result.getProductTitle());
		verify(productRepository).save(any(Product.class));
	}
	
	@Test
	@DisplayName("Test 6: Should update product with ID successfully")
	void testUpdateWithId_ShouldReturnUpdatedProductDto() {
		// Given
		Integer productId = 1;
		ProductDto updatedDto = ProductDto.builder()
			.productId(1)
			.productTitle("Updated Laptop")
			.priceUnit(1199.99)
			.quantity(10)
			.categoryDto(categoryDto)
			.build();
		
		when(productRepository.findById(productId)).thenReturn(Optional.of(product));
		when(productRepository.save(any(Product.class))).thenReturn(product);
		
		// When
		ProductDto result = productService.update(productId, updatedDto);
		
		// Then
		assertNotNull(result);
		verify(productRepository).findById(productId);
		verify(productRepository).save(any(Product.class));
	}
	
	@Test
	@DisplayName("Test 7: Should delete product by ID successfully")
	void testDeleteById_ShouldDeleteProduct() {
		// Given
		Integer productId = 1;
		when(productRepository.findById(productId)).thenReturn(Optional.of(product));
		
		// When
		productService.deleteById(productId);
		
		// Then
		verify(productRepository).findById(productId);
		verify(productRepository).delete(any(Product.class));
	}
}

