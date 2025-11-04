package com.selimhorri.app.e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("E2E Test 5: Shopping Cart to Order Conversion Flow")
class ShoppingCartToOrderE2ETest {
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	private String getBaseUrl() {
		return "http://localhost:" + port;
	}
	
	@Test
	@DisplayName("E2E Test 5: User adds multiple products to cart and converts to order")
	void testShoppingCartToOrder() {
		// Step 1: User registration
		UserDto user = UserDto.builder()
			.firstName("Cart")
			.lastName("User")
			.email("cart.user@example.com")
			.phone("1234567890")
			.build();
		
		ResponseEntity<UserDto> userResponse = restTemplate.postForEntity(
			getBaseUrl() + "/api/users",
			user,
			UserDto.class
		);
		
		assertEquals(HttpStatus.OK, userResponse.getStatusCode());
		UserDto registeredUser = userResponse.getBody();
		assertNotNull(registeredUser);
		
		// Step 2: Add multiple products to catalog
		ProductDto product1 = ProductDto.builder()
			.productTitle("Cart Product 1")
			.priceUnit(19.99)
			.quantity(100)
			.build();
		
		ProductDto product2 = ProductDto.builder()
			.productTitle("Cart Product 2")
			.priceUnit(29.99)
			.quantity(100)
			.build();
		
		ProductDto product3 = ProductDto.builder()
			.productTitle("Cart Product 3")
			.priceUnit(39.99)
			.quantity(100)
			.build();
		
		ResponseEntity<ProductDto> p1Response = restTemplate.postForEntity(
			getBaseUrl() + "/api/products",
			product1,
			ProductDto.class
		);
		
		ResponseEntity<ProductDto> p2Response = restTemplate.postForEntity(
			getBaseUrl() + "/api/products",
			product2,
			ProductDto.class
		);
		
		ResponseEntity<ProductDto> p3Response = restTemplate.postForEntity(
			getBaseUrl() + "/api/products",
			product3,
			ProductDto.class
		);
		
		assertEquals(HttpStatus.OK, p1Response.getStatusCode());
		assertEquals(HttpStatus.OK, p2Response.getStatusCode());
		assertEquals(HttpStatus.OK, p3Response.getStatusCode());
		
		// Step 3: Browse products (view catalog)
		ResponseEntity<DtoCollectionResponse<ProductDto>> catalogResponse = restTemplate.exchange(
			getBaseUrl() + "/api/products",
			HttpMethod.GET,
			null,
			new ParameterizedTypeReference<DtoCollectionResponse<ProductDto>>() {}
		);
		
		assertEquals(HttpStatus.OK, catalogResponse.getStatusCode());
		List<ProductDto> catalog = catalogResponse.getBody().getCollection();
		assertNotNull(catalog);
		
		// Step 4: Calculate total and create order
		Double total = p1Response.getBody().getPriceUnit()
			+ p2Response.getBody().getPriceUnit()
			+ p3Response.getBody().getPriceUnit();
		
		OrderDto order = OrderDto.builder()
			.orderDesc("Cart to order conversion test")
			.orderFee(total)
			.build();
		
		ResponseEntity<OrderDto> orderResponse = restTemplate.postForEntity(
			getBaseUrl() + "/api/orders",
			order,
			OrderDto.class
		);
		
		// Verify order creation with cart total
		assertEquals(HttpStatus.OK, orderResponse.getStatusCode());
		OrderDto createdOrder = orderResponse.getBody();
		assertNotNull(createdOrder);
		assertEquals("Cart to order conversion test", createdOrder.getOrderDesc());
		assertEquals(total, createdOrder.getOrderFee());
	}
}

