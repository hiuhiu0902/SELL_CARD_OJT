package com.demo.sell_card_demo1.service;

import com.demo.sell_card_demo1.dto.OrderHistoryResponse;
import com.demo.sell_card_demo1.enums.OrderStatus;
import com.demo.sell_card_demo1.repository.OrderRepository;
import com.demo.sell_card_demo1.repository.ProductRepository;
import com.demo.sell_card_demo1.repository.ProductVariantsRepository;
import com.demo.sell_card_demo1.repository.StorageRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@SpringBootTest
public class OrderServiceTest {
	@Autowired
	private OrderService orderService;

	@MockBean
	private OrderRepository orderRepository;

	@MockBean
	private AuthenticationService authenticationService;

	@MockBean
	private ProductRepository productRepository;

	@MockBean
	private ProductVariantsRepository productVariantsRepository;

	@MockBean
	private StorageRepository storageRepository;

	@Test
	public void searchOrders() {
		LocalDateTime from = null;
		LocalDateTime to = null;
		String username = "abc";
		OrderStatus status = OrderStatus.CANCELLED;
		Pageable pageable = null;
		Page<OrderHistoryResponse> expected = null;
		Page<OrderHistoryResponse> actual = orderService.searchOrders(from, to, username, status, pageable);

		assertEquals(expected, actual);
	}
}
