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

import com.selimhorri.app.domain.Address;
import com.selimhorri.app.domain.User;
import com.selimhorri.app.dto.AddressDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.exception.wrapper.AddressNotFoundException;
import com.selimhorri.app.repository.AddressRepository;
import com.selimhorri.app.service.impl.AddressServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("AddressService Unit Tests")
class AddressServiceUnitTest {
	
	@Mock
	private AddressRepository addressRepository;
	
	@InjectMocks
	private AddressServiceImpl addressService;
	
	private AddressDto addressDto;
	private Address address;
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
		
		addressDto = AddressDto.builder()
			.addressId(1)
			.fullAddress("123 Main St")
			.postalCode("12345")
			.city("Anytown")
			.userDto(userDto)
			.build();
		
		address = Address.builder()
			.addressId(1)
			.fullAddress("123 Main St")
			.postalCode("12345")
			.city("Anytown")
			.user(user)
			.build();
	}
	
	@Test
	@DisplayName("Test 1: Should find all addresses successfully")
	void testFindAll_ShouldReturnListOfAddresses() {
		// Given - Create two different addresses with different IDs
		User user2 = User.builder().userId(2).firstName("Jane").build();
		Address address2 = Address.builder()
			.addressId(2)
			.fullAddress("456 Oak Ave")
			.postalCode("67890")
			.city("Otherville")
			.user(user2)
			.build();
		
		List<Address> addresses = Arrays.asList(address, address2);
		when(addressRepository.findAll()).thenReturn(addresses);
		
		// When
		List<AddressDto> result = addressService.findAll();
		
		// Then
		assertNotNull(result);
		assertEquals(2, result.size());
		verify(addressRepository).findAll();
	}
	
	@Test
	@DisplayName("Test 2: Should find address by id successfully")
	void testFindById_ShouldReturnAddressDto() {
		// Given
		Integer addressId = 1;
		when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));
		
		// When
		AddressDto result = addressService.findById(addressId);
		
		// Then
		assertNotNull(result);
		assertEquals(addressId, result.getAddressId());
		assertEquals("123 Main St", result.getFullAddress());
		verify(addressRepository).findById(addressId);
	}
	
	@Test
	@DisplayName("Test 3: Should throw exception when address not found")
	void testFindById_WhenAddressNotFound_ShouldThrowException() {
		// Given
		Integer addressId = 999;
		when(addressRepository.findById(addressId)).thenReturn(Optional.empty());
		
		// When & Then
		assertThrows(AddressNotFoundException.class, () -> {
			addressService.findById(addressId);
		});
		verify(addressRepository).findById(addressId);
	}
	
	@Test
	@DisplayName("Test 4: Should save address successfully")
	void testSave_ShouldReturnSavedAddressDto() {
		// Given
		when(addressRepository.save(any(Address.class))).thenReturn(address);
		
		// When
		AddressDto result = addressService.save(addressDto);
		
		// Then
		assertNotNull(result);
		assertEquals(addressDto.getFullAddress(), result.getFullAddress());
		verify(addressRepository).save(any(Address.class));
	}
	
	@Test
	@DisplayName("Test 5: Should update address successfully")
	void testUpdate_ShouldReturnUpdatedAddressDto() {
		// Given
		AddressDto updatedDto = AddressDto.builder()
			.addressId(1)
			.fullAddress("456 New St")
			.postalCode("54321")
			.city("Newtown")
			.userDto(userDto)
			.build();
		
		Address updatedAddress = Address.builder()
			.addressId(1)
			.fullAddress("456 New St")
			.postalCode("54321")
			.city("Newtown")
			.user(user)
			.build();
		
		when(addressRepository.save(any(Address.class))).thenReturn(updatedAddress);
		
		// When
		AddressDto result = addressService.update(updatedDto);
		
		// Then
		assertNotNull(result);
		assertEquals("456 New St", result.getFullAddress());
		verify(addressRepository).save(any(Address.class));
	}
	
	@Test
	@DisplayName("Test 6: Should update address with ID successfully")
	void testUpdateWithId_ShouldReturnUpdatedAddressDto() {
		// Given
		Integer addressId = 1;
		AddressDto updatedDto = AddressDto.builder()
			.addressId(1)
			.fullAddress("789 Old Rd")
			.postalCode("98765")
			.city("Oldville")
			.userDto(userDto)
			.build();
		
		when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));
		when(addressRepository.save(any(Address.class))).thenReturn(address);
		
		// When
		AddressDto result = addressService.update(addressId, updatedDto);
		
		// Then
		assertNotNull(result);
		verify(addressRepository).findById(addressId);
		verify(addressRepository).save(any(Address.class));
	}
	
	@Test
	@DisplayName("Test 7: Should delete address by ID successfully")
	void testDeleteById_ShouldDeleteAddress() {
		// Given
		Integer addressId = 1;
		
		// When
		addressService.deleteById(addressId);
		
		// Then
		verify(addressRepository).deleteById(addressId);
	}
}

