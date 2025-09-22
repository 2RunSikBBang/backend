package com.project.sikbbang.app.domain.order.service;

import com.project.sikbbang.app.domain.admin.model.Admin;
import com.project.sikbbang.app.domain.admin.model.AdminRole;
import com.project.sikbbang.app.domain.admin.repository.AdminRepository;
import com.project.sikbbang.app.domain.menu.dto.MenuDto;
import com.project.sikbbang.app.domain.menu.model.Menu;
import com.project.sikbbang.app.domain.menu.repository.MenuRepository;
import com.project.sikbbang.app.domain.order.dto.OrderDto;
import com.project.sikbbang.app.domain.order.dto.OrderRequestDto;
import com.project.sikbbang.app.domain.order.dto.OrderUpdateRequestDto;
import com.project.sikbbang.app.domain.order.model.Order;
import com.project.sikbbang.app.domain.order.model.OrderItem;
import com.project.sikbbang.app.domain.order.model.OrderStatus;
import com.project.sikbbang.app.domain.order.repository.OrderRepository;
import com.project.sikbbang.app.domain.store.dto.StoreStatusDto;
import com.project.sikbbang.app.domain.store.dto.StoreInfoDto;
import com.project.sikbbang.app.domain.store.model.Store;
import com.project.sikbbang.app.domain.store.model.StoreInspection;
import com.project.sikbbang.app.domain.store.model.StoreStatus;
import com.project.sikbbang.app.domain.store.repository.StoreRepository;
import com.project.sikbbang.app.global.code.dto.ApiResponse;
import com.project.sikbbang.app.global.code.status.ErrorStatus;
import com.project.sikbbang.app.global.code.status.SuccessStatus;
import com.project.sikbbang.app.global.exception.GeneralException;
import com.project.sikbbang.app.global.util.SecurityUtil;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;
    private final AdminRepository adminRepository;

    @Transactional
    public ApiResponse<?> createOrder(Long storeId, OrderRequestDto request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));

        if (store.getInspection() != StoreInspection.APPROVED) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }
        if (store.getStatus() == StoreStatus.UNAVAILABLE || store.getStatus() == StoreStatus.NOT_READY) {
            throw new GeneralException(ErrorStatus.STORE_NOT_ACCEPTING_ORDERS);
        }

        List<OrderItem> orderItems = request.getOrderItems().stream().map(
                orderItemDto -> {
                    Menu menu = menuRepository.findById(orderItemDto.getMenuId())
                            .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));

                    if (!menu.getStore().getId().equals(storeId)) {
                        throw new GeneralException(ErrorStatus.BAD_REQUEST);
                    }

                    return OrderItem.builder()
                            .menu(menu)
                            .quantity(orderItemDto.getQuantity())
                            .price(menu.getPrice() * orderItemDto.getQuantity())
                            .build();
                }
        ).collect(Collectors.toList());

        Integer totalPrice = orderItems.stream().mapToInt(OrderItem::getPrice).sum();

        Order order = Order.builder()
                .store(store)
                .phoneNumber(request.getPhoneNumber())
                .customerName(request.getCustomerName())
                .deliveryAddress(request.getDeliveryAddress())
                .status(OrderStatus.PENDING)
                .totalPrice(totalPrice)
                .orderDate(LocalDateTime.now())
                .orderItems(orderItems)
                .build();

        orderItems.forEach(orderItem -> orderItem.setOrder(order));

        Order saved = orderRepository.save(order);

        return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(), saved.getId());
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<OrderDto>> getOrdersByStore(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));

        verifyStoreOwnership(storeId);

        List<Order> orders = orderRepository.findByStoreOrderByOrderDateDesc(store);
        List<OrderDto> orderDtos = orders.stream()
                .map(OrderDto::fromEntity)
                .collect(Collectors.toList());
        return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(), orderDtos);
    }

    @Transactional
    public ApiResponse<?> updateOrderStatus(Long storeId, Long orderId, OrderUpdateRequestDto request) {
        verifyStoreOwnership(storeId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));

        if (!order.getStore().getId().equals(storeId)) {
            throw new GeneralException(ErrorStatus.BAD_REQUEST);
        }

        order.updateStatus(request.getStatus());
        orderRepository.save(order);

        return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(), "Order status updated to " + request.getStatus().name());
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<OrderDto>> getOrdersByPhoneNumber(String phoneNumber) {
        List<Order> orders = orderRepository.findByPhoneNumber(phoneNumber);
        if (orders.isEmpty()) {
            throw new GeneralException(ErrorStatus.NOT_FOUND);
        }
        List<OrderDto> orderDtos = orders.stream()
                .map(OrderDto::fromEntity)
                .collect(Collectors.toList());
        return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(), orderDtos);
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<OrderDto>> getOrdersByPhoneNumberAndStore(Long storeId, String phoneNumber) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));

        verifyStoreOwnership(storeId);

        List<Order> orders = orderRepository.findByStoreAndPhoneNumberOrderByOrderDateDesc(store, phoneNumber);
        if (orders.isEmpty()) {
            throw new GeneralException(ErrorStatus.NOT_FOUND);
        }
        List<OrderDto> orderDtos = orders.stream()
                .map(OrderDto::fromEntity)
                .collect(Collectors.toList());
        return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(), orderDtos);
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<MenuDto>> getMenusByStore(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));
        List<Menu> menus = menuRepository.findByStore(store);
        List<MenuDto> menuDtos = menus.stream()
                .map(MenuDto::fromEntity)
                .collect(Collectors.toList());
        return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(), menuDtos);
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<StoreStatusDto>> getAllStoreStatuses() {
        List<Store> allStores = storeRepository.findAll();
        List<StoreStatusDto> statusList = allStores.stream()
                .map(store -> StoreStatusDto.builder()
                        .storeId(store.getId())
                        .status(store.getStatus())
                        .message(store.getStatus().getMessage())
                        .build())
                .collect(Collectors.toList());
        return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(), statusList);
    }

    @Transactional(readOnly = true)
    public ApiResponse<StoreInfoDto> getStoreInfo(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));
        return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(),
                StoreInfoDto.fromEntity(store));
    }

    private void verifyStoreOwnership(Long storeId) {
        String adminIdStr = SecurityUtil.getCurrentAdminId().toString();
        String currentRole = SecurityUtil.getCurrentAdminRole();

        if (currentRole.equals(AdminRole.SUPER_ADMIN.name())) {
            return;
        }

        if (currentRole.equals(AdminRole.STORE_ADMIN.name())) {
            Admin admin = adminRepository.findById(Long.valueOf(adminIdStr))
                    .orElseThrow(() -> new GeneralException(ErrorStatus.UNAUTHORIZED));
            if (admin.getStore() == null || !admin.getStore().getId().equals(storeId)) {
                throw new GeneralException(ErrorStatus.FORBIDDEN);
            }
        }

        if (currentRole.equals(AdminRole.BOOTH_OPERATOR.name())) {
            Long authenticatedStoreId = Long.valueOf(adminIdStr);
            if (!authenticatedStoreId.equals(storeId)) {
                throw new GeneralException(ErrorStatus.FORBIDDEN);
            }
        }
    }

    @Transactional
    public ApiResponse<?> deleteOrder(Long storeId, Long orderId) {
        Order order = orderRepository.findByIdAndStoreId(orderId, storeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));

        orderRepository.delete(order);

        return ApiResponse.onSuccess(
                SuccessStatus.OK.getCode(),
                SuccessStatus.OK.getMessage(),
                Map.of("orderId", orderId, "status", "deleted")
        );
    }
}
