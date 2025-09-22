package com.project.sikbbang.app.domain.store.controller;

import com.project.sikbbang.app.domain.order.dto.OrderDto;
import com.project.sikbbang.app.domain.order.dto.OrderUpdateRequestDto;
import com.project.sikbbang.app.domain.order.service.OrderService;
import com.project.sikbbang.app.domain.store.dto.MenuCreateRequestDto;
import com.project.sikbbang.app.domain.store.dto.MenuDeletionResponseDto;
import com.project.sikbbang.app.domain.store.dto.MenuUpdateRequestDto;
import com.project.sikbbang.app.domain.store.dto.StoreInfoUpdateRequestDto;
import com.project.sikbbang.app.domain.store.dto.StoreLoginRequestDto;
import com.project.sikbbang.app.domain.store.dto.StoreInfoDto;
import com.project.sikbbang.app.domain.store.dto.StoreStatusUpdateRequestDto;
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

import java.util.List;

@Tag(name = "2.Store & Booth Operator API", description = "가게 및 부스 운영자 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;
    private final OrderService orderService;

    @Operation(summary = "부스 운영자 로그인", description = "가게 ID와 비밀번호를 사용하여 부스 운영자로 로그인합니다. 승인된 가게만 로그인이 가능합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": true, \"code\": \"COMMON200\", \"message\": \"성공입니다.\", \"result\": \"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZSI6IkJP...\"}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"NOTFOUND404\", \"message\": \"요청하신 리소스를 찾을 수 없습니다.\", \"result\": null}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "승인되지 않은 가게",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"FORBIDDEN403\", \"message\": \"권한이 없습니다.\", \"result\": null}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 비밀번호",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"INVALID400\", \"message\": \"비밀번호가 올바르지 않습니다.\", \"result\": null}")))
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> loginStore(@RequestBody StoreLoginRequestDto request) {
        return ResponseEntity.ok(storeService.loginStore(request));
    }

    @Operation(summary = "메뉴 추가", description = "특정 가게에 메뉴를 추가합니다. 단일 또는 여러 개의 메뉴를 리스트로 한 번에 추가할 수 있습니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": true, \"code\": \"COMMON200\", \"message\": \"성공입니다.\", \"result\": \"2개의 메뉴가 등록되었습니다.\"}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"NOTFOUND404\", \"message\": \"요청하신 리소스를 찾을 수 없습니다.\", \"result\": null}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"FORBIDDEN403\", \"message\": \"권한이 없습니다.\", \"result\": null}")))
    })
    @PostMapping("/{storeId}/menus")
    public ResponseEntity<ApiResponse<?>> addMenus(@PathVariable Long storeId, @RequestBody List<MenuCreateRequestDto> requests) {
        return ResponseEntity.ok(storeService.addMenus(storeId, requests));
    }

    @Operation(summary = "메뉴 가격 수정", description = "특정 가게의 특정 메뉴 가격을 수정합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": true, \"code\": \"COMMON200\", \"message\": \"성공입니다.\", \"result\": 1}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "메뉴 또는 가게를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"NOTFOUND404\", \"message\": \"요청하신 리소스를 찾을 수 없습니다.\", \"result\": null}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"FORBIDDEN403\", \"message\": \"권한이 없습니다.\", \"result\": null}")))
    })
    @PutMapping("/{storeId}/menus/{menuId}")
    public ResponseEntity<ApiResponse<?>> updateMenuPrice(@PathVariable Long storeId, @PathVariable Long menuId, @RequestBody MenuUpdateRequestDto request) {
        return ResponseEntity.ok(storeService.updateMenuPrice(storeId, menuId, request));
    }

    @Operation(summary = "메뉴 삭제", description = "특정 가게의 특정 메뉴를 삭제합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": true, \"code\": \"COMMON200\", \"message\": \"성공입니다.\", \"result\": {\"message\": \"Menu deleted successfully.\", \"menuId\": 1}}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "메뉴 또는 가게를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"NOTFOUND404\", \"message\": \"요청하신 리소스를 찾을 수 없습니다.\", \"result\": null}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"FORBIDDEN403\", \"message\": \"권한이 없습니다.\", \"result\": null}")))
    })
    @DeleteMapping("/{storeId}/menus/{menuId}")
    public ResponseEntity<ApiResponse<MenuDeletionResponseDto>> deleteMenu(@PathVariable Long storeId, @PathVariable Long menuId) {
        return ResponseEntity.ok(storeService.deleteMenu(storeId, menuId));
    }

    @Operation(summary = "가게 상태 변경", description = "특정 가게의 영업 상태(OPEN, DELAYED 등)를 변경합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": true, \"code\": \"COMMON200\", \"message\": \"성공입니다.\", \"result\": \"Store status updated.\"}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"NOTFOUND404\", \"message\": \"요청하신 리소스를 찾을 수 없습니다.\", \"result\": null}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"FORBIDDEN403\", \"message\": \"권한이 없습니다.\", \"result\": null}")))
    })
    @PutMapping("/{storeId}/status")
    public ResponseEntity<ApiResponse<?>> updateStoreStatus(@PathVariable Long storeId, @RequestBody StoreStatusUpdateRequestDto request) {
        return ResponseEntity.ok(storeService.updateStoreStatus(storeId, request));
    }

    @Operation(summary = "가게 정보 등록/수정", description = "가게의 상품 사진, 환불 규정, 입금 계좌번호, 취소 문의 전화번호를 등록하거나 수정합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": true, \"code\": \"COMMON200\", \"message\": \"성공입니다.\", \"result\": \"Store information updated.\"}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"NOTFOUND404\", \"message\": \"요청하신 리소스를 찾을 수 없습니다.\", \"result\": null}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"FORBIDDEN403\", \"message\": \"권한이 없습니다.\", \"result\": null}")))
    })
    @PutMapping("/{storeId}/info")
    public ResponseEntity<ApiResponse<?>> updateStoreInfo(@PathVariable Long storeId, @RequestBody StoreInfoUpdateRequestDto request) {
        return ResponseEntity.ok(storeService.updateStoreInfo(storeId, request));
    }

    @Operation(summary = "주문 내역 조회", description = "특정 가게의 모든 주문 내역을 최신순으로 조회합니다. 'STORE_ADMIN', 'SUPER_ADMIN', 'BOOTH_OPERATOR' 권한으로 접근 가능합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": true, \"code\": \"COMMON200\", \"message\": \"성공입니다.\", \"result\": [{\"orderId\": 1, \"customerName\": \"홍길동\", \"phoneNumber\": \"010-1234-5678\", \"deliveryAddress\": \"서울시 강남구\", \"status\": \"PENDING\", \"totalPrice\": 12000, \"orderDate\": \"2023-10-27T10:00:00\", \"orderItems\": [{\"menuId\": 1, \"menuName\": \"토스트\", \"quantity\": 2, \"price\": 10000}]}]}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"NOTFOUND404\", \"message\": \"요청하신 리소스를 찾을 수 없습니다.\", \"result\": null}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"FORBIDDEN403\", \"message\": \"권한이 없습니다.\", \"result\": null}")))
    })
    @GetMapping("/{storeId}/orders")
    public ResponseEntity<ApiResponse<List<OrderDto>>> getOrdersByStore(@PathVariable Long storeId) {
        return ResponseEntity.ok(orderService.getOrdersByStore(storeId));
    }

    @Operation(summary = "주문 상태 변경", description = "특정 주문의 상태(PENDING, PREPARING 등)를 변경합니다. 'STORE_ADMIN', 'SUPER_ADMIN', 'BOOTH_OPERATOR' 권한으로 접근 가능합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": true, \"code\": \"COMMON200\", \"message\": \"성공입니다.\", \"result\": \"Order status updated to PREPARING\"}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "주문 또는 가게를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"NOTFOUND404\", \"message\": \"요청하신 리소스를 찾을 수 없습니다.\", \"result\": null}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"FORBIDDEN403\", \"message\": \"권한이 없습니다.\", \"result\": null}")))
    })
    @PutMapping("/{storeId}/orders/{orderId}/status")
    public ResponseEntity<ApiResponse<?>> updateOrderStatus(@PathVariable Long storeId, @PathVariable Long orderId, @RequestBody OrderUpdateRequestDto request) {
        return ResponseEntity.ok(orderService.updateOrderStatus(storeId, orderId, request));
    }

    @Operation(summary = "가게 상태 조회", description = "관리자 권한으로 특정 가게의 현재 영업 상태를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": true, \"code\": \"COMMON200\", \"message\": \"성공입니다.\", \"result\": {\"storeId\": 1, \"status\": \"OPEN\", \"message\": \"현재 주문이 가능해요.\"}}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"NOTFOUND404\", \"message\": \"요청하신 리소스를 찾을 수 없습니다.\", \"result\": null}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"FORBIDDEN403\", \"message\": \"권한이 없습니다.\", \"result\": null}")))
    })
    @GetMapping("/{storeId}/info")
    public ResponseEntity<ApiResponse<StoreInfoDto>> getStoreInfo(@PathVariable Long storeId) {
        return ResponseEntity.ok(storeService.getStoreInfo(storeId));
    }

    @Operation(summary = "단골 조회", description = "부스 운영자가 특정 손님의 주문 내역을 조회합니다. 'STORE_ADMIN', 'SUPER_ADMIN', 'BOOTH_OPERATOR' 권한으로 접근 가능합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": true, \"code\": \"COMMON200\", \"message\": \"성공입니다.\", \"result\": [{\"orderId\": 1, \"customerName\": \"홍길동\", \"phoneNumber\": \"010-1234-5678\", \"deliveryAddress\": \"서울시 강남구\", \"status\": \"PENDING\", \"totalPrice\": 12000, \"orderDate\": \"2023-10-27T10:00:00\", \"orderItems\": [{\"menuId\": 1, \"menuName\": \"토스트\", \"quantity\": 2, \"price\": 10000}]}]}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "가게 또는 주문 내역을 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"NOTFOUND404\", \"message\": \"요청하신 리소스를 찾을 수 없습니다.\", \"result\": null}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"FORBIDDEN403\", \"message\": \"권한이 없습니다.\", \"result\": null}")))
    })
    @GetMapping("/{storeId}/customers/{phoneNumber}/orders")
    public ResponseEntity<ApiResponse<List<OrderDto>>> getOrdersByPhoneNumberAndStore(
            @PathVariable Long storeId, @PathVariable String phoneNumber) {
        return ResponseEntity.ok(orderService.getOrdersByPhoneNumberAndStore(storeId, phoneNumber));
    }

    @Operation(summary = "주문 삭제", description = "특정 주문과 해당 주문의 아이템들을 삭제합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": true, \"code\": \"COMMON200\", \"message\": \"성공입니다.\", \"result\": {\"orderId\": 1, \"status\": \"deleted\"}}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"NOTFOUND404\", \"message\": \"요청하신 리소스를 찾을 수 없습니다.\", \"result\": null}")))
    })
    @DeleteMapping("/{storeId}/orders/{orderId}")
    public ResponseEntity<ApiResponse<?>> deleteOrder(@PathVariable Long storeId, @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.deleteOrder(storeId, orderId));
    }
}
