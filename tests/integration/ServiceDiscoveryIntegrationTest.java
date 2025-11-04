package com.selimhorri.app.integration;

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
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.dto.response.collection.DtoCollectionResponse;

/**
 * Pruebas de integración que validan el descubrimiento de servicios
 * Estas pruebas verifican que los servicios pueden ser descubiertos y accedidos
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Service Discovery Integration Tests")
class ServiceDiscoveryIntegrationTest {
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	private String getBaseUrl() {
		return "http://localhost:" + port;
	}
	
	@Test
	@DisplayName("Integration Test 11: Verify services are discoverable and accessible")
	void testServicesAreDiscoverable() {
		// Test User Service is accessible
		ResponseEntity<DtoCollectionResponse<UserDto>> usersResponse = restTemplate.exchange(
			getBaseUrl() + "/api/users",
			HttpMethod.GET,
			null,
			new ParameterizedTypeReference<DtoCollectionResponse<UserDto>>() {}
		);
		
		assertEquals(HttpStatus.OK, usersResponse.getStatusCode());
		assertNotNull(usersResponse.getBody());
		
		// Test Product Service is accessible
		ResponseEntity<DtoCollectionResponse<ProductDto>> productsResponse = restTemplate.exchange(
			getBaseUrl() + "/api/products",
			HttpMethod.GET,
			null,
			new ParameterizedTypeReference<DtoCollectionResponse<ProductDto>>() {}
		);
		
		assertEquals(HttpStatus.OK, productsResponse.getStatusCode());
		assertNotNull(productsResponse.getBody());
	}
	
	@Test
	@DisplayName("Integration Test 12: Verify service health endpoints")
	void testServiceHealthEndpoints() {
		// This test verifies that services are healthy and responding
		// In a real scenario, we would check actuator health endpoints
		
		// Create entities to verify services are working
		UserDto user = UserDto.builder()
			.firstName("Health")
			.lastName("Check")
			.email("health.check@example.com")
			.build();
		
		ResponseEntity<UserDto> userResponse = restTemplate.postForEntity(
			getBaseUrl() + "/api/users",
			user,
			UserDto.class
		);
		
		assertEquals(HttpStatus.OK, userResponse.getStatusCode());
		assertNotNull(userResponse.getBody());
		
		// Verify we can retrieve the created entity
		ResponseEntity<UserDto> retrievedUser = restTemplate.exchange(
			getBaseUrl() + "/api/users/" + userResponse.getBody().getUserId(),
			HttpMethod.GET,
			null,
			UserDto.class
		);
		
		assertEquals(HttpStatus.OK, retrievedUser.getStatusCode());
		assertNotNull(retrievedUser.getBody());
	}
}

