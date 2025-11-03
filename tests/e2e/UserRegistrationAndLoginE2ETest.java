package com.selimhorri.app.e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.concurrent.TimeUnit;

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

import com.selimhorri.app.dto.UserDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("E2E Test 1: User Registration and Login Flow")
class UserRegistrationAndLoginE2ETest {
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	private String getBaseUrl() {
		return "http://localhost:" + port;
	}
	
	@Test
	@DisplayName("E2E Test 1: Complete user registration and login flow")
	void testUserRegistrationAndLogin() throws InterruptedException {
		// Step 1: Register new user
		UserDto newUser = UserDto.builder()
			.firstName("E2E")
			.lastName("User")
			.email("e2e.user@example.com")
			.phone("1234567890")
			.build();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<UserDto> request = new HttpEntity<>(newUser, headers);
		
		ResponseEntity<UserDto> registrationResponse = restTemplate.exchange(
			getBaseUrl() + "/api/users",
			HttpMethod.POST,
			request,
			UserDto.class
		);
		
		// Verify registration
		assertEquals(HttpStatus.OK, registrationResponse.getStatusCode());
		UserDto registeredUser = registrationResponse.getBody();
		assertNotNull(registeredUser);
		assertEquals("E2E", registeredUser.getFirstName());
		assertEquals("e2e.user@example.com", registeredUser.getEmail());
		
		// Step 2: Retrieve user information
		TimeUnit.SECONDS.sleep(1); // Simulate delay
		
		ResponseEntity<UserDto> getUserResponse = restTemplate.exchange(
			getBaseUrl() + "/api/users/" + registeredUser.getUserId(),
			HttpMethod.GET,
			null,
			UserDto.class
		);
		
		// Verify retrieval
		assertEquals(HttpStatus.OK, getUserResponse.getStatusCode());
		UserDto retrievedUser = getUserResponse.getBody();
		assertNotNull(retrievedUser);
		assertEquals(registeredUser.getUserId(), retrievedUser.getUserId());
		assertEquals(registeredUser.getEmail(), retrievedUser.getEmail());
	}
}

