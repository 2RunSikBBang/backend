package com.project.sikbbang.app.domain.admin.service;

import com.project.sikbbang.app.domain.admin.dto.AdminActivationResponseDto;
import com.project.sikbbang.app.domain.admin.dto.AdminDto;
import com.project.sikbbang.app.domain.admin.dto.SuperAdminLoginRequestDto;
import com.project.sikbbang.app.domain.admin.dto.SuperAdminRegisterRequestDto;
import com.project.sikbbang.app.domain.admin.model.Admin;
import com.project.sikbbang.app.domain.admin.model.AdminRole;
import com.project.sikbbang.app.domain.admin.repository.AdminRepository;
import com.project.sikbbang.app.domain.store.dto.StoreApprovalResponseDto;
import com.project.sikbbang.app.domain.store.dto.StoreManagementDto;
import com.project.sikbbang.app.domain.store.model.Store;
import com.project.sikbbang.app.domain.store.model.StoreInspection;
import com.project.sikbbang.app.domain.store.repository.StoreRepository;
import com.project.sikbbang.app.global.code.dto.ApiResponse;
import com.project.sikbbang.app.global.code.status.ErrorStatus;
import com.project.sikbbang.app.global.code.status.SuccessStatus;
import com.project.sikbbang.app.global.exception.GeneralException;
import com.project.sikbbang.app.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SuperAdminService {

    private final AdminRepository adminRepository;
    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public ApiResponse<?> register(SuperAdminRegisterRequestDto request) {
        if (adminRepository.existsByUsername(request.getUsername())) {
            throw new GeneralException(ErrorStatus.DUPLICATE_RESOURCE);
        }
        Admin admin = Admin.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(AdminRole.SUPER_ADMIN)
                .build();
        Admin saved = adminRepository.save(admin);
        return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(), saved.getId());
    }

    @Transactional(readOnly = true)
    public ApiResponse<?> login(SuperAdminLoginRequestDto request) {
        Admin admin = adminRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new GeneralException(ErrorStatus.UNAUTHORIZED));
        if (admin.getRole() != AdminRole.SUPER_ADMIN) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new GeneralException(ErrorStatus.INVALID_PASSWORD);
        }
        String token = jwtTokenProvider.createToken(String.valueOf(admin.getId()), AdminRole.SUPER_ADMIN.name());
        return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(), token);
    }

    @Transactional
    public ApiResponse<StoreApprovalResponseDto> approveStoreCreation(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));
        if (store.getInspection() != StoreInspection.PENDING) {
            throw new GeneralException(ErrorStatus.BAD_REQUEST);
        }
        store.approve();
        storeRepository.save(store);

        Admin admin = adminRepository.findByStore(store)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));
        admin.setRole(AdminRole.STORE_ADMIN);
        adminRepository.save(admin);

        return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(),
                StoreApprovalResponseDto.builder()
                        .status("approved")
                        .storeId(storeId)
                        .build());
    }

    @Transactional
    public ApiResponse<StoreApprovalResponseDto> rejectStoreCreation(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));
        if (store.getInspection() != StoreInspection.PENDING) {
            throw new GeneralException(ErrorStatus.BAD_REQUEST);
        }
        store.reject();
        storeRepository.save(store);

        Admin admin = adminRepository.findByStore(store)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));
        admin.setStore(null);
        adminRepository.save(admin);

        return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(),
                StoreApprovalResponseDto.builder()
                        .status("rejected")
                        .storeId(storeId)
                        .build());
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<StoreManagementDto>> getPendingStoreCreations() {
        List<Store> stores = storeRepository.findByInspection(StoreInspection.PENDING);
        List<StoreManagementDto> dtos = stores.stream()
                .map(store -> StoreManagementDto.builder()
                        .storeId(store.getId())
                        .storeName(store.getName())
                        .inspection(store.getInspection())
                        .adminId(store.getAdmin() != null ? store.getAdmin().getId() : null)
                        .adminUsername(store.getAdmin() != null ? store.getAdmin().getUsername() : null)
                        .build())
                .collect(Collectors.toList());
        return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(), dtos);
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<AdminDto>> getAdmins() {
        List<Admin> admins = adminRepository.findAll();
        List<AdminDto> adminDtos = admins.stream()
                .map(admin -> AdminDto.builder()
                        .id(admin.getId())
                        .username(admin.getUsername())
                        .role(admin.getRole().name())
                        .storeId(admin.getStore() != null ? admin.getStore().getId() : null)
                        .build())
                .collect(Collectors.toList());
        return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(), adminDtos);
    }

    @Transactional
    public ApiResponse<AdminActivationResponseDto> activateAdmin(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));
        if (admin.getRole() != AdminRole.NOT_ACTIVATE_ADMIN) {
            throw new GeneralException(ErrorStatus.BAD_REQUEST);
        }
        admin.setRole(AdminRole.STORE_ADMIN);
        adminRepository.save(admin);
        return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(),
                AdminActivationResponseDto.builder()
                        .status("activated")
                        .adminId(adminId)
                        .build());
    }
}
