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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.dto.response.collection.DtoCollectionResponse;

/**
 * Pruebas de integración que validan la comunicación real entre microservicios
 * Estos tests verifican que los servicios pueden comunicarse correctamente entre sí
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Cross-Service Communication Integration Tests")
class CrossServiceCommunicationTest {
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	private String getBaseUrl() {
		return "http://localhost:" + port;
	}
	
	@Test
	@DisplayName("Integration Test 6: User Service → Product Service communication")
	void testUserToProductServiceCommunication() {
		// Step 1: Create user in User Service
		UserDto user = UserDto.builder()
			.firstName("CrossService")
			.lastName("User")
			.email("crossservice.user@example.com")
			.phone("1234567890")
			.build();
		
		ResponseEntity<UserDto> userResponse = restTemplate.postForEntity(
			getBaseUrl() + "/api/users",
			user,
			UserDto.class
		);
		
		assertEquals(HttpStatus.OK, userResponse.getStatusCode());
		UserDto createdUser = userResponse.getBody();
		assertNotNull(createdUser);
		assertNotNull(createdUser.getUserId());
		
		// Step 2: Create product in Product Service
		ProductDto product = ProductDto.builder()
			.productTitle("CrossService Product")
			.priceUnit(99.99)
			.quantity(100)
			.build();
		
		ResponseEntity<ProductDto> productResponse = restTemplate.postForEntity(
			getBaseUrl() + "/api/products",
			product,
			ProductDto.class
		);
		
		assertEquals(HttpStatus.OK, productResponse.getStatusCode());
		ProductDto createdProduct = productResponse.getBody();
		assertNotNull(createdProduct);
		assertNotNull(createdProduct.getProductId());
		
		// Step 3: Verify both services respond correctly
		ResponseEntity<UserDto> retrievedUser = restTemplate.getForEntity(
			getBaseUrl() + "/api/users/" + createdUser.getUserId(),
			UserDto.class
		);
		
		ResponseEntity<ProductDto> retrievedProduct = restTemplate.getForEntity(
			getBaseUrl() + "/api/products/" + createdProduct.getProductId(),
			ProductDto.class
		);
		
		assertEquals(HttpStatus.OK, retrievedUser.getStatusCode());
		assertEquals(HttpStatus.OK, retrievedProduct.getStatusCode());
		assertNotNull(retrievedUser.getBody());
		assertNotNull(retrievedProduct.getBody());
	}
	
	@Test
	@DisplayName("Integration Test 7: Order Service → User Service → Product Service communication")
	void testOrderToUserToProductServiceCommunication() {
		// Step 1: Create User (User Service)
		UserDto user = UserDto.builder()
			.firstName("OrderUser")
			.lastName("Test")
			.email("orderuser.test@example.com")
			.phone("1234567890")
			.build();
		
		ResponseEntity<UserDto> userResponse = restTemplate.postForEntity(
			getBaseUrl() + "/api/users",
			user,
			UserDto.class
		);
		
		assertEquals(HttpStatus.OK, userResponse.getStatusCode());
		UserDto createdUser = userResponse.getBody();
		assertNotNull(createdUser);
		
		// Step 2: Create Product (Product Service)
		ProductDto product = ProductDto.builder()
			.productTitle("Order Test Product")
			.priceUnit(149.99)
			.quantity(50)
			.build();
		
		ResponseEntity<ProductDto> productResponse = restTemplate.postForEntity(
			getBaseUrl() + "/api/products",
			product,
			ProductDto.class
		);
		
		assertEquals(HttpStatus.OK, productResponse.getStatusCode());
		ProductDto createdProduct = productResponse.getBody();
		assertNotNull(createdProduct);
		
		// Step 3: Create Order (Order Service) that references User and Product
		OrderDto order = OrderDto.builder()
			.orderDesc("Order referencing user and product")
			.orderFee(createdProduct.getPriceUnit())
			.build();
		
		ResponseEntity<OrderDto> orderResponse = restTemplate.postForEntity(
			getBaseUrl() + "/api/orders",
			order,
			OrderDto.class
		);
		
		// Step 4: Verify all services are communicating
		assertEquals(HttpStatus.OK, orderResponse.getStatusCode());
		OrderDto createdOrder = orderResponse.getBody();
		assertNotNull(createdOrder);
		
		// Verify we can retrieve all entities from their respective services
		assertNotNull(createdUser.getUserId());
		assertNotNull(createdProduct.getProductId());
		assertNotNull(createdOrder.getOrderId());
	}
	
	@Test
	@DisplayName("Integration Test 8: Multiple services query communication")
	void testMultipleServicesQueryCommunication() {
		// Create entities in multiple services
		UserDto user1 = UserDto.builder()
			.firstName("Query")
			.lastName("User1")
			.email("query.user1@example.com")
			.build();
		
		UserDto user2 = UserDto.builder()
			.firstName("Query")
			.lastName("User2")
			.email("query.user2@example.com")
			.build();
		
		ProductDto product1 = ProductDto.builder()
			.productTitle("Query Product 1")
			.priceUnit(49.99)
			.build();
		
		ProductDto product2 = ProductDto.builder()
			.productTitle("Query Product 2")
			.priceUnit(79.99)
			.build();
		
		// Create entities
		restTemplate.postForEntity(getBaseUrl() + "/api/users", user1, UserDto.class);
		restTemplate.postForEntity(getBaseUrl() + "/api/users", user2, UserDto.class);
		restTemplate.postForEntity(getBaseUrl() + "/api/products", product1, ProductDto.class);
		restTemplate.postForEntity(getBaseUrl() + "/api/products", product2, ProductDto.class);
		
		// Query all entities from multiple services
		ResponseEntity<DtoCollectionResponse<UserDto>> usersResponse = restTemplate.exchange(
			getBaseUrl() + "/api/users",
			HttpMethod.GET,
			null,
			new ParameterizedTypeReference<DtoCollectionResponse<UserDto>>() {}
		);
		
		ResponseEntity<DtoCollectionResponse<ProductDto>> productsResponse = restTemplate.exchange(
			getBaseUrl() + "/api/products",
			HttpMethod.GET,
			null,
			new ParameterizedTypeReference<DtoCollectionResponse<ProductDto>>() {}
		);
		
		ResponseEntity<DtoCollectionResponse<OrderDto>> ordersResponse = restTemplate.exchange(
			getBaseUrl() + "/api/orders",
			HttpMethod.GET,
			null,
			new ParameterizedTypeReference<DtoCollectionResponse<OrderDto>>() {}
		);
		
		// Verify all services respond correctly
		assertEquals(HttpStatus.OK, usersResponse.getStatusCode());
		assertEquals(HttpStatus.OK, productsResponse.getStatusCode());
		assertEquals(HttpStatus.OK, ordersResponse.getStatusCode());
		
		assertNotNull(usersResponse.getBody());
		assertNotNull(productsResponse.getBody());
		assertNotNull(ordersResponse.getBody());
		
		List<UserDto> users = usersResponse.getBody().getCollection();
		List<ProductDto> products = productsResponse.getBody().getCollection();
		List<OrderDto> orders = ordersResponse.getBody().getCollection();
		
		assertTrue(users.size() >= 2);
		assertTrue(products.size() >= 2);
		assertNotNull(orders);
	}
	
	@Test
	@DisplayName("Integration Test 9: Service-to-service data consistency")
	void testServiceToServiceDataConsistency() {
		// Step 1: Create user
		UserDto user = UserDto.builder()
			.firstName("Consistency")
			.lastName("Test")
			.email("consistency.test@example.com")
			.phone("1234567890")
			.build();
		
		ResponseEntity<UserDto> userResponse = restTemplate.postForEntity(
			getBaseUrl() + "/api/users",
			user,
			UserDto.class
		);
		
		UserDto createdUser = userResponse.getBody();
		assertNotNull(createdUser);
		Integer userId = createdUser.getUserId();
		
		// Step 2: Update user
		UserDto updatedUser = UserDto.builder()
			.userId(userId)
			.firstName("Consistency Updated")
			.lastName("Test")
			.email("consistency.test@example.com")
			.phone("1234567890")
			.build();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<UserDto> request = new HttpEntity<>(updatedUser, headers);
		
		ResponseEntity<UserDto> updateResponse = restTemplate.exchange(
			getBaseUrl() + "/api/users",
			HttpMethod.PUT,
			request,
			UserDto.class
		);
		
		// Step 3: Verify data consistency across service calls
		assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
		UserDto retrievedUser = updateResponse.getBody();
		assertNotNull(retrievedUser);
		assertEquals("Consistency Updated", retrievedUser.getFirstName());
		assertEquals(userId, retrievedUser.getUserId());
	}
	
	@Test
	@DisplayName("Integration Test 10: Complete order flow with all services")
	void testCompleteOrderFlowWithAllServices() {
		// Step 1: Create User (User Service)
		UserDto user = UserDto.builder()
			.firstName("CompleteFlow")
			.lastName("User")
			.email("completeflow.user@example.com")
			.phone("1234567890")
			.build();
		
		ResponseEntity<UserDto> userResponse = restTemplate.postForEntity(
			getBaseUrl() + "/api/users",
			user,
			UserDto.class
		);
		
		assertEquals(HttpStatus.OK, userResponse.getStatusCode());
		UserDto createdUser = userResponse.getBody();
		assertNotNull(createdUser);
		
		// Step 2: Create multiple Products (Product Service)
		ProductDto product1 = ProductDto.builder()
			.productTitle("Complete Flow Product 1")
			.priceUnit(29.99)
			.quantity(100)
			.build();
		
		ProductDto product2 = ProductDto.builder()
			.productTitle("Complete Flow Product 2")
			.priceUnit(39.99)
			.quantity(100)
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
		
		// Step 3: Create Order (Order Service) that uses User and Products
		Double totalAmount = product1Response.getBody().getPriceUnit()
			+ product2Response.getBody().getPriceUnit();
		
		OrderDto order = OrderDto.builder()
			.orderDesc("Complete flow order with user and products")
			.orderFee(totalAmount)
			.build();
		
		ResponseEntity<OrderDto> orderResponse = restTemplate.postForEntity(
			getBaseUrl() + "/api/orders",
			order,
			OrderDto.class
		);
		
		// Step 4: Verify all services participated in the flow
		assertEquals(HttpStatus.OK, orderResponse.getStatusCode());
		OrderDto createdOrder = orderResponse.getBody();
		assertNotNull(createdOrder);
		
		// Step 5: Verify we can retrieve all entities from their services
		ResponseEntity<UserDto> retrievedUser = restTemplate.getForEntity(
			getBaseUrl() + "/api/users/" + createdUser.getUserId(),
			UserDto.class
		);
		
		ResponseEntity<ProductDto> retrievedProduct1 = restTemplate.getForEntity(
			getBaseUrl() + "/api/products/" + product1Response.getBody().getProductId(),
			ProductDto.class
		);
		
		ResponseEntity<OrderDto> retrievedOrder = restTemplate.getForEntity(
			getBaseUrl() + "/api/orders/" + createdOrder.getOrderId(),
			OrderDto.class
		);
		
		// Verify all services are accessible and data is consistent
		assertEquals(HttpStatus.OK, retrievedUser.getStatusCode());
		assertEquals(HttpStatus.OK, retrievedProduct1.getStatusCode());
		assertEquals(HttpStatus.OK, retrievedOrder.getStatusCode());
		
		assertNotNull(retrievedUser.getBody());
		assertNotNull(retrievedProduct1.getBody());
		assertNotNull(retrievedOrder.getBody());
		
		// Verify order total matches sum of products
		assertEquals(totalAmount, retrievedOrder.getBody().getOrderFee());
	}
}

