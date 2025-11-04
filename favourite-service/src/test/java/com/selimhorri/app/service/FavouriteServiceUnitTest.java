package com.selimhorri.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
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
import org.springframework.web.client.RestTemplate;

import com.selimhorri.app.constant.AppConstant;
import com.selimhorri.app.domain.Favourite;
import com.selimhorri.app.domain.id.FavouriteId;
import com.selimhorri.app.dto.FavouriteDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.exception.wrapper.FavouriteNotFoundException;
import com.selimhorri.app.repository.FavouriteRepository;
import com.selimhorri.app.service.impl.FavouriteServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("FavouriteService Unit Tests")
class FavouriteServiceUnitTest {
	
	@Mock
	private FavouriteRepository favouriteRepository;
	
	@Mock
	private RestTemplate restTemplate;
	
	@InjectMocks
	private FavouriteServiceImpl favouriteService;
	
	private FavouriteDto favouriteDto;
	private Favourite favourite;
	private FavouriteId favouriteId;
	private UserDto userDto;
	private ProductDto productDto;
	
	@BeforeEach
	void setUp() {
		LocalDateTime now = LocalDateTime.now();
		
		favouriteId = new FavouriteId();
		favouriteId.setUserId(1);
		favouriteId.setProductId(100);
		favouriteId.setLikeDate(now);
		
		favouriteDto = FavouriteDto.builder()
			.userId(1)
			.productId(100)
			.likeDate(now)
			.build();
		
		favourite = Favourite.builder()
			.userId(1)
			.productId(100)
			.likeDate(now)
			.build();
		
		userDto = UserDto.builder()
			.userId(1)
			.firstName("John")
			.lastName("Doe")
			.email("john.doe@example.com")
			.build();
		
		productDto = ProductDto.builder()
			.productId(100)
			.productTitle("Test Product")
			.priceUnit(99.99)
			.build();
	}
	
	@Test
	@DisplayName("Test 1: Should find all favourites successfully")
	void testFindAll_ShouldReturnListOfFavourites() {
		// Given
		List<Favourite> favourites = Arrays.asList(favourite, favourite);
		when(favouriteRepository.findAll()).thenReturn(favourites);
		when(restTemplate.getForObject(anyString(), any(Class.class)))
			.thenReturn(userDto, productDto);
		
		// When
		List<FavouriteDto> result = favouriteService.findAll();
		
		// Then
		assertNotNull(result);
		assertEquals(2, result.size());
		verify(favouriteRepository).findAll();
	}
	
	@Test
	@DisplayName("Test 2: Should find favourite by id successfully")
	void testFindById_ShouldReturnFavouriteDto() {
		// Given
		when(favouriteRepository.findById(favouriteId)).thenReturn(Optional.of(favourite));
		when(restTemplate.getForObject(anyString(), any(Class.class)))
			.thenReturn(userDto, productDto);
		
		// When
		FavouriteDto result = favouriteService.findById(favouriteId);
		
		// Then
		assertNotNull(result);
		assertEquals(favouriteId.getUserId(), result.getUserId());
		assertEquals(favouriteId.getProductId(), result.getProductId());
		verify(favouriteRepository).findById(favouriteId);
	}
	
	@Test
	@DisplayName("Test 3: Should throw exception when favourite not found by id")
	void testFindById_WhenFavouriteNotFound_ShouldThrowException() {
		// Given
		when(favouriteRepository.findById(favouriteId)).thenReturn(Optional.empty());
		
		// When & Then
		assertThrows(FavouriteNotFoundException.class, () -> {
			favouriteService.findById(favouriteId);
		});
		verify(favouriteRepository).findById(favouriteId);
	}
	
	@Test
	@DisplayName("Test 4: Should save favourite successfully")
	void testSave_ShouldReturnSavedFavouriteDto() {
		// Given
		when(favouriteRepository.save(any(Favourite.class))).thenReturn(favourite);
		
		// When
		FavouriteDto result = favouriteService.save(favouriteDto);
		
		// Then
		assertNotNull(result);
		assertEquals(favouriteDto.getUserId(), result.getUserId());
		assertEquals(favouriteDto.getProductId(), result.getProductId());
		verify(favouriteRepository).save(any(Favourite.class));
	}
	
	@Test
	@DisplayName("Test 5: Should update favourite successfully")
	void testUpdate_ShouldReturnUpdatedFavouriteDto() {
		// Given
		FavouriteDto updatedDto = FavouriteDto.builder()
			.userId(1)
			.productId(200)
			.likeDate(LocalDateTime.now())
			.build();
		Favourite updatedFavourite = Favourite.builder()
			.userId(1)
			.productId(200)
			.likeDate(LocalDateTime.now())
			.build();
		
		when(favouriteRepository.save(any(Favourite.class))).thenReturn(updatedFavourite);
		
		// When
		FavouriteDto result = favouriteService.update(updatedDto);
		
		// Then
		assertNotNull(result);
		assertEquals(200, result.getProductId());
		verify(favouriteRepository).save(any(Favourite.class));
	}
	
	@Test
	@DisplayName("Test 6: Should delete favourite by id successfully")
	void testDeleteById_ShouldDeleteFavourite() {
		// Given
		// When
		favouriteService.deleteById(favouriteId);
		
		// Then
		verify(favouriteRepository).deleteById(favouriteId);
	}
	
	@Test
	@DisplayName("Test 7: Should populate user and product DTOs when finding all")
	void testFindAll_ShouldPopulateUserAndProductDtos() {
		// Given
		List<Favourite> favourites = Arrays.asList(favourite);
		when(favouriteRepository.findAll()).thenReturn(favourites);
		when(restTemplate.getForObject(
			AppConstant.DiscoveredDomainsApi.USER_SERVICE_API_URL + "/1", 
			UserDto.class))
			.thenReturn(userDto);
		when(restTemplate.getForObject(
			AppConstant.DiscoveredDomainsApi.PRODUCT_SERVICE_API_URL + "/100", 
			ProductDto.class))
			.thenReturn(productDto);
		
		// When
		List<FavouriteDto> result = favouriteService.findAll();
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.size());
		assertNotNull(result.get(0).getUserDto());
		assertNotNull(result.get(0).getProductDto());
		verify(favouriteRepository).findAll();
	}
	
	@Test
	@DisplayName("Test 8: Should populate user and product DTOs when finding by id")
	void testFindById_ShouldPopulateUserAndProductDtos() {
		// Given
		when(favouriteRepository.findById(favouriteId)).thenReturn(Optional.of(favourite));
		when(restTemplate.getForObject(
			AppConstant.DiscoveredDomainsApi.USER_SERVICE_API_URL + "/1", 
			UserDto.class))
			.thenReturn(userDto);
		when(restTemplate.getForObject(
			AppConstant.DiscoveredDomainsApi.PRODUCT_SERVICE_API_URL + "/100", 
			ProductDto.class))
			.thenReturn(productDto);
		
		// When
		FavouriteDto result = favouriteService.findById(favouriteId);
		
		// Then
		assertNotNull(result);
		assertNotNull(result.getUserDto());
		assertNotNull(result.getProductDto());
		assertEquals("John", result.getUserDto().getFirstName());
		assertEquals("Test Product", result.getProductDto().getProductTitle());
		verify(favouriteRepository).findById(favouriteId);
	}
}

