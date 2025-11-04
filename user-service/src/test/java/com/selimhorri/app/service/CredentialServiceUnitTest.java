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
import com.selimhorri.app.exception.wrapper.CredentialNotFoundException;
import com.selimhorri.app.exception.wrapper.UserObjectNotFoundException;
import com.selimhorri.app.repository.CredentialRepository;
import com.selimhorri.app.service.impl.CredentialServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("CredentialService Unit Tests")
class CredentialServiceUnitTest {
	
	@Mock
	private CredentialRepository credentialRepository;
	
	@InjectMocks
	private CredentialServiceImpl credentialService;
	
	private CredentialDto credentialDto;
	private Credential credential;
	private User user;
	private UserDto userDto;
	
	@BeforeEach
	void setUp() {
		user = User.builder()
			.userId(1)
			.firstName("John")
			.lastName("Doe")
			.email("john.doe@example.com")
			.phone("1234567890")
			.build();
		
		userDto = UserDto.builder()
			.userId(1)
			.firstName("John")
			.lastName("Doe")
			.email("john.doe@example.com")
			.phone("1234567890")
			.build();
		
		credentialDto = CredentialDto.builder()
			.credentialId(1)
			.username("testuser")
			.password("password")
			.roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
			.isEnabled(true)
			.isAccountNonExpired(true)
			.isAccountNonLocked(true)
			.isCredentialsNonExpired(true)
			.userDto(userDto)
			.build();
		
		credential = Credential.builder()
			.credentialId(1)
			.username("testuser")
			.password("password")
			.roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
			.isEnabled(true)
			.isAccountNonExpired(true)
			.isAccountNonLocked(true)
			.isCredentialsNonExpired(true)
			.user(user)
			.build();
		
		user.setCredential(credential);
	}
	
	@Test
	@DisplayName("Test 1: Should find all credentials successfully")
	void testFindAll_ShouldReturnListOfCredentials() {
		// Given - Create two different credentials with different IDs
		User user2 = User.builder().userId(2).firstName("Jane").build();
		Credential credential2 = Credential.builder()
			.credentialId(2)
			.username("testuser2")
			.password("password2")
			.roleBasedAuthority(RoleBasedAuthority.ROLE_ADMIN)
			.isEnabled(true)
			.isAccountNonExpired(true)
			.isAccountNonLocked(true)
			.isCredentialsNonExpired(true)
			.user(user2)
			.build();
		user2.setCredential(credential2);
		
		List<Credential> credentials = Arrays.asList(credential, credential2);
		when(credentialRepository.findAll()).thenReturn(credentials);
		
		// When
		List<CredentialDto> result = credentialService.findAll();
		
		// Then
		assertNotNull(result);
		assertEquals(2, result.size());
		verify(credentialRepository).findAll();
	}
	
	@Test
	@DisplayName("Test 2: Should find credential by id successfully")
	void testFindById_ShouldReturnCredentialDto() {
		// Given
		Integer credentialId = 1;
		when(credentialRepository.findById(credentialId)).thenReturn(Optional.of(credential));
		
		// When
		CredentialDto result = credentialService.findById(credentialId);
		
		// Then
		assertNotNull(result);
		assertEquals(credentialId, result.getCredentialId());
		assertEquals("testuser", result.getUsername());
		verify(credentialRepository).findById(credentialId);
	}
	
	@Test
	@DisplayName("Test 3: Should throw exception when credential not found by id")
	void testFindById_WhenCredentialNotFound_ShouldThrowException() {
		// Given
		Integer credentialId = 999;
		when(credentialRepository.findById(credentialId)).thenReturn(Optional.empty());
		
		// When & Then
		assertThrows(CredentialNotFoundException.class, () -> {
			credentialService.findById(credentialId);
		});
		verify(credentialRepository).findById(credentialId);
	}
	
