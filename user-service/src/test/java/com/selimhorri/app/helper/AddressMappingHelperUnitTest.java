package com.selimhorri.app.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.selimhorri.app.domain.Address;
import com.selimhorri.app.domain.User;
import com.selimhorri.app.dto.AddressDto;
import com.selimhorri.app.dto.UserDto;

@DisplayName("AddressMappingHelper Unit Tests")
class AddressMappingHelperUnitTest {
	
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
	@DisplayName("Test 1: Should map Address to AddressDto successfully")
	void testMap_FromAddress_ShouldReturnAddressDto() {
		// When
		AddressDto result = AddressMappingHelper.map(address);
		
		// Then
		assertNotNull(result);
		assertEquals(address.getAddressId(), result.getAddressId());
		assertEquals(address.getFullAddress(), result.getFullAddress());
		assertEquals(address.getPostalCode(), result.getPostalCode());
		assertEquals(address.getCity(), result.getCity());
		assertNotNull(result.getUserDto());
		assertEquals(address.getUser().getUserId(), result.getUserDto().getUserId());
		assertEquals(address.getUser().getFirstName(), result.getUserDto().getFirstName());
		assertEquals(address.getUser().getLastName(), result.getUserDto().getLastName());
	}
	
	@Test
	@DisplayName("Test 2: Should map AddressDto to Address successfully")
	void testMap_FromAddressDto_ShouldReturnAddress() {
		// When
		Address result = AddressMappingHelper.map(addressDto);
		
		// Then
		assertNotNull(result);
		assertEquals(addressDto.getAddressId(), result.getAddressId());
		assertEquals(addressDto.getFullAddress(), result.getFullAddress());
		assertEquals(addressDto.getPostalCode(), result.getPostalCode());
		assertEquals(addressDto.getCity(), result.getCity());
		assertNotNull(result.getUser());
		assertEquals(addressDto.getUserDto().getUserId(), result.getUser().getUserId());
		assertEquals(addressDto.getUserDto().getFirstName(), result.getUser().getFirstName());
		assertEquals(addressDto.getUserDto().getLastName(), result.getUser().getLastName());
	}
}

