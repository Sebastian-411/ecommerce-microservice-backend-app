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
	
	private User testUser;
	
	@BeforeEach
	void setUp() {
		testUser = User.builder()
			.firstName("John")
			.lastName("Doe")
			.email("john.doe@example.com")
			.phone("1234567890")
			.build();
		testUser = userRepository.save(testUser);
	}
	
	@Test
	@DisplayName("Integration Test 1: Should create and retrieve user from database")
	void testCreateAndRetrieveUser() {
		// Given
		UserDto userDto = UserDto.builder()
			.firstName("Jane")
			.lastName("Smith")
			.email("jane.smith@example.com")
			.phone("0987654321")
			.build();
		
		// When
		UserDto saved = userService.save(userDto);
		UserDto retrieved = userService.findById(saved.getUserId());
		
		// Then
		assertNotNull(retrieved);
		assertEquals("Jane", retrieved.getFirstName());
		assertEquals("jane.smith@example.com", retrieved.getEmail());
	}
	
	@Test
	@DisplayName("Integration Test 2: Should update user in database")
	void testUpdateUser() {
		// Given
		UserDto existing = UserDto.builder()
			.userId(testUser.getUserId())
			.firstName("John Updated")
			.lastName("Doe Updated")
			.email("john.updated@example.com")
			.phone("1111111111")
			.build();
		
		// When
		UserDto updated = userService.update(existing);
		
		// Then
		assertNotNull(updated);
		assertEquals("John Updated", updated.getFirstName());
		assertEquals("john.updated@example.com", updated.getEmail());
	}
	
	@Test
	@DisplayName("Integration Test 3: Should delete user from database")
	void testDeleteUser() {
		// Given
		Integer userId = testUser.getUserId();
		
		// When
		userService.deleteById(userId);
		
		// Then
		assertTrue(userRepository.findById(userId).isEmpty());
	}
	
	@Test
	@DisplayName("Integration Test 4: Should find all users from database")
	void testFindAllUsers() {
		// Given
		User anotherUser = User.builder()
			.firstName("Bob")
			.lastName("Johnson")
			.email("bob.johnson@example.com")
			.build();
		userRepository.save(anotherUser);
		
		// When
		List<UserDto> users = userService.findAll();
		
		// Then
		assertNotNull(users);
		assertTrue(users.size() >= 2);
	}
}

