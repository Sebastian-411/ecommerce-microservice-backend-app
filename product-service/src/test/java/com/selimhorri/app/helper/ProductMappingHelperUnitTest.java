package com.selimhorri.app.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.selimhorri.app.domain.Category;
import com.selimhorri.app.domain.Product;
import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.dto.ProductDto;

@DisplayName("ProductMappingHelper Unit Tests")
class ProductMappingHelperUnitTest {
	
	private Product product;
	private ProductDto productDto;
	private Category category;
	private CategoryDto categoryDto;
	
	@BeforeEach
	void setUp() {
		category = Category.builder()
			.categoryId(1)
			.categoryTitle("Electronics")
			.imageUrl("http://example.com/category.jpg")
			.build();
		
		product = Product.builder()
			.productId(1)
			.productTitle("Laptop")
			.imageUrl("http://example.com/product.jpg")
			.sku("LAP-001")
			.priceUnit(999.99)
			.quantity(10)
			.category(category)
			.build();
		
		categoryDto = CategoryDto.builder()
			.categoryId(1)
			.categoryTitle("Electronics")
			.imageUrl("http://example.com/category.jpg")
			.build();
		
		productDto = ProductDto.builder()
			.productId(1)
			.productTitle("Laptop")
			.imageUrl("http://example.com/product.jpg")
			.sku("LAP-001")
			.priceUnit(999.99)
			.quantity(10)
			.categoryDto(categoryDto)
			.build();
	}
	
	@Test
	@DisplayName("Test 1: Should map Product to ProductDto correctly")
	void testMapProductToProductDto() {
		// When
		ProductDto result = ProductMappingHelper.map(product);
		
		// Then
		assertNotNull(result);
		assertEquals(product.getProductId(), result.getProductId());
		assertEquals(product.getProductTitle(), result.getProductTitle());
		assertEquals(product.getPriceUnit(), result.getPriceUnit());
		assertEquals(product.getQuantity(), result.getQuantity());
		assertNotNull(result.getCategoryDto());
		assertEquals(product.getCategory().getCategoryId(), result.getCategoryDto().getCategoryId());
	}
	
	@Test
	@DisplayName("Test 2: Should map ProductDto to Product correctly")
	void testMapProductDtoToProduct() {
		// When
		Product result = ProductMappingHelper.map(productDto);
		
		// Then
		assertNotNull(result);
		assertEquals(productDto.getProductId(), result.getProductId());
		assertEquals(productDto.getProductTitle(), result.getProductTitle());
		assertEquals(productDto.getPriceUnit(), result.getPriceUnit());
		assertEquals(productDto.getQuantity(), result.getQuantity());
		assertNotNull(result.getCategory());
		assertEquals(productDto.getCategoryDto().getCategoryId(), result.getCategory().getCategoryId());
	}
}

