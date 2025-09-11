package com.project.sikbbang.app.domain.admin.service;

import com.project.sikbbang.app.domain.admin.dto.AdminLoginRequestDto;
import com.project.sikbbang.app.domain.admin.dto.AdminRegisterRequestDto;
import com.project.sikbbang.app.domain.admin.model.Admin;
import com.project.sikbbang.app.domain.admin.model.AdminRole;
import com.project.sikbbang.app.domain.admin.repository.AdminRepository;
import com.project.sikbbang.app.global.code.dto.ApiResponse;
import com.project.sikbbang.app.global.code.status.ErrorStatus;
import com.project.sikbbang.app.global.code.status.SuccessStatus;
import com.project.sikbbang.app.global.exception.GeneralException;
import com.project.sikbbang.app.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public ApiResponse<?> register(AdminRegisterRequestDto request) {
        if (adminRepository.existsByUsername(request.getUsername())) {
            throw new GeneralException(ErrorStatus.DUPLICATE_RESOURCE);
        }
        Admin admin = Admin.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(AdminRole.NOT_ACTIVATE_ADMIN)
                .build();
        Admin saved = adminRepository.save(admin);
        return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(), saved.getId());
    }

    @Transactional(readOnly = true)
    public ApiResponse<?> login(AdminLoginRequestDto request) {
        Admin admin = adminRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new GeneralException(ErrorStatus.UNAUTHORIZED));
        if (admin.getRole() == AdminRole.NOT_ACTIVATE_ADMIN) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new GeneralException(ErrorStatus.INVALID_PASSWORD);
        }
        String token = jwtTokenProvider.createToken(String.valueOf(admin.getId()), admin.getRole().name());
        return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(), token);
    }
}
