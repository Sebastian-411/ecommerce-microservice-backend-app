package com.selimhorri.app.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
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

import com.selimhorri.app.domain.RoleBasedAuthority;
import com.selimhorri.app.dto.CredentialDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.dto.response.collection.DtoCollectionResponse;
import com.selimhorri.app.service.CredentialService;

@ExtendWith(MockitoExtension.class)
@DisplayName("CredentialResource Unit Tests")
class CredentialResourceUnitTest {
	
	@Mock
	private CredentialService credentialService;
	
	@InjectMocks
	private CredentialResource credentialResource;
	
	private CredentialDto credentialDto;
	
	@BeforeEach
	void setUp() {
		UserDto userDto = UserDto.builder()
			.userId(1)
			.firstName("John")
			.lastName("Doe")
			.email("john.doe@example.com")
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
	}
	
	@Test
	@DisplayName("Test 1: Should return all credentials with 200 status")
	void testFindAll_ShouldReturnOkWithList() {
		// Given
		List<CredentialDto> credentials = Arrays.asList(credentialDto);
		when(credentialService.findAll()).thenReturn(credentials);
		
		// When
		ResponseEntity<DtoCollectionResponse<CredentialDto>> response = credentialResource.findAll();
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(1, response.getBody().getCollection().size());
		verify(credentialService).findAll();
	}
	
	@Test
	@DisplayName("Test 2: Should return credential by id with 200 status")
	void testFindById_ShouldReturnOkWithCredential() {
		// Given
		String credentialId = "1";
		when(credentialService.findById(anyInt())).thenReturn(credentialDto);
		
		// When
		ResponseEntity<CredentialDto> response = credentialResource.findById(credentialId);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(1, response.getBody().getCredentialId());
		verify(credentialService).findById(anyInt());
	}
	
	@Test
	@DisplayName("Test 3: Should save credential and return 200 status")
	void testSave_ShouldReturnOkWithSavedCredential() {
		// Given
		when(credentialService.save(any(CredentialDto.class))).thenReturn(credentialDto);
		
		// When
		ResponseEntity<CredentialDto> response = credentialResource.save(credentialDto);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		verify(credentialService).save(any(CredentialDto.class));
	}
	
	@Test
	@DisplayName("Test 4: Should update credential and return 200 status")
	void testUpdate_ShouldReturnOkWithUpdatedCredential() {
		// Given
		CredentialDto updatedDto = CredentialDto.builder()
			.credentialId(1)
			.username("updateduser")
			.password("newpassword")
			.roleBasedAuthority(RoleBasedAuthority.ROLE_ADMIN)
			.isEnabled(false)
			.build();
		when(credentialService.update(any(CredentialDto.class))).thenReturn(updatedDto);
		
		// When
		ResponseEntity<CredentialDto> response = credentialResource.update(updatedDto);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("updateduser", response.getBody().getUsername());
		verify(credentialService).update(any(CredentialDto.class));
	}
	
	@Test
	@DisplayName("Test 5: Should update credential with id and return 200 status")
	void testUpdateWithId_ShouldReturnOkWithUpdatedCredential() {
		// Given
		String credentialId = "1";
		CredentialDto updatedDto = CredentialDto.builder()
			.credentialId(1)
			.username("updateduserwithid")
			.password("newpasswordid")
			.roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
			.isEnabled(true)
			.build();
		when(credentialService.update(anyInt(), any(CredentialDto.class))).thenReturn(updatedDto);
		
		// When
		ResponseEntity<CredentialDto> response = credentialResource.update(credentialId, updatedDto);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("updateduserwithid", response.getBody().getUsername());
		verify(credentialService).update(anyInt(), any(CredentialDto.class));
	}
	
	@Test
	@DisplayName("Test 6: Should delete credential and return true")
	void testDeleteById_ShouldReturnOkWithTrue() {
		// Given
		String credentialId = "1";
		
		// When
		ResponseEntity<Boolean> response = credentialResource.deleteById(credentialId);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(true, response.getBody());
		verify(credentialService).deleteById(anyInt());
	}
	
	@Test
	@DisplayName("Test 7: Should return credential by username with 200 status")
	void testFindByUsername_ShouldReturnOkWithCredential() {
		// Given
		String username = "testuser";
		when(credentialService.findByUsername(anyString())).thenReturn(credentialDto);
		
		// When
		ResponseEntity<CredentialDto> response = credentialResource.findByUsername(username);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("testuser", response.getBody().getUsername());
		verify(credentialService).findByUsername(anyString());
	}
}

