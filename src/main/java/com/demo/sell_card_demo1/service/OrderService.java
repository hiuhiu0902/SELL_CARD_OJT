package com.demo.sell_card_demo1.service;

import com.demo.sell_card_demo1.dto.*;
import com.demo.sell_card_demo1.entity.*;
import com.demo.sell_card_demo1.enums.CardStatus;
import com.demo.sell_card_demo1.enums.OrderStatus;
import com.demo.sell_card_demo1.exception.BadRequestException;
import com.demo.sell_card_demo1.repository.OrderRepository;
import com.demo.sell_card_demo1.repository.ProductRepository;
import com.demo.sell_card_demo1.repository.ProductVariantsRepository;
import com.demo.sell_card_demo1.repository.StorageRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    ProductVariantsRepository productVariantsRepository;

    @Autowired
    StorageRepository stockRepository;


    @Transactional // Bắt buộc phải có để Lock hoạt động
    public Order createOrder(CreateOrderRequest request) {
        User user = authenticationService.getCurrentUser();
        Order order = new Order();
        order.setUser(user);
        order.setPayment(request.getPaymentMethod());
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> items = new ArrayList<>();
        Long total = 0L;

        for (OrderItemRequest item : request.getOrderItemRequests()) {
            ProductVariant variant = productVariantsRepository.findById(item.getVariantId())
                    .orElseThrow(() -> new BadRequestException("Variant not found"));

            // Cố gắng lấy và KHÓA số lượng thẻ cần thiết ngay lập tức
            List<Storage> storagesToSell = stockRepository.findAndLockCards(
                    CardStatus.UNUSED,
                    variant.getVariantId(),
                    PageRequest.of(0, item.getQuantity())
            );

            if (storagesToSell.size() < item.getQuantity()) {
                throw new BadRequestException("Not enough stock for variant: " + variant.getProduct().getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(variant.getProduct());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(variant.getPrice());

            items.add(orderItem);
            total += variant.getPrice() * item.getQuantity();

            // Cập nhật trạng thái thẻ
            for (Storage storage : storagesToSell) {
                storage.setStatus(CardStatus.PENDING_PAYMENT);
                storage.setOrderItem(orderItem);
                stockRepository.save(storage);
            }
        }

        order.setOrderItems(items);
        order.setTotalAmount(total);

        return orderRepository.save(order);
    }

    public void handlePaymentSuccess(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BadRequestException("Order not found with id: " + orderId));
        order.setStatus(OrderStatus.COMPLETED);

        for (OrderItem item : order.getOrderItems()) {
            List<Storage> storages = stockRepository.findByOrderItem_ItemId(item.getItemId());
            for (Storage storage : storages) {
                storage.setStatus(CardStatus.USED);
                storage.setActivationDate(LocalDateTime.now());
                stockRepository.save(storage);
            }
        }
        orderRepository.save(order);
    }

    public void handlePaymentFailure(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BadRequestException("Order not found with id: " + orderId));
        order.setStatus(OrderStatus.CANCELLED);

        for (OrderItem item : order.getOrderItems()) {
            List<Storage> storages = stockRepository.findByOrderItem_ItemId(item.getItemId());
            for (Storage storage : storages) {
                storage.setStatus(CardStatus.UNUSED);
                storage.setOrderItem(null);
                stockRepository.save(storage);
            }
        }
        orderRepository.save(order);
    }

    public List<OrderHistoryResponse> getOrderHistoryForCurrentUser(Pageable pageable) {
        User user = authenticationService.getCurrentUser();
        Page<Order> orders = orderRepository.findByUser_UserIdOrderByCreatedAtDesc(user.getUserId(), pageable);
        List<OrderHistoryResponse> responseList = new ArrayList<>();
        for (Order order : orders.getContent()) {
            OrderHistoryResponse response = new OrderHistoryResponse();
            response.setOrderId(order.getOrderId());
            response.setOrderStatus(order.getStatus());
            response.setTotalAmount(order.getTotalAmount());
            response.setOrderDate(order.getCreatedAt());
            responseList.add(response);
        }
        return responseList;
    }

    public OrderDetailResponse getOrderDetailForCurrentUser(Long orderId) {
        User user = authenticationService.getCurrentUser();
        Order order = orderRepository.findByOrderIdAndUser_UserId(orderId, user.getUserId()).orElse(null);
        if (!order.getStatus().equals(OrderStatus.COMPLETED)) {
            throw new BadRequestException("Order is not completed");
        }
        OrderDetailResponse response = new OrderDetailResponse();
        response.setOrderId(order.getOrderId());
        response.setOrderStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());

        List<PurchasedItemResponse> purchasedItems = new ArrayList<>();
        for (OrderItem item : order.getOrderItems()) {
            PurchasedItemResponse responseItem = new PurchasedItemResponse();
            responseItem.setProductName(item.getProduct().getName());
            responseItem.setQuantity(item.getQuantity());
            responseItem.setPricePerUnit(item.getPrice());

            List<Storage> storages = stockRepository.findByOrderItem_ItemId(item.getItemId());
            List<CardInfo> cardInfos = new ArrayList<>();
            for (Storage storage : storages) {
                CardInfo cardInfo = new CardInfo();
                cardInfo.setCode(storage.getActivateCode());
                cardInfo.setExpirationDate(storage.getExpirationDate());
                cardInfos.add(cardInfo);
            }
            responseItem.setCards(cardInfos);
        }
        response.setPurchasedItems(purchasedItems);

        return response;
    }

    /**
     * Lấy tất cả đơn hàng (dành cho admin).
     * @param pageable
     * @return Page<Order>
     */
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    /**
     * Cập nhật trạng thái của một đơn hàng (dành cho admin).
     * @param orderId ID của đơn hàng
     * @param newStatus Trạng thái mới
     * @return Order đã được cập nhật
     */
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BadRequestException("Order not found with id: " + orderId));
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    /**
     * Người dùng hủy đơn hàng của chính họ.
     * Chỉ cho phép hủy khi đơn hàng đang ở trạng thái PENDING.
     * @param orderId ID của đơn hàng cần hủy
     */
    public void cancelOrder(Long orderId) {
        User user = authenticationService.getCurrentUser();
        Order order = orderRepository.findByOrderIdAndUser_UserId(orderId, user.getUserId()).orElse(null);
        if (order == null) {
            throw new BadRequestException("Order not found");
        }
        if (!order.getStatus().equals(OrderStatus.PENDING)) {
            throw new BadRequestException("Only pending orders can be cancelled");
        }
        order.setStatus(OrderStatus.CANCELLED);
        for (OrderItem item : order.getOrderItems()) {
            List<Storage> storages = stockRepository.findByOrderItem_ItemId(item.getItemId());
            for (Storage storage : storages) {
                storage.setStatus(CardStatus.UNUSED);
                storage.setOrderItem(null);
                stockRepository.save(storage);
            }
        }
    }


    public Page<Order> searchOrders(LocalDateTime from, LocalDateTime to, String username, OrderStatus status, Pageable pageable) {
        Specification<Order> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (from != null && to != null) {
                predicates.add(cb.between(root.get("createdAt"), from, to));
            }
            if (username != null && !username.isEmpty()) {
                predicates.add(cb.like(root.get("user").get("username"), "%" + username + "%"));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return orderRepository.findAll(spec, pageable);
    }

    // 2. Hoàn tiền (Refund)
    @Transactional
    public void refundOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BadRequestException("Order not found"));

        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new BadRequestException("Only completed orders can be refunded");
        }
        order.setStatus(OrderStatus.REFUNDED);
        for (OrderItem item : order.getOrderItems()) {
            List<Storage> cards = stockRepository.findByOrderItem_ItemId(item.getItemId());
            for (Storage card : cards) {
                card.setStatus(CardStatus.ERROR);
                card.setOrderItem(null);
                card.setActivationDate(null);
            }
            stockRepository.saveAll(cards);
        }

        orderRepository.save(order);
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }
}
