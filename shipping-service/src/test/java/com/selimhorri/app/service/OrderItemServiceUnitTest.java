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
import org.springframework.web.client.RestTemplate;

import com.selimhorri.app.constant.AppConstant;
import com.selimhorri.app.domain.OrderItem;
import com.selimhorri.app.domain.id.OrderItemId;
import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.dto.OrderItemDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.exception.wrapper.OrderItemNotFoundException;
import com.selimhorri.app.repository.OrderItemRepository;
import com.selimhorri.app.service.impl.OrderItemServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderItemService Unit Tests")
class OrderItemServiceUnitTest {
	
	@Mock
	private OrderItemRepository orderItemRepository;
	
	@Mock
	private RestTemplate restTemplate;
	
	@InjectMocks
	private OrderItemServiceImpl orderItemService;
	
	private OrderItemDto orderItemDto;
	private OrderItem orderItem;
	private OrderItemId orderItemId;
	private ProductDto productDto;
	private OrderDto orderDto;
	
	@BeforeEach
	void setUp() {
		orderItemId = new OrderItemId();
		orderItemId.setProductId(100);
		orderItemId.setOrderId(1);
		
		productDto = ProductDto.builder()
			.productId(100)
			.productTitle("Test Product")
			.priceUnit(99.99)
			.build();
		
		orderDto = OrderDto.builder()
			.orderId(1)
			.orderDesc("Test Order")
			.orderFee(199.99)
			.build();
		
		orderItemDto = OrderItemDto.builder()
			.productId(100)
			.orderId(1)
			.orderedQuantity(2)
			.build();
		
		orderItem = OrderItem.builder()
			.productId(100)
			.orderId(1)
			.orderedQuantity(2)
			.build();
	}
	
	@Test
	@DisplayName("Test 1: Should find all order items successfully")
	void testFindAll_ShouldReturnListOfOrderItems() {
		// Given - Create two different order items with different IDs
		OrderItem orderItem2 = OrderItem.builder()
			.productId(200)
			.orderId(2)
			.orderedQuantity(3)
			.build();
		
		List<OrderItem> orderItems = Arrays.asList(orderItem, orderItem2);
		when(orderItemRepository.findAll()).thenReturn(orderItems);
		
		// Mock RestTemplate calls - for each orderItem, we need ProductDto and OrderDto
		// First orderItem: productId=100, orderId=1
		when(restTemplate.getForObject(
			AppConstant.DiscoveredDomainsApi.PRODUCT_SERVICE_API_URL + "/100", 
			ProductDto.class))
			.thenReturn(productDto);
		when(restTemplate.getForObject(
			AppConstant.DiscoveredDomainsApi.ORDER_SERVICE_API_URL + "/1", 
			OrderDto.class))
			.thenReturn(orderDto);
		
		// Second orderItem: productId=200, orderId=2
		ProductDto productDto2 = ProductDto.builder()
			.productId(200)
			.productTitle("Another Product")
			.priceUnit(199.99)
			.build();
		OrderDto orderDto2 = OrderDto.builder()
			.orderId(2)
			.orderDesc("Second Order")
			.orderFee(299.99)
			.build();
		
		when(restTemplate.getForObject(
			AppConstant.DiscoveredDomainsApi.PRODUCT_SERVICE_API_URL + "/200", 
			ProductDto.class))
			.thenReturn(productDto2);
		when(restTemplate.getForObject(
			AppConstant.DiscoveredDomainsApi.ORDER_SERVICE_API_URL + "/2", 
			OrderDto.class))
			.thenReturn(orderDto2);
		
		// When
		List<OrderItemDto> result = orderItemService.findAll();
		
		// Then
		assertNotNull(result);
		assertEquals(2, result.size());
		verify(orderItemRepository).findAll();
	}
	
	@Test
	@DisplayName("Test 2: Should find order item by id successfully")
	void testFindById_ShouldReturnOrderItemDto() {
		// Given
		when(orderItemRepository.findById(orderItemId)).thenReturn(Optional.of(orderItem));
		when(restTemplate.getForObject(
			AppConstant.DiscoveredDomainsApi.PRODUCT_SERVICE_API_URL + "/100", 
			ProductDto.class))
			.thenReturn(productDto);
		when(restTemplate.getForObject(
			AppConstant.DiscoveredDomainsApi.ORDER_SERVICE_API_URL + "/1", 
			OrderDto.class))
			.thenReturn(orderDto);
		
		// When
		OrderItemDto result = orderItemService.findById(orderItemId);
		
		// Then
		assertNotNull(result);
		assertEquals(orderItemId.getProductId(), result.getProductId());
		assertEquals(orderItemId.getOrderId(), result.getOrderId());
		verify(orderItemRepository).findById(orderItemId);
	}
	
	@Test
	@DisplayName("Test 3: Should throw exception when order item not found by id")
	void testFindById_WhenOrderItemNotFound_ShouldThrowException() {
		// Given
		when(orderItemRepository.findById(orderItemId)).thenReturn(Optional.empty());
		
		// When & Then
		assertThrows(OrderItemNotFoundException.class, () -> {
			orderItemService.findById(orderItemId);
		});
		verify(orderItemRepository).findById(orderItemId);
	}
	
