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

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        User user = authenticationService.getCurrentUser();
        Order order = new Order();
        order.setUser(user);
        order.setPayment(request.getPaymentMethod());
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        long totalAmount = processOrderItems(request.getOrderItemRequests(), order);
        order.setTotalAmount(totalAmount);
        return orderRepository.save(order);
    }
    @Transactional
    public void rollbackOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return;

        if (order.getStatus() == OrderStatus.COMPLETED) return;

        for (OrderItem item : order.getOrderItems()) {
            List<Storage> lockedCards = stockRepository.findByOrderItem_ItemId(item.getItemId());
            for (Storage card : lockedCards) {
                card.setStatus(CardStatus.UNUSED); // Trả về trạng thái chưa bán
                card.setOrderItem(null);           // Hủy liên kết
            }
            stockRepository.saveAll(lockedCards);
        }

        // 3. Tùy chọn: Nếu bạn muốn xóa hẳn khỏi DB thì dùng dòng dưới thay vì setStatus
        orderRepository.delete(order);
    }
    // =========================================================================
    // 2. CẬP NHẬT ĐƠN HÀNG (Sửa, Xóa, Thêm item khi chưa thanh toán)
    // =========================================================================
    @Transactional
    public Order updatePendingOrder(Long orderId, CreateOrderRequest request) {
        User user = authenticationService.getCurrentUser();

        // 1. Tìm đơn hàng và validate quyền
        Order order = orderRepository.findByOrderIdAndUser_UserId(orderId, user.getUserId())
                .orElseThrow(() -> new BadRequestException("Order not found or access denied"));

        // 2. Chỉ cho phép sửa khi đang PENDING
        if (!order.getStatus().equals(OrderStatus.PENDING)) {
            throw new BadRequestException("Only PENDING orders can be updated");
        }

        // 3. HOÀN TRẢ KHO CŨ (Release Stock)
        // Duyệt qua các item cũ để nhả thẻ ra
        for (OrderItem oldItem : order.getOrderItems()) {
            List<Storage> lockedCards = stockRepository.findByOrderItem_ItemId(oldItem.getItemId());
            for (Storage card : lockedCards) {
                card.setStatus(CardStatus.UNUSED); // Trả về trạng thái chưa dùng
                card.setOrderItem(null);           // Gỡ liên kết
            }
            stockRepository.saveAll(lockedCards);
        }

        // 4. Xóa sạch các OrderItem cũ
        // Lưu ý: Trong Entity Order phải có @OneToMany(orphanRemoval = true) thì lệnh clear() mới xóa DB
        order.getOrderItems().clear();

        // Update lại phương thức thanh toán nếu user thay đổi
        if (request.getPaymentMethod() != null) {
            order.setPayment(request.getPaymentMethod());
        }

        // 5. TÍNH TOÁN KHO MỚI & TẠO ITEM MỚI (Dùng lại hàm helper)
        long newTotalAmount = processOrderItems(request.getOrderItemRequests(), order);

        order.setTotalAmount(newTotalAmount);
        order.setCreatedAt(LocalDateTime.now()); // Cập nhật lại thời gian (tùy nghiệp vụ)

        return orderRepository.save(order);
    }
    // ... các imports
    // Thêm vào trong class OrderService

    // Thêm đoạn này vào trong class OrderService
    @Transactional
    public void handlePaymentCancel(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BadRequestException("Order not found with id: " + orderId));

        // Nếu đơn đã hoàn thành thì không được phép hủy nữa (đề phòng)
        if (order.getStatus() == OrderStatus.COMPLETED) {
            return;
        }

        // 1. Đặt trạng thái là FAILED
        order.setStatus(OrderStatus.FAILED);

        for (OrderItem item : order.getOrderItems()) {
            List<Storage> storages = stockRepository.findByOrderItem_ItemId(item.getItemId());
            for (Storage storage : storages) {
                storage.setStatus(CardStatus.UNUSED); // Trả về trạng thái chưa dùng
                storage.setOrderItem(null);           // Gỡ liên kết
            }
            stockRepository.saveAll(storages);
        }

        orderRepository.save(order);
    }

    public OrderDetailResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BadRequestException("Order not found with id: " + orderId));

        OrderDetailResponse response = new OrderDetailResponse();
        response.setOrderId(order.getOrderId());
        response.setPayment(order.getPayment());
        response.setOrderStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());
        response.setOrderDate(order.getCreatedAt());

        if (order.getUser() != null) {
            response.setUserId(order.getUser().getUserId());
            response.setUsername(order.getUser().getUsername());
        }

        if (order.getTransaction() != null) {
            // Giả sử Transaction entity có getId() hoặc getTransactionCode()
            response.setTransactionCode(order.getTransaction().getTransactionId());
        }

        // 5. Xử lý danh sách sản phẩm và MÃ THẺ ĐI KÈM
        List<PurchasedItemResponse> itemResponses = new ArrayList<>();

        for (OrderItem item : order.getOrderItems()) {
            PurchasedItemResponse itemDto = new PurchasedItemResponse();

            // Map thông tin cơ bản sản phẩm
            itemDto.setProductName(item.getProduct().getName());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setPricePerUnit(item.getPrice());

            List<Storage> soldCards = stockRepository.findByOrderItem_ItemId(item.getItemId());

            List<CardInfo> cardInfos = new ArrayList<>();
            for (Storage storage : soldCards) {
                CardInfo cardInfo = new CardInfo();
                cardInfo.setCode(storage.getActivateCode());
                cardInfo.setExpirationDate(storage.getExpirationDate());
                cardInfos.add(cardInfo);
            }

            itemDto.setCards(cardInfos); // Gán list thẻ vào item
            itemResponses.add(itemDto);
        }

        response.setPurchasedItems(itemResponses);

        return response;
    }
    // =========================================================================
    // HELPER: Hàm chung để xử lý logic tìm thẻ, lock thẻ và tạo OrderItem
    // =========================================================================
    private long processOrderItems(List<OrderItemRequest> itemRequests, Order order) {
        long total = 0L;
        List<OrderItem> newItems = new ArrayList<>();
        for (OrderItemRequest itemReq : itemRequests) {
            ProductVariant variant = productVariantsRepository.findById(itemReq.getVariantId())
                    .orElseThrow(() -> new BadRequestException("Variant not found"));
            List<Storage> storagesToSell = stockRepository.findAndLockCards(
                    CardStatus.UNUSED,
                    variant.getVariantId(),
                    PageRequest.of(0, itemReq.getQuantity())
            );
            if (storagesToSell.size() < itemReq.getQuantity()) {
                throw new BadRequestException("Not enough stock for variant: " + variant.getProduct().getName());
            }
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(variant.getProduct());
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setPrice(variant.getPrice());
            newItems.add(orderItem);
            total += variant.getPrice() * itemReq.getQuantity();
            for (Storage storage : storagesToSell) {
                storage.setStatus(CardStatus.PENDING_PAYMENT);
                storage.setOrderItem(orderItem);
                stockRepository.save(storage);
            }
        }
        if (order.getOrderItems() == null) order.setOrderItems(newItems);
        else order.getOrderItems().addAll(newItems);
        return total;
    }
    // =========================================================================
    // 3. LẤY LỊCH SỬ ĐƠN HÀNG (Refactor trả về Page)
    // =========================================================================
    public Page<OrderHistoryResponse> getOrderHistoryForCurrentUser(Pageable pageable) {
        User user = authenticationService.getCurrentUser();
        Page<Order> orders = orderRepository.findByUser_UserIdOrderByCreatedAtDesc(user.getUserId(), pageable);

        return orders.map(this::mapToOrderHistoryResponse);
    }

    /**
     * Lấy tất cả đơn hàng (dành cho admin).
     * @param pageable
     * @return Page<Order>
     */
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }
    public void handlePaymentSuccess(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BadRequestException("Order not found with id: " + orderId));

        if(order.getStatus() == OrderStatus.COMPLETED) return;

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


    /**
     * Cập nhật trạng thái của một đơn hàng (dành cho admin).
     * @param orderId ID của đơn hàng
     * @param newStatus Trạng thái mới
     * @return Order đã được cập nhật
     */
    public OrderDetailResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BadRequestException("Order not found with id: " + orderId));
        order.setStatus(newStatus);
        orderRepository.save(order);
        OrderDetailResponse response = new OrderDetailResponse();
        response.setOrderId(order.getOrderId());
        response.setOrderStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());
        return response;
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


    public Page<OrderHistoryResponse> searchOrders(LocalDateTime from, LocalDateTime to, String username, OrderStatus status, Pageable pageable) {
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
        Page<Order> orderEntities = orderRepository.findAll(spec, pageable);

        return orderEntities.map(this::mapToOrderHistoryResponse);
    }

    private OrderHistoryResponse mapToOrderHistoryResponse(Order order) {
        OrderHistoryResponse dto = new OrderHistoryResponse();

        dto.setOrderId(order.getOrderId());
        dto.setOrderStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderDate(order.getCreatedAt());

        return dto;
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
    public OrderDetailResponse getOrderDetailForCurrentUser(Long orderId) {
        // 1. Lấy User đang đăng nhập
        User user = authenticationService.getCurrentUser();

        // 2. Tìm đơn hàng nhưng BẮT BUỘC phải khớp userId (Chặn xem trộm đơn người khác)
        Order order = orderRepository.findByOrderIdAndUser_UserId(orderId, user.getUserId())
                .orElseThrow(() -> new BadRequestException("Không tìm thấy đơn hàng hoặc bạn không có quyền truy cập"));

        // 3. Map thông tin chung
        OrderDetailResponse response = new OrderDetailResponse();
        response.setOrderId(order.getOrderId());
        response.setPayment(order.getPayment());
        response.setOrderStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());
        response.setOrderDate(order.getCreatedAt());

        // 4. Map sản phẩm và Mã thẻ
        List<PurchasedItemResponse> itemResponses = new ArrayList<>();

        for (OrderItem item : order.getOrderItems()) {
            PurchasedItemResponse itemDto = new PurchasedItemResponse();
            itemDto.setProductName(item.getProduct().getName());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setPricePerUnit(item.getPrice());

            // --- LOGIC QUAN TRỌNG: Chỉ hiện mã thẻ khi ĐÃ THANH TOÁN ---
            if (order.getStatus() == OrderStatus.COMPLETED) {
                List<Storage> soldCards = stockRepository.findByOrderItem_ItemId(item.getItemId());
                List<CardInfo> cardInfos = new ArrayList<>();
                for (Storage storage : soldCards) {
                    CardInfo cardInfo = new CardInfo();
                    cardInfo.setCode(storage.getActivateCode());
                    cardInfo.setExpirationDate(storage.getExpirationDate());
                    cardInfos.add(cardInfo);
                }
                itemDto.setCards(cardInfos);
            } else {
                // Nếu chưa thanh toán, trả về list rỗng hoặc null để frontend không hiện gì
                itemDto.setCards(new ArrayList<>());
            }
            // ------------------------------------------------------------

            itemResponses.add(itemDto);
        }

        response.setPurchasedItems(itemResponses);
        return response;
    }
}
