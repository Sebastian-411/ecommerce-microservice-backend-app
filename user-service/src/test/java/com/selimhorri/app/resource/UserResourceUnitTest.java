package com.selimhorri.app.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.dto.response.collection.DtoCollectionResponse;
import com.selimhorri.app.service.UserService;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserResource Unit Tests")
class UserResourceUnitTest {
	
	@Mock
	private UserService userService;
	
	@InjectMocks
	private UserResource userResource;
	
	private UserDto userDto;
	
	@BeforeEach
	void setUp() {
		userDto = UserDto.builder()
			.userId(1)
			.firstName("John")
			.lastName("Doe")
			.email("john.doe@example.com")
			.phone("1234567890")
			.build();
	}
	
	@Test
	@DisplayName("Test 1: Should return all users with 200 status")
	void testFindAll_ShouldReturnOkWithList() {
		// Given
		List<UserDto> users = Arrays.asList(userDto, userDto);
		when(userService.findAll()).thenReturn(users);
		
		// When
		ResponseEntity<DtoCollectionResponse<UserDto>> response = userResource.findAll();
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(2, response.getBody().getCollection().size());
		verify(userService).findAll();
	}
	
	@Test
	@DisplayName("Test 2: Should return user by id with 200 status")
	void testFindById_ShouldReturnOkWithUser() {
		// Given
		String userId = "1";
		when(userService.findById(anyInt())).thenReturn(userDto);
		
		// When
		ResponseEntity<UserDto> response = userResource.findById(userId);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(1, response.getBody().getUserId());
		verify(userService).findById(anyInt());
	}
	
	@Test
	@DisplayName("Test 3: Should save user and return 200 status")
	void testSave_ShouldReturnOkWithSavedUser() {
		// Given
		when(userService.save(any(UserDto.class))).thenReturn(userDto);
		
		// When
		ResponseEntity<UserDto> response = userResource.save(userDto);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		verify(userService).save(any(UserDto.class));
	}
	
	@Test
	@DisplayName("Test 4: Should update user and return 200 status")
	void testUpdate_ShouldReturnOkWithUpdatedUser() {
		// Given
		UserDto updatedDto = UserDto.builder()
			.userId(1)
			.firstName("Jane")
			.lastName("Doe")
			.build();
		when(userService.update(any(UserDto.class))).thenReturn(updatedDto);
		
		// When
		ResponseEntity<UserDto> response = userResource.update(updatedDto);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Jane", response.getBody().getFirstName());
		verify(userService).update(any(UserDto.class));
	}
}

