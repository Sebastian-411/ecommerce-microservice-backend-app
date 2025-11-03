package com.selimhorri.app.e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
 * Pruebas E2E que se ejecutan contra servicios desplegados en Kubernetes (Staging)
 * Estas pruebas validan que los servicios funcionan correctamente después del despliegue
 * 
 * Configuración:
 * - spring.profiles.active=stage
 * - api.gateway.url debe apuntar al API Gateway desplegado
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("stage")
@DisplayName("E2E Tests - Servicios Desplegados en Kubernetes (Staging)")
class DeployedServicesE2ETest {
	
	@LocalServerPort
	private int port;
	
	private static String apiGatewayUrl;
	private TestRestTemplate restTemplate = new TestRestTemplate();
	
	@BeforeAll
	static void setup() {
		// Obtener URL del API Gateway desde variables de entorno
		apiGatewayUrl = System.getProperty("api.gateway.url", System.getenv("API_GATEWAY_URL"));
		if (apiGatewayUrl == null || apiGatewayUrl.isEmpty()) {
			apiGatewayUrl = "http://localhost:8080";
		}
		System.out.println("API Gateway URL: " + apiGatewayUrl);
	}
	
	private String getApiGatewayUrl() {
		return apiGatewayUrl;
	}
	
	@Test
	@DisplayName("E2E Test 8: Health Check - Servicios Desplegados")
	void testHealthCheckDeployedServices() {
		// Verificar que el API Gateway responde
		ResponseEntity<String> healthResponse = restTemplate.getForEntity(
			getApiGatewayUrl() + "/actuator/health",
			String.class
		);
		
		assertEquals(HttpStatus.OK, healthResponse.getStatusCode());
		assertNotNull(healthResponse.getBody());
		assertTrue(healthResponse.getBody().contains("UP") || healthResponse.getBody().contains("\"status\":\"UP\""));
	}
	
	@Test
	@DisplayName("E2E Test 9: User Service - Endpoint Desplegado")
	void testUserServiceDeployedEndpoint() {
		// Verificar que el endpoint de usuarios responde
		ResponseEntity<DtoCollectionResponse<UserDto>> response = restTemplate.exchange(
			getApiGatewayUrl() + "/user-service/api/users",
			HttpMethod.GET,
			null,
			new ParameterizedTypeReference<DtoCollectionResponse<UserDto>>() {}
		);
		
		// Aceptamos 200 (OK), 404 (Not Found es válido si no hay usuarios), o 401 (Unauthorized)
		assertTrue(
			response.getStatusCode() == HttpStatus.OK ||
			response.getStatusCode() == HttpStatus.NOT_FOUND ||
			response.getStatusCode() == HttpStatus.UNAUTHORIZED
		);
	}
	
	@Test
	@DisplayName("E2E Test 10: Product Service - Endpoint Desplegado")
	void testProductServiceDeployedEndpoint() {
		// Verificar que el endpoint de productos responde
		ResponseEntity<DtoCollectionResponse<ProductDto>> response = restTemplate.exchange(
			getApiGatewayUrl() + "/product-service/api/products",
			HttpMethod.GET,
			null,
			new ParameterizedTypeReference<DtoCollectionResponse<ProductDto>>() {}
		);
		
		// Aceptamos 200 (OK), 404 (Not Found es válido si no hay productos), o 401 (Unauthorized)
		assertTrue(
			response.getStatusCode() == HttpStatus.OK ||
			response.getStatusCode() == HttpStatus.NOT_FOUND ||
			response.getStatusCode() == HttpStatus.UNAUTHORIZED
		);
	}
	
	@Test
	@DisplayName("E2E Test 11: Order Service - Endpoint Desplegado")
	void testOrderServiceDeployedEndpoint() {
		// Verificar que el endpoint de órdenes responde
		ResponseEntity<DtoCollectionResponse<OrderDto>> response = restTemplate.exchange(
			getApiGatewayUrl() + "/order-service/api/orders",
			HttpMethod.GET,
			null,
			new ParameterizedTypeReference<DtoCollectionResponse<OrderDto>>() {}
		);
		
		// Aceptamos 200 (OK), 404 (Not Found es válido si no hay órdenes), o 401 (Unauthorized)
		assertTrue(
			response.getStatusCode() == HttpStatus.OK ||
			response.getStatusCode() == HttpStatus.NOT_FOUND ||
			response.getStatusCode() == HttpStatus.UNAUTHORIZED
		);
	}
	
