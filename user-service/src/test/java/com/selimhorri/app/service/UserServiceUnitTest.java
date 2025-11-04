package com.selimhorri.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.selimhorri.app.domain.Credential;
import com.selimhorri.app.domain.RoleBasedAuthority;
import com.selimhorri.app.domain.User;
import com.selimhorri.app.dto.CredentialDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.exception.wrapper.UserObjectNotFoundException;
import com.selimhorri.app.repository.UserRepository;
import com.selimhorri.app.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceUnitTest {
	
	@Mock
	private UserRepository userRepository;
	
	@InjectMocks
	private UserServiceImpl userService;
	
	private UserDto userDto;
	private User user;
	private Credential credential;
	
	@BeforeEach
	void setUp() {
		credential = Credential.builder()
			.credentialId(1)
			.username("johndoe")
			.password("password123")
			.roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
			.isEnabled(true)
			.isAccountNonExpired(true)
			.isAccountNonLocked(true)
			.isCredentialsNonExpired(true)
			.build();
		
		user = User.builder()
			.userId(1)
			.firstName("John")
			.lastName("Doe")
			.email("john.doe@example.com")
			.phone("1234567890")
			.credential(credential)
			.build();
		
		credential.setUser(user);
		
		CredentialDto credentialDto = CredentialDto.builder()
			.credentialId(1)
			.username("johndoe")
			.password("password123")
			.roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
			.isEnabled(true)
			.isAccountNonExpired(true)
			.isAccountNonLocked(true)
			.isCredentialsNonExpired(true)
			.build();
		
		userDto = UserDto.builder()
			.userId(1)
			.firstName("John")
			.lastName("Doe")
			.email("john.doe@example.com")
			.phone("1234567890")
			.credentialDto(credentialDto)
			.build();
	}
	
	@Test
	@DisplayName("Test 1: Should find all users successfully")
	void testFindAll_ShouldReturnListOfUsers() {
		// Given - Create two different users with different IDs
		Credential credential2 = Credential.builder()
			.credentialId(2)
			.username("janedoe")
			.password("password456")
			.roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
			.isEnabled(true)
			.isAccountNonExpired(true)
			.isAccountNonLocked(true)
			.isCredentialsNonExpired(true)
			.build();
		
		User user2 = User.builder()
			.userId(2)
			.firstName("Jane")
			.lastName("Doe")
			.email("jane.doe@example.com")
			.phone("0987654321")
			.credential(credential2)
			.build();
		
		credential2.setUser(user2);
		
		List<User> users = Arrays.asList(user, user2);
		when(userRepository.findAll()).thenReturn(users);
		
		// When
		List<UserDto> result = userService.findAll();
		
		// Then
		assertNotNull(result);
		assertEquals(2, result.size());
		verify(userRepository).findAll();
	}
	
	@Test
	@DisplayName("Test 2: Should find user by id successfully")
	void testFindById_ShouldReturnUserDto() {
		// Given
		Integer userId = 1;
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		
		// When
		UserDto result = userService.findById(userId);
		
		// Then
		assertNotNull(result);
		assertEquals(userId, result.getUserId());
		assertEquals("John", result.getFirstName());
		verify(userRepository).findById(userId);
	}
	
	@Test
	@DisplayName("Test 3: Should throw exception when user not found by id")
	void testFindById_WhenUserNotFound_ShouldThrowException() {
		// Given
		Integer userId = 999;
		when(userRepository.findById(userId)).thenReturn(Optional.empty());
		
		// When & Then
		assertThrows(UserObjectNotFoundException.class, () -> {
			userService.findById(userId);
		});
		verify(userRepository).findById(userId);
	}
	
	@Test
	@DisplayName("Test 4: Should save user successfully")
	void testSave_ShouldReturnSavedUserDto() {
		// Given
		when(userRepository.save(any(User.class))).thenReturn(user);
		
		// When
		UserDto result = userService.save(userDto);
		
		// Then
		assertNotNull(result);
		assertEquals(userDto.getFirstName(), result.getFirstName());
		verify(userRepository).save(any(User.class));
	}
	
	@Test
	@DisplayName("Test 5: Should update user successfully")
	void testUpdate_ShouldReturnUpdatedUserDto() {
		// Given
		CredentialDto updatedCredentialDto = CredentialDto.builder()
			.credentialId(1)
			.username("janedoe")
			.password("password123")
			.roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
			.isEnabled(true)
			.isAccountNonExpired(true)
			.isAccountNonLocked(true)
			.isCredentialsNonExpired(true)
			.build();
		
		UserDto updatedDto = UserDto.builder()
			.userId(1)
			.firstName("Jane")
			.lastName("Doe")
			.email("jane.doe@example.com")
			.credentialDto(updatedCredentialDto)
			.build();
		
		Credential updatedCredential = Credential.builder()
			.credentialId(1)
			.username("janedoe")
			.password("password123")
			.roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
			.isEnabled(true)
			.isAccountNonExpired(true)
			.isAccountNonLocked(true)
			.isCredentialsNonExpired(true)
			.build();
		
		User updatedUser = User.builder()
			.userId(1)
			.firstName("Jane")
			.lastName("Doe")
			.email("jane.doe@example.com")
			.credential(updatedCredential)
			.build();
		
		updatedCredential.setUser(updatedUser);
		
		when(userRepository.save(any(User.class))).thenReturn(updatedUser);
		
		// When
		UserDto result = userService.update(updatedDto);
		
		// Then
		assertNotNull(result);
		assertEquals("Jane", result.getFirstName());
		verify(userRepository).save(any(User.class));
	}
	
	@Test
	@DisplayName("Test 6: Should delete user by id successfully")
	void testDeleteById_ShouldDeleteUser() {
		// Given
		Integer userId = 1;
		
		// When
		userService.deleteById(userId);
		
		// Then
		verify(userRepository).deleteById(userId);
	}
	
	@Test
	@DisplayName("Test 7: Should find user by username successfully")
	void testFindByUsername_ShouldReturnUserDto() {
		// Given
		String username = "johndoe";
		when(userRepository.findByCredentialUsername(username)).thenReturn(Optional.of(user));
		
		// When
		UserDto result = userService.findByUsername(username);
		
		// Then
		assertNotNull(result);
		assertEquals("John", result.getFirstName());
		verify(userRepository).findByCredentialUsername(username);
	}
	
	@Test
	@DisplayName("Test 8: Should throw exception when user not found by username")
	void testFindByUsername_WhenUserNotFound_ShouldThrowException() {
		// Given
		String username = "nonexistent";
		when(userRepository.findByCredentialUsername(username)).thenReturn(Optional.empty());
		
		// When & Then
		assertThrows(UserObjectNotFoundException.class, () -> {
			userService.findByUsername(username);
		});
		verify(userRepository).findByCredentialUsername(username);
	}
	
	@Test
	@DisplayName("Test 9: Should update user with ID successfully")
	void testUpdateWithId_ShouldReturnUpdatedUserDto() {
		// Given
		Integer userId = 1;
		CredentialDto updatedCredentialDto = CredentialDto.builder()
			.credentialId(1)
			.username("janeupdated")
			.password("password123")
			.roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
			.isEnabled(true)
			.isAccountNonExpired(true)
			.isAccountNonLocked(true)
			.isCredentialsNonExpired(true)
			.build();
		
		UserDto updatedDto = UserDto.builder()
			.userId(1)
			.firstName("Jane Updated")
			.lastName("Doe Updated")
			.email("jane.updated@example.com")
			.credentialDto(updatedCredentialDto)
			.build();
		
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(userRepository.save(any(User.class))).thenReturn(user);
		
		// When
		UserDto result = userService.update(userId, updatedDto);
		
		// Then
		assertNotNull(result);
		verify(userRepository).findById(userId);
		verify(userRepository).save(any(User.class));
	}
}

