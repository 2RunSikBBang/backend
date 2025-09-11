package com.project.sikbbang.app.domain.admin.controller;

import com.project.sikbbang.app.domain.admin.dto.AdminLoginRequestDto;
import com.project.sikbbang.app.domain.admin.dto.AdminRegisterRequestDto;
import com.project.sikbbang.app.domain.admin.service.AdminAuthService;
import com.project.sikbbang.app.domain.store.dto.StoreRegisterRequestDto;
import com.project.sikbbang.app.domain.store.service.StoreService;
import com.project.sikbbang.app.global.code.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "3. Admin API", description = "일반 어드민 권한 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminAuthService adminAuthService;
    private final StoreService storeService;

    @Operation(summary = "어드민 가입", description = "새로운 어드민 계정을 등록합니다. 계정은 'NOT_ACTIVATE_ADMIN' 상태로 생성됩니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": true, \"code\": \"COMMON200\", \"message\": \"성공입니다.\", \"result\": 1}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "중복된 사용자 이름",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"DUPLICATE409\", \"message\": \"이미 존재하는 리소스입니다.\", \"result\": null}")))
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@RequestBody AdminRegisterRequestDto request) {
        return ResponseEntity.ok(adminAuthService.register(request));
    }

    @Operation(summary = "어드민 로그인", description = "가입된 어드민 계정으로 로그인합니다. 'NOT_ACTIVATE_ADMIN' 계정은 로그인이 거부됩니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": true, \"code\": \"COMMON200\", \"message\": \"성공입니다.\", \"result\": \"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZSI6IlNUP...\"}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "로그인 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"UNAUTHORIZED401\", \"message\": \"인증이 필요합니다.\", \"result\": null}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음 (비활성화 계정)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"FORBIDDEN403\", \"message\": \"권한이 없습니다.\", \"result\": null}")))
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody AdminLoginRequestDto request) {
        return ResponseEntity.ok(adminAuthService.login(request));
    }

    @Operation(summary = "가게 생성 요청", description = "어드민 계정이 새로운 가게를 생성하도록 요청합니다. 요청 시 자동으로 해당 어드민에게 가게가 할당됩니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": true, \"code\": \"COMMON200\", \"message\": \"성공입니다.\", \"result\": 1}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "이미 가게에 소속된 어드민",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"FORBIDDEN403\", \"message\": \"권한이 없습니다.\", \"result\": null}")))
    })
    @PostMapping("/request-store-creation")
    public ResponseEntity<ApiResponse<?>> requestStoreCreation(@RequestBody StoreRegisterRequestDto request) {
        return ResponseEntity.ok(storeService.requestStoreCreation(request));
    }
}
