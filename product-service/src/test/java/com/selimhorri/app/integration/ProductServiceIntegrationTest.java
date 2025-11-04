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

import com.selimhorri.app.domain.Category;
import com.selimhorri.app.domain.Product;
import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.repository.CategoryRepository;
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
	private CategoryRepository categoryRepository;
	
	@Autowired
	private ProductService productService;
	
	private Product testProduct;
	private Category testCategory;
	
	@BeforeEach
	void setUp() {
		// Find or create a category from migrations (categories 1-3 exist)
		testCategory = categoryRepository.findById(1)
			.orElseGet(() -> {
				Category cat = Category.builder()
					.categoryTitle("Test Category")
					.build();
				return categoryRepository.save(cat);
			});
		
		testProduct = Product.builder()
			.productTitle("Test Laptop")
			.priceUnit(999.99)
			.quantity(10)
			.category(testCategory)
			.build();
		testProduct = productRepository.save(testProduct);
	}
	
	@Test
	@DisplayName("Integration Test 1: Should create and retrieve product from database")
	void testCreateAndRetrieveProduct() {
		// Given - Get category from database (categories 1-3 exist from migrations)
		Category category = categoryRepository.findById(1)
			.orElseGet(() -> categoryRepository.findAll().get(0));
		
		CategoryDto categoryDto = CategoryDto.builder()
			.categoryId(category.getCategoryId())
			.categoryTitle(category.getCategoryTitle())
			.imageUrl(category.getImageUrl())
			.build();
		
		ProductDto productDto = ProductDto.builder()
			.productTitle("Gaming Mouse")
			.priceUnit(49.99)
			.quantity(50)
			.categoryDto(categoryDto)
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
		// Given - Build CategoryDto from testCategory
		CategoryDto categoryDto = CategoryDto.builder()
			.categoryId(testCategory.getCategoryId())
			.categoryTitle(testCategory.getCategoryTitle())
			.imageUrl(testCategory.getImageUrl())
			.build();
		
		ProductDto updateDto = ProductDto.builder()
			.productId(testProduct.getProductId())
			.productTitle(testProduct.getProductTitle())
			.priceUnit(testProduct.getPriceUnit())
			.quantity(25)
			.categoryDto(categoryDto)
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
		// Given - Get category from database
		Category category = categoryRepository.findById(1)
			.orElseGet(() -> categoryRepository.findAll().get(0));
		
		Product anotherProduct = Product.builder()
			.productTitle("Keyboard")
			.priceUnit(79.99)
			.quantity(30)
			.category(category)
			.build();
		productRepository.save(anotherProduct);
		
		// When
		List<ProductDto> products = productService.findAll();
		
		// Then
		assertNotNull(products);
		assertTrue(products.size() >= 2);
	}
}

