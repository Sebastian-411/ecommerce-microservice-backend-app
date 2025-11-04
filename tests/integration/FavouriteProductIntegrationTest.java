package com.selimhorri.app.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

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

import com.selimhorri.app.dto.FavouriteDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.dto.UserDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Favourite-Product Service Integration Tests")
class FavouriteProductIntegrationTest {
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	private String getBaseUrl() {
		return "http://localhost:" + port;
	}
	
	@Test
	@DisplayName("Integration Test: User adds product to favourites")
	void testAddProductToFavourites() {
		// Step 1: Create User
		UserDto userDto = UserDto.builder()
			.firstName("Favourite")
			.lastName("User")
			.email("favourite.user@example.com")
			.phone("1234567890")
			.build();
		
		ResponseEntity<UserDto> userResponse = restTemplate.postForEntity(
			getBaseUrl() + "/api/users",
			userDto,
			UserDto.class
		);
		
		assertEquals(HttpStatus.OK, userResponse.getStatusCode());
		UserDto createdUser = userResponse.getBody();
		assertNotNull(createdUser);
		
		// Step 2: Create Product
		ProductDto productDto = ProductDto.builder()
			.productTitle("Favourite Product")
			.priceUnit(79.99)
			.quantity(50)
			.build();
		
		ResponseEntity<ProductDto> productResponse = restTemplate.postForEntity(
			getBaseUrl() + "/api/products",
			productDto,
			ProductDto.class
		);
		
		assertEquals(HttpStatus.OK, productResponse.getStatusCode());
		ProductDto createdProduct = productResponse.getBody();
		assertNotNull(createdProduct);
		
		// Step 3: Add to Favourites
		FavouriteDto favouriteDto = FavouriteDto.builder()
			.userId(createdUser.getUserId())
			.productId(createdProduct.getProductId())
			.likeDate(LocalDateTime.now())
			.build();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<FavouriteDto> favouriteRequest = new HttpEntity<>(favouriteDto, headers);
		
		ResponseEntity<FavouriteDto> favouriteResponse = restTemplate.exchange(
			getBaseUrl() + "/api/favourites",
			HttpMethod.POST,
			favouriteRequest,
			FavouriteDto.class
		);
		
		// Verify Favourite Creation
		assertEquals(HttpStatus.OK, favouriteResponse.getStatusCode());
		FavouriteDto createdFavourite = favouriteResponse.getBody();
		assertNotNull(createdFavourite);
		assertEquals(createdUser.getUserId(), createdFavourite.getUserId());
		assertEquals(createdProduct.getProductId(), createdFavourite.getProductId());
	}
	
	@Test
	@DisplayName("Integration Test: Retrieve user favourites")
	void testRetrieveUserFavourites() {
		// Create User
		UserDto userDto = UserDto.builder()
			.firstName("FavouriteList")
			.lastName("User")
			.email("favouritelist.user@example.com")
			.build();
		
		ResponseEntity<UserDto> userResponse = restTemplate.postForEntity(
			getBaseUrl() + "/api/users",
			userDto,
			UserDto.class
		);
		
		assertEquals(HttpStatus.OK, userResponse.getStatusCode());
		UserDto createdUser = userResponse.getBody();
		assertNotNull(createdUser);
		
		// Create Multiple Products
		ProductDto product1 = ProductDto.builder()
			.productTitle("Favourite Product 1")
			.priceUnit(29.99)
			.build();
		
		ProductDto product2 = ProductDto.builder()
			.productTitle("Favourite Product 2")
			.priceUnit(39.99)
			.build();
		
		ResponseEntity<ProductDto> product1Response = restTemplate.postForEntity(
			getBaseUrl() + "/api/products",
			product1,
			ProductDto.class
		);
		
		ResponseEntity<ProductDto> product2Response = restTemplate.postForEntity(
			getBaseUrl() + "/api/products",
			product2,
			ProductDto.class
		);
		
		assertEquals(HttpStatus.OK, product1Response.getStatusCode());
		assertEquals(HttpStatus.OK, product2Response.getStatusCode());
		
		// Add to Favourites
		FavouriteDto favourite1 = FavouriteDto.builder()
			.userId(createdUser.getUserId())
			.productId(product1Response.getBody().getProductId())
			.likeDate(LocalDateTime.now())
			.build();
		
		FavouriteDto favourite2 = FavouriteDto.builder()
			.userId(createdUser.getUserId())
			.productId(product2Response.getBody().getProductId())
			.likeDate(LocalDateTime.now())
			.build();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		restTemplate.postForEntity(
			getBaseUrl() + "/api/favourites",
			new HttpEntity<>(favourite1, headers),
			FavouriteDto.class
		);
		
		restTemplate.postForEntity(
			getBaseUrl() + "/api/favourites",
			new HttpEntity<>(favourite2, headers),
			FavouriteDto.class
		);
		
		// Retrieve All Favourites
		ResponseEntity<Object> favouritesResponse = restTemplate.getForEntity(
			getBaseUrl() + "/api/favourites",
			Object.class
		);
		
		// Verify
		assertEquals(HttpStatus.OK, favouritesResponse.getStatusCode());
		assertNotNull(favouritesResponse.getBody());
	}
}

