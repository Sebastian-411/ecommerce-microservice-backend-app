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

@DisplayName("UserMappingHelper Unit Tests")
class UserMappingHelperUnitTest {
	
	private User user;
	private UserDto userDto;
	private Credential credential;
	private CredentialDto credentialDto;
	
	@BeforeEach
	void setUp() {
		credential = Credential.builder()
			.credentialId(1)
			.username("test@example.com")
			.password("password")
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
			.imageUrl("http://example.com/image.jpg")
			.credential(credential)
			.build();
		
		credentialDto = CredentialDto.builder()
			.credentialId(1)
			.username("test@example.com")
			.password("password")
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
			.imageUrl("http://example.com/image.jpg")
			.credentialDto(credentialDto)
			.build();
	}
	
	@Test
	@DisplayName("Test 1: Should map User to UserDto correctly")
	void testMapUserToUserDto() {
		// When
		UserDto result = UserMappingHelper.map(user);
		
		// Then
		assertNotNull(result);
		assertEquals(user.getUserId(), result.getUserId());
		assertEquals(user.getFirstName(), result.getFirstName());
		assertEquals(user.getLastName(), result.getLastName());
		assertEquals(user.getEmail(), result.getEmail());
		assertEquals(user.getPhone(), result.getPhone());
		assertEquals(user.getImageUrl(), result.getImageUrl());
		assertNotNull(result.getCredentialDto());
		assertEquals(user.getCredential().getCredentialId(), result.getCredentialDto().getCredentialId());
		assertEquals(user.getCredential().getUsername(), result.getCredentialDto().getUsername());
	}
	
	@Test
	@DisplayName("Test 2: Should map UserDto to User correctly")
	void testMapUserDtoToUser() {
		// When
		User result = UserMappingHelper.map(userDto);
		
		// Then
		assertNotNull(result);
		assertEquals(userDto.getUserId(), result.getUserId());
		assertEquals(userDto.getFirstName(), result.getFirstName());
		assertEquals(userDto.getLastName(), result.getLastName());
		assertEquals(userDto.getEmail(), result.getEmail());
		assertEquals(userDto.getPhone(), result.getPhone());
		assertEquals(userDto.getImageUrl(), result.getImageUrl());
		assertNotNull(result.getCredential());
		assertEquals(userDto.getCredentialDto().getCredentialId(), result.getCredential().getCredentialId());
		assertEquals(userDto.getCredentialDto().getUsername(), result.getCredential().getUsername());
	}
}

