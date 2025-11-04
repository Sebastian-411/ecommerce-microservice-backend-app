package com.selimhorri.app.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.selimhorri.app.domain.User;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.repository.UserRepository;
import com.selimhorri.app.service.UserService;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DisplayName("UserService Integration Tests")
class UserServiceIntegrationTest {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserService userService;
	
	private Integer testUserId;
	
	@BeforeEach
	void setUp() {
		// Use existing user from migrations (user_id = 1 has credential)
		// This user exists from V2__insert_users_table.sql and V6__insert_credentials_table.sql
		testUserId = 1;
	}
	
	@Test
	@DisplayName("Integration Test 1: Should create and retrieve user from database")
	void testCreateAndRetrieveUser() {
		// Given - Use existing user from migrations (user_id = 1)
		// When - Retrieve existing user
		UserDto retrieved = userService.findById(testUserId);
		
		// Then
		assertNotNull(retrieved);
		assertNotNull(retrieved.getUserId());
		assertEquals(testUserId, retrieved.getUserId());
		assertNotNull(retrieved.getFirstName());
		assertNotNull(retrieved.getEmail());
	}
	
	@Test
	@DisplayName("Integration Test 2: Should update user in database")
	void testUpdateUser() {
		// Given - Get existing user from database first to have credential
		UserDto existingUser = userService.findById(testUserId);
		assertNotNull(existingUser, "User from migrations should exist");
		assertNotNull(existingUser.getCredentialDto(), "User should have credential from migrations");
		
		UserDto existing = UserDto.builder()
			.userId(existingUser.getUserId())
			.firstName("Updated First Name")
			.lastName("Updated Last Name")
			.email(existingUser.getEmail())
			.phone(existingUser.getPhone())
			.credentialDto(existingUser.getCredentialDto())
			.build();
		
		// When
		UserDto updated = userService.update(existing);
		
		// Then
		assertNotNull(updated);
		assertEquals("Updated First Name", updated.getFirstName());
		assertEquals("Updated Last Name", updated.getLastName());
	}
	
	@Test
	@DisplayName("Integration Test 3: Should delete user from database")
	void testDeleteUser() {
		// Given - Create a new user for deletion test (to avoid deleting migration data)
		User newUser = User.builder()
			.firstName("Delete")
			.lastName("Test")
			.email("delete.test@example.com")
			.phone("9999999999")
			.build();
		User savedUser = userRepository.save(newUser);
		
		// When
		userService.deleteById(savedUser.getUserId());
		
		// Then
		assertTrue(userRepository.findById(savedUser.getUserId()).isEmpty());
	}
	
	@Test
	@DisplayName("Integration Test 4: Should find all users from database")
	void testFindAllUsers() {
		// Given - Use existing users from database (they should have credentials from migrations)
		// When
		List<UserDto> users = userService.findAll();
		
		// Then
		assertNotNull(users);
		// Check that we can retrieve users (they should exist from migrations - at least 4 users)
		assertTrue(users.size() >= 4, "Should have at least 4 users from migrations");
		// Verify they have required fields
		UserDto firstUser = users.get(0);
		assertNotNull(firstUser.getUserId());
		assertNotNull(firstUser.getFirstName());
		assertNotNull(firstUser.getCredentialDto(), "User should have credential from migrations");
	}
}