	@Test
	@DisplayName("Test 4: Should save order item successfully")
	void testSave_ShouldReturnSavedOrderItemDto() {
		// Given
		when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem);
		
		// When
		OrderItemDto result = orderItemService.save(orderItemDto);
		
		// Then
		assertNotNull(result);
		assertEquals(orderItemDto.getProductId(), result.getProductId());
		assertEquals(orderItemDto.getOrderId(), result.getOrderId());
		assertEquals(orderItemDto.getOrderedQuantity(), result.getOrderedQuantity());
		verify(orderItemRepository).save(any(OrderItem.class));
	}
	
	@Test
	@DisplayName("Test 5: Should update order item successfully")
	void testUpdate_ShouldReturnUpdatedOrderItemDto() {
		// Given
		OrderItemDto updatedDto = OrderItemDto.builder()
			.productId(100)
			.orderId(1)
			.orderedQuantity(5)
			.build();
		OrderItem updatedOrderItem = OrderItem.builder()
			.productId(100)
			.orderId(1)
			.orderedQuantity(5)
			.build();
		
		when(orderItemRepository.save(any(OrderItem.class))).thenReturn(updatedOrderItem);
		
		// When
		OrderItemDto result = orderItemService.update(updatedDto);
		
		// Then
		assertNotNull(result);
		assertEquals(5, result.getOrderedQuantity());
		verify(orderItemRepository).save(any(OrderItem.class));
	}
	
	@Test
	@DisplayName("Test 6: Should delete order item by id successfully")
	void testDeleteById_ShouldDeleteOrderItem() {
		// Given
		// When
		orderItemService.deleteById(orderItemId);
		
		// Then
		verify(orderItemRepository).deleteById(orderItemId);
	}
	
	@Test
	@DisplayName("Test 7: Should populate product and order DTOs when finding all")
	void testFindAll_ShouldPopulateProductAndOrderDtos() {
		// Given
		List<OrderItem> orderItems = Arrays.asList(orderItem);
		when(orderItemRepository.findAll()).thenReturn(orderItems);
		when(restTemplate.getForObject(
			AppConstant.DiscoveredDomainsApi.PRODUCT_SERVICE_API_URL + "/100", 
			ProductDto.class))
			.thenReturn(productDto);
		when(restTemplate.getForObject(
			AppConstant.DiscoveredDomainsApi.ORDER_SERVICE_API_URL + "/1", 
			OrderDto.class))
			.thenReturn(orderDto);
		
		// When
		List<OrderItemDto> result = orderItemService.findAll();
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.size());
		assertNotNull(result.get(0).getProductDto());
		assertNotNull(result.get(0).getOrderDto());
		assertEquals(100, result.get(0).getProductDto().getProductId());
		assertEquals(1, result.get(0).getOrderDto().getOrderId());
		verify(orderItemRepository).findAll();
	}
	
	@Test
	@DisplayName("Test 8: Should populate product and order DTOs when finding by id")
	void testFindById_ShouldPopulateProductAndOrderDtos() {
		// Given
		when(orderItemRepository.findById(orderItemId)).thenReturn(Optional.of(orderItem));
		when(restTemplate.getForObject(
			AppConstant.DiscoveredDomainsApi.PRODUCT_SERVICE_API_URL + "/100", 
			ProductDto.class))
			.thenReturn(productDto);
		when(restTemplate.getForObject(
			AppConstant.DiscoveredDomainsApi.ORDER_SERVICE_API_URL + "/1", 
			OrderDto.class))
			.thenReturn(orderDto);
		
		// When
		OrderItemDto result = orderItemService.findById(orderItemId);
		
		// Then
		assertNotNull(result);
		assertNotNull(result.getProductDto());
		assertNotNull(result.getOrderDto());
		assertEquals("Test Product", result.getProductDto().getProductTitle());
		assertEquals("Test Order", result.getOrderDto().getOrderDesc());
		verify(orderItemRepository).findById(orderItemId);
	}
	
	@Test
	@DisplayName("Test 9: Should handle order item with different quantities")
	void testSave_WithDifferentQuantities_ShouldReturnOrderItemDto() {
		// Given
		OrderItemDto quantityDto = OrderItemDto.builder()
			.productId(200)
			.orderId(2)
			.orderedQuantity(10)
			.build();
		OrderItem quantityOrderItem = OrderItem.builder()
			.productId(200)
			.orderId(2)
			.orderedQuantity(10)
			.build();
		
		when(orderItemRepository.save(any(OrderItem.class))).thenReturn(quantityOrderItem);
		
		// When
		OrderItemDto result = orderItemService.save(quantityDto);
		
		// Then
		assertNotNull(result);
		assertEquals(10, result.getOrderedQuantity());
		assertEquals(200, result.getProductId());
		assertEquals(2, result.getOrderId());
		verify(orderItemRepository).save(any(OrderItem.class));
	}
}

