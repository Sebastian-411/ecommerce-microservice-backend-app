package com.selimhorri.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
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
import com.selimhorri.app.domain.VerificationToken;
import com.selimhorri.app.dto.CredentialDto;
import com.selimhorri.app.dto.VerificationTokenDto;
import com.selimhorri.app.exception.wrapper.VerificationTokenNotFoundException;
import com.selimhorri.app.repository.VerificationTokenRepository;
import com.selimhorri.app.service.impl.VerificationTokenServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("VerificationTokenService Unit Tests")
class VerificationTokenServiceUnitTest {
	
	@Mock
	private VerificationTokenRepository verificationTokenRepository;
	
	@InjectMocks
	private VerificationTokenServiceImpl verificationTokenService;
	
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
	@DisplayName("Test 1: Should find all verification tokens successfully")
	void testFindAll_ShouldReturnListOfVerificationTokens() {
		// Given - Create two different verification tokens with different IDs
		Credential credential2 = Credential.builder().credentialId(2).username("user2").roleBasedAuthority(RoleBasedAuthority.ROLE_USER).build();
		VerificationToken verificationToken2 = VerificationToken.builder()
			.verificationTokenId(2)
			.token("anothertoken")
			.expireDate(LocalDate.now().plusDays(2))
			.credential(credential2)
			.build();
		
		List<VerificationToken> verificationTokens = Arrays.asList(verificationToken, verificationToken2);
		when(verificationTokenRepository.findAll()).thenReturn(verificationTokens);
		
		// When
		List<VerificationTokenDto> result = verificationTokenService.findAll();
		
		// Then
		assertNotNull(result);
		assertEquals(2, result.size());
		verify(verificationTokenRepository).findAll();
	}
	
	@Test
	@DisplayName("Test 2: Should find verification token by id successfully")
	void testFindById_ShouldReturnVerificationTokenDto() {
		// Given
		Integer verificationTokenId = 1;
		when(verificationTokenRepository.findById(verificationTokenId)).thenReturn(Optional.of(verificationToken));
		
		// When
		VerificationTokenDto result = verificationTokenService.findById(verificationTokenId);
		
		// Then
		assertNotNull(result);
		assertEquals(verificationTokenId, result.getVerificationTokenId());
		assertEquals("testtoken", result.getToken());
		verify(verificationTokenRepository).findById(verificationTokenId);
	}
	
	@Test
	@DisplayName("Test 3: Should throw exception when verification token not found")
	void testFindById_WhenVerificationTokenNotFound_ShouldThrowException() {
		// Given
		Integer verificationTokenId = 999;
		when(verificationTokenRepository.findById(verificationTokenId)).thenReturn(Optional.empty());
		
		// When & Then
		assertThrows(VerificationTokenNotFoundException.class, () -> {
			verificationTokenService.findById(verificationTokenId);
		});
		verify(verificationTokenRepository).findById(verificationTokenId);
	}
	
	@Test
	@DisplayName("Test 4: Should save verification token successfully")
	void testSave_ShouldReturnSavedVerificationTokenDto() {
		// Given
		when(verificationTokenRepository.save(any(VerificationToken.class))).thenReturn(verificationToken);
		
		// When
		VerificationTokenDto result = verificationTokenService.save(verificationTokenDto);
		
		// Then
		assertNotNull(result);
		assertEquals(verificationTokenDto.getToken(), result.getToken());
		verify(verificationTokenRepository).save(any(VerificationToken.class));
	}
	
	@Test
	@DisplayName("Test 5: Should update verification token successfully")
	void testUpdate_ShouldReturnUpdatedVerificationTokenDto() {
		// Given
		VerificationTokenDto updatedDto = VerificationTokenDto.builder()
			.verificationTokenId(1)
			.token("updatedtoken")
			.expireDate(LocalDate.now().plusDays(2))
			.credentialDto(credentialDto)
			.build();
		
		VerificationToken updatedVerificationToken = VerificationToken.builder()
			.verificationTokenId(1)
			.token("updatedtoken")
			.expireDate(LocalDate.now().plusDays(2))
			.credential(credential)
			.build();
		
		when(verificationTokenRepository.save(any(VerificationToken.class))).thenReturn(updatedVerificationToken);
		
		// When
		VerificationTokenDto result = verificationTokenService.update(updatedDto);
		
		// Then
		assertNotNull(result);
		assertEquals("updatedtoken", result.getToken());
		verify(verificationTokenRepository).save(any(VerificationToken.class));
	}
	
	@Test
	@DisplayName("Test 6: Should update verification token with ID successfully")
	void testUpdateWithId_ShouldReturnUpdatedVerificationTokenDto() {
		// Given
		Integer verificationTokenId = 1;
		VerificationTokenDto updatedDto = VerificationTokenDto.builder()
			.verificationTokenId(1)
			.token("updatedtokenwithid")
			.expireDate(LocalDate.now().plusDays(3))
			.credentialDto(credentialDto)
			.build();
		
		when(verificationTokenRepository.findById(verificationTokenId)).thenReturn(Optional.of(verificationToken));
		when(verificationTokenRepository.save(any(VerificationToken.class))).thenReturn(verificationToken);
		
		// When
		VerificationTokenDto result = verificationTokenService.update(verificationTokenId, updatedDto);
		
		// Then
		assertNotNull(result);
		verify(verificationTokenRepository).findById(verificationTokenId);
		verify(verificationTokenRepository).save(any(VerificationToken.class));
	}
	
	@Test
	@DisplayName("Test 7: Should delete verification token by ID successfully")
	void testDeleteById_ShouldDeleteVerificationToken() {
		// Given
		Integer verificationTokenId = 1;
		
		// When
		verificationTokenService.deleteById(verificationTokenId);
		
		// Then
		verify(verificationTokenRepository).deleteById(verificationTokenId);
	}
}

