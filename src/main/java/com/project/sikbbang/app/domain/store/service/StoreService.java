package com.project.sikbbang.app.domain.store.service;

import com.project.sikbbang.app.domain.admin.model.Admin;
import com.project.sikbbang.app.domain.admin.model.AdminRole;
import com.project.sikbbang.app.domain.admin.repository.AdminRepository;
import com.project.sikbbang.app.domain.menu.dto.MenuDto;
import com.project.sikbbang.app.domain.menu.model.Menu;
import com.project.sikbbang.app.domain.menu.repository.MenuRepository;
import com.project.sikbbang.app.domain.order.dto.OrderDto;
import com.project.sikbbang.app.domain.order.dto.OrderUpdateRequestDto;
import com.project.sikbbang.app.domain.order.model.Order;
import com.project.sikbbang.app.domain.order.repository.OrderRepository;
import com.project.sikbbang.app.domain.store.dto.MenuCreateRequestDto;
import com.project.sikbbang.app.domain.store.dto.MenuDeletionResponseDto;
import com.project.sikbbang.app.domain.store.dto.MenuUpdateRequestDto;
import com.project.sikbbang.app.domain.store.dto.StoreInfoUpdateRequestDto;
import com.project.sikbbang.app.domain.store.dto.StoreLoginRequestDto;
import com.project.sikbbang.app.domain.store.dto.StoreRegisterRequestDto;
import com.project.sikbbang.app.domain.store.dto.StoreStatusUpdateRequestDto;
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
import com.project.sikbbang.app.global.security.JwtTokenProvider;
import com.project.sikbbang.app.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;
    private final AdminRepository adminRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public ApiResponse<?> requestStoreCreation(StoreRegisterRequestDto request) {
        Long adminId = SecurityUtil.getCurrentAdminId();
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.UNAUTHORIZED));

        if (admin.getStore() != null) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }

        Store store = Store.builder()
                .name(request.getStoreName())
                .password(passwordEncoder.encode(request.getPassword()))
                .profileImageUrl(request.getProfileImageUrl())
                .status(StoreStatus.NOT_READY)
                .inspection(StoreInspection.PENDING)
                .deleted(false)
                .build();
        Store saved = storeRepository.save(store);
        admin.setStore(saved);
        admin.setRole(AdminRole.NOT_ACTIVATE_ADMIN);
        adminRepository.save(admin);
        return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(), saved.getId());
    }

    @Transactional(readOnly = true)
    public ApiResponse<?> loginStore(StoreLoginRequestDto request) {
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));

        if (store.getInspection() != StoreInspection.APPROVED) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }

        if (!store.checkPassword(request.getPassword(), passwordEncoder)) {
            throw new GeneralException(ErrorStatus.INVALID_PASSWORD);
        }

        String token = jwtTokenProvider.createToken(String.valueOf(store.getId()), AdminRole.BOOTH_OPERATOR.name());

        return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(), token);
    }

    @Transactional
    public ApiResponse<?> addMenus(Long storeId, List<MenuCreateRequestDto> requests) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));
        verifyStoreOwnership(storeId);

        List<Menu> menus = requests.stream()
                .map(request -> Menu.builder()
                        .name(request.getName())
                        .price(request.getPrice())
                        .minOrderQuantity(request.getMinOrderQuantity())
                        .maxOrderQuantity(request.getMaxOrderQuantity())
                        .isRepresentative(request.isRepresentative())
                        .store(store)
                        .build())
                .collect(Collectors.toList());

        List<Menu> savedMenus = menuRepository.saveAll(menus);
        return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(), savedMenus.size() + "개의 메뉴가 등록되었습니다.");
    }

    @Transactional
    public ApiResponse<?> updateMenuPrice(Long storeId, Long menuId, MenuUpdateRequestDto request) {
        verifyStoreOwnership(storeId);
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));
        if (!menu.getStore().getId().equals(storeId)) {
            throw new GeneralException(ErrorStatus.BAD_REQUEST);
        }
        menu.updatePrice(request.getPrice());
        menuRepository.save(menu);
        return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(), menu.getId());
    }

    @Transactional
    public ApiResponse<MenuDeletionResponseDto> deleteMenu(Long storeId, Long menuId) {
        verifyStoreOwnership(storeId);
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));
        if (!menu.getStore().getId().equals(storeId)) {
            throw new GeneralException(ErrorStatus.BAD_REQUEST);
        }
        menu.setDeleted(true);
        menuRepository.save(menu);
        return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(),
                MenuDeletionResponseDto.builder()
                        .message("Menu deleted successfully.")
                        .menuId(menuId)
                        .build());
    }

    @Transactional
    public ApiResponse<?> updateStoreStatus(Long storeId, StoreStatusUpdateRequestDto request) {
        verifyStoreOwnership(storeId);
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));
        store.updateStatus(request.getStatus());
        storeRepository.save(store);
        return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(), "Store status updated.");
    }

    @Transactional
    public ApiResponse<?> updateStoreInfo(Long storeId, StoreInfoUpdateRequestDto request) {
        verifyStoreOwnership(storeId);
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));

        store.updateInfo(
                request.getProductImageUrls(),
                request.getRefundPolicy(),
                request.getBankAccount(),
                request.getCancelPhoneNumber()
        );
        storeRepository.save(store);
        return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(), "Store information updated.");
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
    public ApiResponse<List<MenuDto>> getMenusByStore(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));
        List<Menu> menus = menuRepository.findByStoreAndDeletedFalse(store);
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
}
