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

import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.dto.response.collection.DtoCollectionResponse;

/**
 * Pruebas E2E que validan flujos completos del e-commerce
 * Estas pruebas simulan el comportamiento real de un usuario en el sistema
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("E2E Test 6: Full E-Commerce Flow")
class FullECommerceFlowE2ETest {
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	private String getBaseUrl() {
		return "http://localhost:" + port;
	}
	
	@Test
	@DisplayName("E2E Test 6: Complete e-commerce flow - Registration to Order Placement")
	void testCompleteECommerceFlow() {
		// Step 1: User Registration
		UserDto newUser = UserDto.builder()
			.firstName("ECommerce")
			.lastName("Customer")
			.email("ecommerce.customer@example.com")
			.phone("1234567890")
			.build();
		
		ResponseEntity<UserDto> registrationResponse = restTemplate.postForEntity(
			getBaseUrl() + "/api/users",
			newUser,
			UserDto.class
		);
		
		assertEquals(HttpStatus.OK, registrationResponse.getStatusCode());
		UserDto registeredUser = registrationResponse.getBody();
		assertNotNull(registeredUser);
		assertNotNull(registeredUser.getUserId());
		
		// Step 2: Browse Products (Product Catalog)
		ResponseEntity<DtoCollectionResponse<ProductDto>> catalogResponse = restTemplate.exchange(
			getBaseUrl() + "/api/products",
			HttpMethod.GET,
			null,
			new ParameterizedTypeReference<DtoCollectionResponse<ProductDto>>() {}
		);
		
		assertEquals(HttpStatus.OK, catalogResponse.getStatusCode());
		List<ProductDto> catalog = catalogResponse.getBody().getCollection();
		assertNotNull(catalog);
		
		// Step 3: View Product Details
		if (!catalog.isEmpty()) {
			ProductDto selectedProduct = catalog.get(0);
			ResponseEntity<ProductDto> productDetailsResponse = restTemplate.exchange(
				getBaseUrl() + "/api/products/" + selectedProduct.getProductId(),
				HttpMethod.GET,
				null,
				ProductDto.class
			);
			
			assertEquals(HttpStatus.OK, productDetailsResponse.getStatusCode());
			assertNotNull(productDetailsResponse.getBody());
		}
		
		// Step 4: Add Products to Order
		ProductDto product1 = ProductDto.builder()
			.productTitle("ECommerce Product 1")
			.priceUnit(49.99)
			.quantity(100)
			.build();
		
		ProductDto product2 = ProductDto.builder()
			.productTitle("ECommerce Product 2")
			.priceUnit(59.99)
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
		
		// Step 5: Place Order
		Double orderTotal = product1Response.getBody().getPriceUnit()
			+ product2Response.getBody().getPriceUnit();
		
		OrderDto order = OrderDto.builder()
			.orderDesc("Complete e-commerce flow order")
			.orderFee(orderTotal)
			.build();
		
		ResponseEntity<OrderDto> orderResponse = restTemplate.postForEntity(
			getBaseUrl() + "/api/orders",
			order,
			OrderDto.class
		);
		
		// Step 6: Verify Complete Flow
		assertEquals(HttpStatus.OK, orderResponse.getStatusCode());
		OrderDto placedOrder = orderResponse.getBody();
		assertNotNull(placedOrder);
		assertEquals(orderTotal, placedOrder.getOrderFee());
		
		// Step 7: Verify User Profile Access
		ResponseEntity<UserDto> userProfileResponse = restTemplate.exchange(
			getBaseUrl() + "/api/users/" + registeredUser.getUserId(),
			HttpMethod.GET,
			null,
			UserDto.class
		);
		
		assertEquals(HttpStatus.OK, userProfileResponse.getStatusCode());
		assertNotNull(userProfileResponse.getBody());
	}
	
	@Test
	@DisplayName("E2E Test 7: Multi-service interaction - User creates order with products")
	void testMultiServiceInteraction() {
		// Create user
		UserDto user = UserDto.builder()
			.firstName("MultiService")
			.lastName("User")
			.email("multiservice.user@example.com")
			.phone("1234567890")
			.build();
		
		ResponseEntity<UserDto> userResponse = restTemplate.postForEntity(
			getBaseUrl() + "/api/users",
			user,
			UserDto.class
		);
		
		assertEquals(HttpStatus.OK, userResponse.getStatusCode());
		
		// Create multiple products
		ProductDto[] products = {
			ProductDto.builder().productTitle("Product A").priceUnit(19.99).quantity(50).build(),
			ProductDto.builder().productTitle("Product B").priceUnit(29.99).quantity(50).build(),
			ProductDto.builder().productTitle("Product C").priceUnit(39.99).quantity(50).build()
		};
		
		Double totalAmount = 0.0;
		for (ProductDto product : products) {
			ResponseEntity<ProductDto> response = restTemplate.postForEntity(
				getBaseUrl() + "/api/products",
				product,
				ProductDto.class
			);
			
			assertEquals(HttpStatus.OK, response.getStatusCode());
			totalAmount += response.getBody().getPriceUnit();
		}
		
		// Create order
		OrderDto order = OrderDto.builder()
			.orderDesc("Multi-service interaction order")
			.orderFee(totalAmount)
			.build();
		
		ResponseEntity<OrderDto> orderResponse = restTemplate.postForEntity(
			getBaseUrl() + "/api/orders",
			order,
			OrderDto.class
		);
		
		assertEquals(HttpStatus.OK, orderResponse.getStatusCode());
		assertNotNull(orderResponse.getBody());
	}
}