	@Test
	@DisplayName("Test 4: Should find credential by username successfully")
	void testFindByUsername_ShouldReturnCredentialDto() {
		// Given
		String username = "testuser";
		when(credentialRepository.findByUsername(username)).thenReturn(Optional.of(credential));
		
		// When
		CredentialDto result = credentialService.findByUsername(username);
		
		// Then
		assertNotNull(result);
		assertEquals(username, result.getUsername());
		verify(credentialRepository).findByUsername(username);
	}
	
	@Test
	@DisplayName("Test 5: Should throw exception when credential not found by username")
	void testFindByUsername_WhenCredentialNotFound_ShouldThrowException() {
		// Given
		String username = "nonexistent";
		when(credentialRepository.findByUsername(username)).thenReturn(Optional.empty());
		
		// When & Then
		assertThrows(UserObjectNotFoundException.class, () -> {
			credentialService.findByUsername(username);
		});
		verify(credentialRepository).findByUsername(username);
	}
	
	@Test
	@DisplayName("Test 6: Should save credential successfully")
	void testSave_ShouldReturnSavedCredentialDto() {
		// Given
		when(credentialRepository.save(any(Credential.class))).thenReturn(credential);
		
		// When
		CredentialDto result = credentialService.save(credentialDto);
		
		// Then
		assertNotNull(result);
		assertEquals(credentialDto.getUsername(), result.getUsername());
		verify(credentialRepository).save(any(Credential.class));
	}
	
	@Test
	@DisplayName("Test 7: Should update credential successfully")
	void testUpdate_ShouldReturnUpdatedCredentialDto() {
		// Given
		CredentialDto updatedDto = CredentialDto.builder()
			.credentialId(1)
			.username("updateduser")
			.password("newpassword")
			.roleBasedAuthority(RoleBasedAuthority.ROLE_ADMIN)
			.isEnabled(false)
			.isAccountNonExpired(false)
			.isAccountNonLocked(false)
			.isCredentialsNonExpired(false)
			.userDto(userDto)
			.build();
		
		Credential updatedCredential = Credential.builder()
			.credentialId(1)
			.username("updateduser")
			.password("newpassword")
			.roleBasedAuthority(RoleBasedAuthority.ROLE_ADMIN)
			.isEnabled(false)
			.isAccountNonExpired(false)
			.isAccountNonLocked(false)
			.isCredentialsNonExpired(false)
			.user(user)
			.build();
		
		when(credentialRepository.save(any(Credential.class))).thenReturn(updatedCredential);
		
		// When
		CredentialDto result = credentialService.update(updatedDto);
		
		// Then
		assertNotNull(result);
		assertEquals("updateduser", result.getUsername());
		verify(credentialRepository).save(any(Credential.class));
	}
	
	@Test
	@DisplayName("Test 8: Should update credential with ID successfully")
	void testUpdateWithId_ShouldReturnUpdatedCredentialDto() {
		// Given
		Integer credentialId = 1;
		CredentialDto updatedDto = CredentialDto.builder()
			.credentialId(1)
			.username("updateduserwithid")
			.password("newpasswordid")
			.roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
			.isEnabled(true)
			.isAccountNonExpired(true)
			.isAccountNonLocked(true)
			.isCredentialsNonExpired(true)
			.userDto(userDto)
			.build();
		
		when(credentialRepository.findById(credentialId)).thenReturn(Optional.of(credential));
		when(credentialRepository.save(any(Credential.class))).thenReturn(credential);
		
		// When
		CredentialDto result = credentialService.update(credentialId, updatedDto);
		
		// Then
		assertNotNull(result);
		verify(credentialRepository).findById(credentialId);
		verify(credentialRepository).save(any(Credential.class));
	}
	
	@Test
	@DisplayName("Test 9: Should delete credential by ID successfully")
	void testDeleteById_ShouldDeleteCredential() {
		// Given
		Integer credentialId = 1;
		
		// When
		credentialService.deleteById(credentialId);
		
		// Then
		verify(credentialRepository).deleteById(credentialId);
	}
}

