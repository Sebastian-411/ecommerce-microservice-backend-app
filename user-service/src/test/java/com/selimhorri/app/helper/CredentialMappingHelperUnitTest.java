package com.selimhorri.app.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.selimhorri.app.domain.Credential;
import com.selimhorri.app.domain.RoleBasedAuthority;
import com.selimhorri.app.domain.User;
import com.selimhorri.app.dto.CredentialDto;
import com.selimhorri.app.dto.UserDto;

@DisplayName("CredentialMappingHelper Unit Tests")
class CredentialMappingHelperUnitTest {
	
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
			.imageUrl("http://example.com/image.jpg")
			.build();
		
		userDto = UserDto.builder()
			.userId(1)
			.firstName("John")
			.lastName("Doe")
			.email("john.doe@example.com")
			.phone("1234567890")
			.imageUrl("http://example.com/image.jpg")
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
	}
	
	@Test
	@DisplayName("Test 1: Should map Credential to CredentialDto successfully")
	void testMap_FromCredential_ShouldReturnCredentialDto() {
		// When
		CredentialDto result = CredentialMappingHelper.map(credential);
		
		// Then
		assertNotNull(result);
		assertEquals(credential.getCredentialId(), result.getCredentialId());
		assertEquals(credential.getUsername(), result.getUsername());
		assertEquals(credential.getPassword(), result.getPassword());
		assertEquals(credential.getRoleBasedAuthority(), result.getRoleBasedAuthority());
		assertEquals(credential.getIsEnabled(), result.getIsEnabled());
		assertEquals(credential.getIsAccountNonExpired(), result.getIsAccountNonExpired());
		assertEquals(credential.getIsAccountNonLocked(), result.getIsAccountNonLocked());
		assertEquals(credential.getIsCredentialsNonExpired(), result.getIsCredentialsNonExpired());
		assertNotNull(result.getUserDto());
		assertEquals(credential.getUser().getUserId(), result.getUserDto().getUserId());
		assertEquals(credential.getUser().getFirstName(), result.getUserDto().getFirstName());
	}
	
	@Test
	@DisplayName("Test 2: Should map CredentialDto to Credential successfully")
	void testMap_FromCredentialDto_ShouldReturnCredential() {
		// When
		Credential result = CredentialMappingHelper.map(credentialDto);
		
		// Then
		assertNotNull(result);
		assertEquals(credentialDto.getCredentialId(), result.getCredentialId());
		assertEquals(credentialDto.getUsername(), result.getUsername());
		assertEquals(credentialDto.getPassword(), result.getPassword());
		assertEquals(credentialDto.getRoleBasedAuthority(), result.getRoleBasedAuthority());
		assertEquals(credentialDto.getIsEnabled(), result.getIsEnabled());
		assertEquals(credentialDto.getIsAccountNonExpired(), result.getIsAccountNonExpired());
		assertEquals(credentialDto.getIsAccountNonLocked(), result.getIsAccountNonLocked());
		assertEquals(credentialDto.getIsCredentialsNonExpired(), result.getIsCredentialsNonExpired());
		assertNotNull(result.getUser());
		assertEquals(credentialDto.getUserDto().getUserId(), result.getUser().getUserId());
		assertEquals(credentialDto.getUserDto().getFirstName(), result.getUser().getFirstName());
	}
}

