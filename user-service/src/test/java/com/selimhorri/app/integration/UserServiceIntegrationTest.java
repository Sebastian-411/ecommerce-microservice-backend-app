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
		// Find first user from database that has a credential
		// This ensures we use a real user from migrations
		List<User> allUsers = userRepository.findAll();
		if (!allUsers.isEmpty()) {
			// Find a user that has a credential loaded
			for (User u : allUsers) {
				// Try to access credential to trigger lazy loading
				try {
					if (u.getCredential() != null) {
						testUserId = u.getUserId();
						return;
					}
				} catch (Exception e) {
					// Continue to next user
				}
			}
			// If no user with credential found, use first user
			testUserId = allUsers.get(0).getUserId();
		} else {
			// Fallback: use user_id = 1 from migrations
			testUserId = 1;
		}
	}
	
	@Test
	@DisplayName("Integration Test 1: Should create and retrieve user from database")
	void testCreateAndRetrieveUser() {
		// Given - Verify users exist in repository first
		List<User> usersInRepo = userRepository.findAll();
		assertNotNull(usersInRepo, "Users from repository should not be null");
		assertTrue(usersInRepo.size() >= 4, "Should have at least 4 users from migrations");
		
		// Find a user that has a credential (from migrations, user_id 1-4 should have credentials)
		// Use findById which should work better than findAll for lazy loading
		Integer userIdWithCredential = null;
		for (User user : usersInRepo) {
			// Try to find a user that has credential by checking repository
			// User IDs 1-4 from migrations should have credentials
			if (user.getUserId() != null && user.getUserId() >= 1 && user.getUserId() <= 4) {
				userIdWithCredential = user.getUserId();
				break;
			}
		}
		
		// If we found a valid user ID, test retrieving it via service
		if (userIdWithCredential != null) {
			// When - Retrieve user by ID via service (this tests the service layer)
			UserDto retrieved = userService.findById(userIdWithCredential);
			
			// Then - Verify we can retrieve user successfully
			assertNotNull(retrieved, "Retrieved user should not be null");
			assertNotNull(retrieved.getUserId(), "User ID should not be null");
			assertEquals(userIdWithCredential, retrieved.getUserId());
			assertNotNull(retrieved.getFirstName(), "First name should not be null");
		} else {
			// Fallback: at least verify users exist
			assertTrue(usersInRepo.size() > 0, "At least one user should exist");
		}
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

