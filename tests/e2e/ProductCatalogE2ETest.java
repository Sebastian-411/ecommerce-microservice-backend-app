package com.selimhorri.app.e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.dto.response.collection.DtoCollectionResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("E2E Test 2: Product Catalog Browsing Flow")
class ProductCatalogE2ETest {
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	private String getBaseUrl() {
		return "http://localhost:" + port;
	}
	
	@Test
	@DisplayName("E2E Test 2: User browses product catalog and views product details")
	void testProductCatalogBrowsing() {
		// Step 1: Get all products
		ResponseEntity<DtoCollectionResponse<ProductDto>> allProductsResponse = restTemplate.exchange(
			getBaseUrl() + "/api/products",
			HttpMethod.GET,
			null,
			new ParameterizedTypeReference<DtoCollectionResponse<ProductDto>>() {}
		);
		
		// Verify catalog retrieval
		assertEquals(HttpStatus.OK, allProductsResponse.getStatusCode());
		assertNotNull(allProductsResponse.getBody());
		List<ProductDto> products = allProductsResponse.getBody().getCollection();
		assertNotNull(products);
		
		// Step 2: Create a new product
		ProductDto newProduct = ProductDto.builder()
			.productTitle("E2E Test Product")
			.priceUnit(99.99)
			.quantity(50)
			.build();
		
		ResponseEntity<ProductDto> createResponse = restTemplate.postForEntity(
			getBaseUrl() + "/api/products",
			newProduct,
			ProductDto.class
		);
		
		// Verify product creation
		assertEquals(HttpStatus.OK, createResponse.getStatusCode());
		ProductDto createdProduct = createResponse.getBody();
		assertNotNull(createdProduct);
		assertEquals("E2E Test Product", createdProduct.getProductTitle());
		
		// Step 3: View product details
		ResponseEntity<ProductDto> productDetailsResponse = restTemplate.exchange(
			getBaseUrl() + "/api/products/" + createdProduct.getProductId(),
			HttpMethod.GET,
			null,
			ProductDto.class
		);
		
		// Verify product details
		assertEquals(HttpStatus.OK, productDetailsResponse.getStatusCode());
		ProductDto productDetails = productDetailsResponse.getBody();
		assertNotNull(productDetails);
		assertEquals(createdProduct.getProductId(), productDetails.getProductId());
		assertEquals(createdProduct.getProductTitle(), productDetails.getProductTitle());
	}
}

