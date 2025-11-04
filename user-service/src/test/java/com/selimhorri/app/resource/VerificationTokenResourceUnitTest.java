package com.selimhorri.app.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
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
import com.selimhorri.app.dto.VerificationTokenDto;
import com.selimhorri.app.dto.response.collection.DtoCollectionResponse;
import com.selimhorri.app.service.VerificationTokenService;

@ExtendWith(MockitoExtension.class)
@DisplayName("VerificationTokenResource Unit Tests")
class VerificationTokenResourceUnitTest {
	
	@Mock
	private VerificationTokenService verificationTokenService;
	
	@InjectMocks
	private VerificationTokenResource verificationTokenResource;
	
	private VerificationTokenDto verificationTokenDto;
	
	@BeforeEach
	void setUp() {
		CredentialDto credentialDto = CredentialDto.builder()
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
	}
	
	@Test
	@DisplayName("Test 1: Should return all verification tokens with 200 status")
	void testFindAll_ShouldReturnOkWithList() {
		// Given
		List<VerificationTokenDto> verificationTokens = Arrays.asList(verificationTokenDto);
		when(verificationTokenService.findAll()).thenReturn(verificationTokens);
		
		// When
		ResponseEntity<DtoCollectionResponse<VerificationTokenDto>> response = verificationTokenResource.findAll();
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(1, response.getBody().getCollection().size());
		verify(verificationTokenService).findAll();
	}
	
	@Test
	@DisplayName("Test 2: Should return verification token by id with 200 status")
	void testFindById_ShouldReturnOkWithVerificationToken() {
		// Given
		String verificationTokenId = "1";
		when(verificationTokenService.findById(anyInt())).thenReturn(verificationTokenDto);
		
		// When
		ResponseEntity<VerificationTokenDto> response = verificationTokenResource.findById(verificationTokenId);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(1, response.getBody().getVerificationTokenId());
		verify(verificationTokenService).findById(anyInt());
	}
	
	@Test
	@DisplayName("Test 3: Should save verification token and return 200 status")
	void testSave_ShouldReturnOkWithSavedVerificationToken() {
		// Given
		when(verificationTokenService.save(any(VerificationTokenDto.class))).thenReturn(verificationTokenDto);
		
		// When
		ResponseEntity<VerificationTokenDto> response = verificationTokenResource.save(verificationTokenDto);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		verify(verificationTokenService).save(any(VerificationTokenDto.class));
	}
	
	@Test
	@DisplayName("Test 4: Should update verification token and return 200 status")
	void testUpdate_ShouldReturnOkWithUpdatedVerificationToken() {
		// Given
		VerificationTokenDto updatedDto = VerificationTokenDto.builder()
			.verificationTokenId(1)
			.token("updatedtoken")
			.expireDate(LocalDate.now().plusDays(2))
			.build();
		when(verificationTokenService.update(any(VerificationTokenDto.class))).thenReturn(updatedDto);
		
		// When
		ResponseEntity<VerificationTokenDto> response = verificationTokenResource.update(updatedDto);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("updatedtoken", response.getBody().getToken());
		verify(verificationTokenService).update(any(VerificationTokenDto.class));
	}
	
	@Test
	@DisplayName("Test 5: Should update verification token with id and return 200 status")
	void testUpdateWithId_ShouldReturnOkWithUpdatedVerificationToken() {
		// Given
		String verificationTokenId = "1";
		VerificationTokenDto updatedDto = VerificationTokenDto.builder()
			.verificationTokenId(1)
			.token("updatedtokenwithid")
			.expireDate(LocalDate.now().plusDays(3))
			.build();
		when(verificationTokenService.update(anyInt(), any(VerificationTokenDto.class))).thenReturn(updatedDto);
		
		// When
		ResponseEntity<VerificationTokenDto> response = verificationTokenResource.update(verificationTokenId, updatedDto);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("updatedtokenwithid", response.getBody().getToken());
		verify(verificationTokenService).update(anyInt(), any(VerificationTokenDto.class));
	}
	
	@Test
	@DisplayName("Test 6: Should delete verification token and return true")
	void testDeleteById_ShouldReturnOkWithTrue() {
		// Given
		String verificationTokenId = "1";
		
		// When
		ResponseEntity<Boolean> response = verificationTokenResource.deleteById(verificationTokenId);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(true, response.getBody());
		verify(verificationTokenService).deleteById(anyInt());
	}
}

