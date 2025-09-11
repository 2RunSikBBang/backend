package com.project.sikbbang.app.domain.admin.controller;

import com.project.sikbbang.app.domain.admin.dto.AdminActivationResponseDto;
import com.project.sikbbang.app.domain.admin.dto.AdminDto;
import com.project.sikbbang.app.domain.admin.dto.SuperAdminLoginRequestDto;
import com.project.sikbbang.app.domain.admin.dto.SuperAdminRegisterRequestDto;
import com.project.sikbbang.app.domain.admin.service.SuperAdminService;
import com.project.sikbbang.app.domain.store.dto.StoreApprovalResponseDto;
import com.project.sikbbang.app.domain.store.dto.StoreManagementDto;
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
import java.util.List;

@Tag(name = "4.Super Admin API", description = "슈퍼 어드민 권한 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/super-admin")
public class SuperAdminController {

    private final SuperAdminService superAdminService;

    @Operation(summary = "슈퍼 어드민 가입", description = "새로운 슈퍼 어드민 계정을 등록합니다.")
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
    public ResponseEntity<ApiResponse<?>> register(@RequestBody SuperAdminRegisterRequestDto request) {
        return ResponseEntity.ok(superAdminService.register(request));
    }

    @Operation(summary = "슈퍼 어드민 로그인", description = "아이디와 비밀번호로 슈퍼 어드민 로그인을 수행합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": true, \"code\": \"COMMON200\", \"message\": \"성공입니다.\", \"result\": \"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZSI6IlNUP...\"}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "로그인 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"UNAUTHORIZED401\", \"message\": \"인증이 필요합니다.\", \"result\": null}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "슈퍼 어드민 계정이 아님",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"FORBIDDEN403\", \"message\": \"권한이 없습니다.\", \"result\": null}")))
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody SuperAdminLoginRequestDto request) {
        return ResponseEntity.ok(superAdminService.login(request));
    }

    @Operation(summary = "보류 중인 가게 목록 조회", description = "어드민이 생성 요청한, 아직 승인되지 않은 가게 목록을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": true, \"code\": \"COMMON200\", \"message\": \"성공입니다.\", \"result\": [{\"storeId\": 1,\"storeName\": \"가게1\", \"inspection\": \"PENDING\", \"adminId\": 1, \"adminUsername\": \"admin1\"}]}")))
    })
    @GetMapping("/pending-stores")
    public ResponseEntity<ApiResponse<List<StoreManagementDto>>> getPendingStoreCreations() {
        return ResponseEntity.ok(superAdminService.getPendingStoreCreations());
    }

    @Operation(summary = "가게 생성 요청 승인", description = "보류 중인 특정 가게 생성 요청을 승인합니다. 해당 어드민의 권한이 'STORE_ADMIN'으로 변경됩니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": true, \"code\": \"COMMON200\", \"message\": \"성공입니다.\", \"result\": {\"status\": \"approved\", \"storeId\": 1}}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"NOTFOUND404\", \"message\": \"요청하신 리소스를 찾을 수 없습니다.\", \"result\": null}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"BAD_REQUEST400\", \"message\": \"잘못된 요청입니다.\", \"result\": null}")))
    })
    @PostMapping("/store-creation/approve/{storeId}")
    public ResponseEntity<ApiResponse<StoreApprovalResponseDto>> approveStoreCreation(@PathVariable Long storeId) {
        return ResponseEntity.ok(superAdminService.approveStoreCreation(storeId));
    }

    @Operation(summary = "가게 생성 요청 거절", description = "보류 중인 특정 가게 생성 요청을 거절합니다. 해당 어드민의 소속 정보가 초기화됩니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": true, \"code\": \"COMMON200\", \"message\": \"성공입니다.\", \"result\": {\"status\": \"rejected\", \"storeId\": 1}}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"NOTFOUND404\", \"message\": \"요청하신 리소스를 찾을 수 없습니다.\", \"result\": null}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"BAD_REQUEST400\", \"message\": \"잘못된 요청입니다.\", \"result\": null}")))
    })
    @PostMapping("/store-creation/reject/{storeId}")
    public ResponseEntity<ApiResponse<StoreApprovalResponseDto>> rejectStoreCreation(@PathVariable Long storeId) {
        return ResponseEntity.ok(superAdminService.rejectStoreCreation(storeId));
    }

    @Operation(summary = "전체 어드민 목록 조회", description = "모든 어드민(슈퍼 어드민 포함)의 목록을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": true, \"code\": \"COMMON200\", \"message\": \"성공입니다.\", \"result\": [{\"id\": 1,\"username\": \"super_admin\", \"role\": \"SUPER_ADMIN\", \"storeId\": null}, {\"id\": 2,\"username\": \"admin1\", \"role\": \"NOT_ACTIVATE_ADMIN\", \"storeId\": 1}]}")))
    })
    @GetMapping("/admins")
    public ResponseEntity<ApiResponse<List<AdminDto>>> getAdmins() {
        return ResponseEntity.ok(superAdminService.getAdmins());
    }

    @Operation(summary = "어드민 계정 활성화", description = "가게 소속이 없는 'NOT_ACTIVATE_ADMIN' 상태의 어드민을 'STORE_ADMIN'으로 활성화합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": true, \"code\": \"COMMON200\", \"message\": \"성공입니다.\", \"result\": {\"status\": \"activated\", \"adminId\": 2}}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "어드민을 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"NOTFOUND404\", \"message\": \"요청하신 리소스를 찾을 수 없습니다.\", \"result\": null}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"BAD_REQUEST400\", \"message\": \"잘못된 요청입니다.\", \"result\": null}")))
    })
    @PostMapping("/activate/{adminId}")
    public ResponseEntity<ApiResponse<AdminActivationResponseDto>> activateAdmin(@PathVariable Long adminId) {
        return ResponseEntity.ok(superAdminService.activateAdmin(adminId));
    }
}
