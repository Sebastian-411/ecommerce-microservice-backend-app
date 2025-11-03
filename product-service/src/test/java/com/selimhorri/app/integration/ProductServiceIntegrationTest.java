package com.selimhorri.app.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.selimhorri.app.domain.Product;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.repository.ProductRepository;
import com.selimhorri.app.service.ProductService;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DisplayName("ProductService Integration Tests")
class ProductServiceIntegrationTest {
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private ProductService productService;
	
	private Product testProduct;
	
	@BeforeEach
	void setUp() {
		testProduct = Product.builder()
			.productTitle("Test Laptop")
			.priceUnit(999.99)
			.quantity(10)
			.build();
		testProduct = productRepository.save(testProduct);
	}
	
	@Test
	@DisplayName("Integration Test 1: Should create and retrieve product from database")
	void testCreateAndRetrieveProduct() {
		// Given
		ProductDto productDto = ProductDto.builder()
			.productTitle("Gaming Mouse")
			.priceUnit(49.99)
			.quantity(50)
			.build();
		
		// When
		ProductDto saved = productService.save(productDto);
		ProductDto retrieved = productService.findById(saved.getProductId());
		
		// Then
		assertNotNull(retrieved);
		assertEquals("Gaming Mouse", retrieved.getProductTitle());
		assertEquals(49.99, retrieved.getPriceUnit());
	}
	
	@Test
	@DisplayName("Integration Test 2: Should update product quantity")
	void testUpdateProductStock() {
		// Given
		ProductDto updateDto = ProductDto.builder()
			.productId(testProduct.getProductId())
			.productTitle(testProduct.getProductTitle())
			.quantity(25)
			.build();
		
		// When
		ProductDto updated = productService.update(updateDto);
		
		// Then
		assertNotNull(updated);
		assertEquals(25, updated.getQuantity());
	}
	
	@Test
	@DisplayName("Integration Test 3: Should find all products from database")
	void testFindAllProducts() {
		// Given
		Product anotherProduct = Product.builder()
			.productTitle("Keyboard")
			.priceUnit(79.99)
			.quantity(30)
			.build();
		productRepository.save(anotherProduct);
		
		// When
		List<ProductDto> products = productService.findAll();
		
		// Then
		assertNotNull(products);
		assertTrue(products.size() >= 2);
	}
}

