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

import com.selimhorri.app.dto.AddressDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.dto.response.collection.DtoCollectionResponse;
import com.selimhorri.app.service.AddressService;

@ExtendWith(MockitoExtension.class)
@DisplayName("AddressResource Unit Tests")
class AddressResourceUnitTest {
	
	@Mock
	private AddressService addressService;
	
	@InjectMocks
	private AddressResource addressResource;
	
	private AddressDto addressDto;
	
	@BeforeEach
	void setUp() {
		UserDto userDto = UserDto.builder()
			.userId(1)
			.firstName("John")
			.lastName("Doe")
			.email("john.doe@example.com")
			.build();
		
		addressDto = AddressDto.builder()
			.addressId(1)
			.fullAddress("123 Main St")
			.postalCode("12345")
			.city("Anytown")
			.userDto(userDto)
			.build();
	}
	
	@Test
	@DisplayName("Test 1: Should return all addresses with 200 status")
	void testFindAll_ShouldReturnOkWithList() {
		// Given
		List<AddressDto> addresses = Arrays.asList(addressDto);
		when(addressService.findAll()).thenReturn(addresses);
		
		// When
		ResponseEntity<DtoCollectionResponse<AddressDto>> response = addressResource.findAll();
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(1, response.getBody().getCollection().size());
		verify(addressService).findAll();
	}
	
	@Test
	@DisplayName("Test 2: Should return address by id with 200 status")
	void testFindById_ShouldReturnOkWithAddress() {
		// Given
		String addressId = "1";
		when(addressService.findById(anyInt())).thenReturn(addressDto);
		
		// When
		ResponseEntity<AddressDto> response = addressResource.findById(addressId);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(1, response.getBody().getAddressId());
		verify(addressService).findById(anyInt());
	}
	
	@Test
	@DisplayName("Test 3: Should save address and return 200 status")
	void testSave_ShouldReturnOkWithSavedAddress() {
		// Given
		when(addressService.save(any(AddressDto.class))).thenReturn(addressDto);
		
		// When
		ResponseEntity<AddressDto> response = addressResource.save(addressDto);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		verify(addressService).save(any(AddressDto.class));
	}
	
	@Test
	@DisplayName("Test 4: Should update address and return 200 status")
	void testUpdate_ShouldReturnOkWithUpdatedAddress() {
		// Given
		AddressDto updatedDto = AddressDto.builder()
			.addressId(1)
			.fullAddress("456 New St")
			.postalCode("54321")
			.city("Newtown")
			.build();
		when(addressService.update(any(AddressDto.class))).thenReturn(updatedDto);
		
		// When
		ResponseEntity<AddressDto> response = addressResource.update(updatedDto);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("456 New St", response.getBody().getFullAddress());
		verify(addressService).update(any(AddressDto.class));
	}
	
	@Test
	@DisplayName("Test 5: Should update address with id and return 200 status")
	void testUpdateWithId_ShouldReturnOkWithUpdatedAddress() {
		// Given
		String addressId = "1";
		AddressDto updatedDto = AddressDto.builder()
			.addressId(1)
			.fullAddress("789 Old Rd")
			.postalCode("98765")
			.city("Oldville")
			.build();
		when(addressService.update(anyInt(), any(AddressDto.class))).thenReturn(updatedDto);
		
		// When
		ResponseEntity<AddressDto> response = addressResource.update(addressId, updatedDto);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("789 Old Rd", response.getBody().getFullAddress());
		verify(addressService).update(anyInt(), any(AddressDto.class));
	}
	
	@Test
	@DisplayName("Test 6: Should delete address and return true")
	void testDeleteById_ShouldReturnOkWithTrue() {
		// Given
		String addressId = "1";
		
		// When
		ResponseEntity<Boolean> response = addressResource.deleteById(addressId);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(true, response.getBody());
		verify(addressService).deleteById(anyInt());
	}
}

