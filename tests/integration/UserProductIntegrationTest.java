package com.selimhorri.app.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.dto.UserDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("User-Product Service Integration Tests")
class UserProductIntegrationTest {
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	private String getBaseUrl() {
		return "http://localhost:" + port;
	}
	
	@Test
	@DisplayName("Integration Test 1: User Service should create user, Product Service should create product")
	void testUserAndProductCreation() {
		// Given - Create User
		UserDto userDto = UserDto.builder()
			.firstName("Integration")
			.lastName("Test")
			.email("integration.test@example.com")
			.phone("1234567890")
			.build();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<UserDto> userRequest = new HttpEntity<>(userDto, headers);
		
		// When - Create user via user-service
		ResponseEntity<UserDto> userResponse = restTemplate.exchange(
			getBaseUrl() + "/api/users",
			HttpMethod.POST,
			userRequest,
			UserDto.class
		);
		
		// Then - Verify user creation
		assertEquals(HttpStatus.OK, userResponse.getStatusCode());
		assertNotNull(userResponse.getBody());
		
		// Given - Create Product
		ProductDto productDto = ProductDto.builder()
			.productTitle("Integration Test Product")
			.priceUnit(99.99)
			.quantity(100)
			.build();
		
		HttpEntity<ProductDto> productRequest = new HttpEntity<>(productDto, headers);
		
		// When - Create product via product-service
		ResponseEntity<ProductDto> productResponse = restTemplate.exchange(
			getBaseUrl() + "/api/products",
			HttpMethod.POST,
			productRequest,
			ProductDto.class
		);
		
		// Then - Verify product creation
		assertEquals(HttpStatus.OK, productResponse.getStatusCode());
		assertNotNull(productResponse.getBody());
	}
	
	@Test
	@DisplayName("Integration Test 2: Should retrieve user and product data correctly")
	void testRetrieveUserAndProduct() {
		// Given - Create User and Product first
		UserDto userDto = UserDto.builder()
			.firstName("Retrieve")
			.lastName("Test")
			.email("retrieve.test@example.com")
			.build();
		
		ProductDto productDto = ProductDto.builder()
			.productName("Retrieve Test Product")
			.productPrice(BigDecimal.valueOf(199.99))
			.build();
		
		// When - Create entities
		ResponseEntity<UserDto> createdUser = restTemplate.postForEntity(
			getBaseUrl() + "/api/users",
			userDto,
			UserDto.class
		);
		
		ResponseEntity<ProductDto> createdProduct = restTemplate.postForEntity(
			getBaseUrl() + "/api/products",
			productDto,
			ProductDto.class
		);
		
		// Then - Retrieve and verify
		ResponseEntity<UserDto> retrievedUser = restTemplate.getForEntity(
			getBaseUrl() + "/api/users/" + createdUser.getBody().getUserId(),
			UserDto.class
		);
		
		ResponseEntity<ProductDto> retrievedProduct = restTemplate.getForEntity(
			getBaseUrl() + "/api/products/" + createdProduct.getBody().getProductId(),
			ProductDto.class
		);
		
		assertEquals(HttpStatus.OK, retrievedUser.getStatusCode());
		assertEquals(HttpStatus.OK, retrievedProduct.getStatusCode());
		assertNotNull(retrievedUser.getBody());
		assertNotNull(retrievedProduct.getBody());
	}
}

