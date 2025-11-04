package com.selimhorri.app.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.selimhorri.app.domain.Credential;
import com.selimhorri.app.domain.RoleBasedAuthority;
import com.selimhorri.app.domain.VerificationToken;
import com.selimhorri.app.dto.CredentialDto;
import com.selimhorri.app.dto.VerificationTokenDto;

@DisplayName("VerificationTokenMappingHelper Unit Tests")
class VerificationTokenMappingHelperUnitTest {
	
	private VerificationTokenDto verificationTokenDto;
	private VerificationToken verificationToken;
	private Credential credential;
	private CredentialDto credentialDto;
	
	@BeforeEach
	void setUp() {
		credentialDto = CredentialDto.builder()
			.credentialId(1)
			.username("testuser")
			.password("password")
			.roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
			.isEnabled(true)
			.isAccountNonExpired(true)
			.isAccountNonLocked(true)
			.isCredentialsNonExpired(true)
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
			.build();
		
		verificationTokenDto = VerificationTokenDto.builder()
			.verificationTokenId(1)
			.token("testtoken")
			.expireDate(LocalDate.now().plusDays(1))
			.credentialDto(credentialDto)
			.build();
		
		verificationToken = VerificationToken.builder()
			.verificationTokenId(1)
			.token("testtoken")
			.expireDate(LocalDate.now().plusDays(1))
			.credential(credential)
			.build();
	}
	
	@Test
	@DisplayName("Test 1: Should map VerificationToken to VerificationTokenDto successfully")
	void testMap_FromVerificationToken_ShouldReturnVerificationTokenDto() {
		// When
		VerificationTokenDto result = VerificationTokenMappingHelper.map(verificationToken);
		
		// Then
		assertNotNull(result);
		assertEquals(verificationToken.getVerificationTokenId(), result.getVerificationTokenId());
		assertEquals(verificationToken.getToken(), result.getToken());
		assertEquals(verificationToken.getExpireDate(), result.getExpireDate());
		assertNotNull(result.getCredentialDto());
		assertEquals(verificationToken.getCredential().getCredentialId(), result.getCredentialDto().getCredentialId());
		assertEquals(verificationToken.getCredential().getUsername(), result.getCredentialDto().getUsername());
		assertEquals(verificationToken.getCredential().getRoleBasedAuthority(), result.getCredentialDto().getRoleBasedAuthority());
	}
	
	@Test
	@DisplayName("Test 2: Should map VerificationTokenDto to VerificationToken successfully")
	void testMap_FromVerificationTokenDto_ShouldReturnVerificationToken() {
		// When
		VerificationToken result = VerificationTokenMappingHelper.map(verificationTokenDto);
		
		// Then
		assertNotNull(result);
		assertEquals(verificationTokenDto.getVerificationTokenId(), result.getVerificationTokenId());
		assertEquals(verificationTokenDto.getToken(), result.getToken());
		assertEquals(verificationTokenDto.getExpireDate(), result.getExpireDate());
		assertNotNull(result.getCredential());
		assertEquals(verificationTokenDto.getCredentialDto().getCredentialId(), result.getCredential().getCredentialId());
		assertEquals(verificationTokenDto.getCredentialDto().getUsername(), result.getCredential().getUsername());
		assertEquals(verificationTokenDto.getCredentialDto().getRoleBasedAuthority(), result.getCredential().getRoleBasedAuthority());
	}
}