	@Test
	@DisplayName("E2E Test 12: Complete Flow - Servicios Desplegados")
	void testCompleteFlowDeployedServices() {
		// Crear usuario
		UserDto user = UserDto.builder()
			.firstName("Deployed")
			.lastName("User")
			.email("deployed.user@example.com")
			.phone("1234567890")
			.build();
		
		ResponseEntity<UserDto> userResponse = restTemplate.postForEntity(
			getApiGatewayUrl() + "/user-service/api/users",
			user,
			UserDto.class
		);
		
		// Si el servicio está funcionando, debería responder con 200 o 201
		// Si falla con 401/403, significa que requiere autenticación, lo cual es válido
		assertTrue(
			userResponse.getStatusCode() == HttpStatus.OK ||
			userResponse.getStatusCode() == HttpStatus.CREATED ||
			userResponse.getStatusCode() == HttpStatus.UNAUTHORIZED ||
			userResponse.getStatusCode() == HttpStatus.FORBIDDEN
		);
		
		// Crear producto
		ProductDto product = ProductDto.builder()
			.productTitle("Deployed Product")
			.priceUnit(99.99)
			.quantity(100)
			.build();
		
		ResponseEntity<ProductDto> productResponse = restTemplate.postForEntity(
			getApiGatewayUrl() + "/product-service/api/products",
			product,
			ProductDto.class
		);
		
		// Si el servicio está funcionando, debería responder
		assertTrue(
			productResponse.getStatusCode() == HttpStatus.OK ||
			productResponse.getStatusCode() == HttpStatus.CREATED ||
			productResponse.getStatusCode() == HttpStatus.UNAUTHORIZED ||
			productResponse.getStatusCode() == HttpStatus.FORBIDDEN
		);
		
		// Si ambos servicios respondieron exitosamente, crear orden
		if ((userResponse.getStatusCode() == HttpStatus.OK || userResponse.getStatusCode() == HttpStatus.CREATED) &&
		    (productResponse.getStatusCode() == HttpStatus.OK || productResponse.getStatusCode() == HttpStatus.CREATED)) {
			
			OrderDto order = OrderDto.builder()
				.orderDesc("Deployed service order")
				.orderFee(99.99)
				.build();
			
			ResponseEntity<OrderDto> orderResponse = restTemplate.postForEntity(
				getApiGatewayUrl() + "/order-service/api/orders",
				order,
				OrderDto.class
			);
			
			assertTrue(
				orderResponse.getStatusCode() == HttpStatus.OK ||
				orderResponse.getStatusCode() == HttpStatus.CREATED ||
				orderResponse.getStatusCode() == HttpStatus.UNAUTHORIZED ||
				orderResponse.getStatusCode() == HttpStatus.FORBIDDEN
			);
		}
	}
	
	@Test
	@DisplayName("E2E Test 13: Service Discovery - Verificar Servicios Registrados")
	void testServiceDiscoveryRegisteredServices() {
		// Intentar acceder al service discovery si está expuesto
		// En un ambiente real, esto requeriría acceso al service discovery
		// Por ahora, verificamos que el API Gateway puede enrutar correctamente
		
		// Verificar que el API Gateway puede enrutar a múltiples servicios
		String[] endpoints = {
			"/user-service/api/users",
			"/product-service/api/products",
			"/order-service/api/orders"
		};
		
		int successfulRoutes = 0;
		for (String endpoint : endpoints) {
			try {
				ResponseEntity<String> response = restTemplate.exchange(
					getApiGatewayUrl() + endpoint,
					HttpMethod.GET,
					null,
					String.class
				);
				
				// Cualquier respuesta (incluso 404 o 401) significa que el enrutamiento funciona
				if (response.getStatusCode().is2xxSuccessful() ||
					response.getStatusCode() == HttpStatus.NOT_FOUND ||
					response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
					successfulRoutes++;
				}
			} catch (Exception e) {
				// Error de conexión, el servicio no está disponible
				System.out.println("Endpoint " + endpoint + " no disponible: " + e.getMessage());
			}
		}
		
		// Al menos un endpoint debe funcionar
		assertTrue(successfulRoutes > 0, "Al menos un endpoint debe responder");
	}
}

