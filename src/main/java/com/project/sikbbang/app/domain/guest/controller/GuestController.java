package com.project.sikbbang.app.domain.guest.controller;

import com.project.sikbbang.app.domain.menu.dto.MenuDto;
import com.project.sikbbang.app.domain.order.dto.OrderDto;
import com.project.sikbbang.app.domain.order.dto.OrderRequestDto;
import com.project.sikbbang.app.domain.order.service.OrderService;
import com.project.sikbbang.app.domain.store.dto.StoreInfoDto;
import com.project.sikbbang.app.domain.store.dto.StoreStatusDto;
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

@Tag(name = "1.Guest API", description = "비회원(Guest) 사용자 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/guest")
public class GuestController {

    private final OrderService orderService;
    private final StoreService storeService;

    @Operation(summary = "특정 가게 메뉴 목록 조회", description = "비회원 사용자가 특정 가게의 메뉴 목록을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": true, \"code\": \"COMMON200\", \"message\": \"성공입니다.\", \"result\": [{\"id\": 1,\"name\": \"메뉴1\", \"price\": 5000, \"minOrderQuantity\": 1, \"maxOrderQuantity\": 10, \"isRepresentative\": true}]}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"NOTFOUND404\", \"message\": \"요청하신 리소스를 찾을 수 없습니다.\", \"result\": null}")))
    })
    @GetMapping("/stores/{storeId}/menus")
    public ResponseEntity<ApiResponse<List<MenuDto>>> getMenusByStore(@PathVariable Long storeId) {
        return ResponseEntity.ok(storeService.getMenusByStore(storeId));
    }

    @Operation(summary = "메뉴 주문 생성", description = "비회원 사용자가 특정 가게에 메뉴를 주문합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": true, \"code\": \"COMMON200\", \"message\": \"성공입니다.\", \"result\": 1}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "가게 또는 메뉴를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"NOTFOUND404\", \"message\": \"요청하신 리소스를 찾을 수 없습니다.\", \"result\": null}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"BAD_REQUEST400\", \"message\": \"잘못된 요청입니다.\", \"result\": null}")))
    })
    @PostMapping("/stores/{storeId}/orders")
    public ResponseEntity<ApiResponse<?>> createOrder(@PathVariable Long storeId, @RequestBody OrderRequestDto request) {
        return ResponseEntity.ok(orderService.createOrder(storeId, request));
    }

    @Operation(summary = "휴대폰 번호로 주문 내역 조회", description = "휴대폰 번호를 통해 비회원 주문 내역을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": true, \"code\": \"COMMON200\", \"message\": \"성공입니다.\", \"result\": [{\"orderId\": 1, \"customerName\": \"홍길동\", \"phoneNumber\": \"010-1234-5678\", \"deliveryAddress\": \"서울시 강남구\", \"status\": \"PENDING\", \"totalPrice\": 12000, \"orderDate\": \"2023-10-27T10:00:00\", \"orderItems\": [{\"menuId\": 1, \"menuName\": \"토스트\", \"quantity\": 2, \"price\": 10000}]}]}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "주문 내역을 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"NOTFOUND404\", \"message\": \"요청하신 리소스를 찾을 수 없습니다.\", \"result\": null}")))
    })
    @GetMapping("/orders/{phoneNumber}")
    public ResponseEntity<ApiResponse<List<OrderDto>>> getOrdersByPhoneNumber(@PathVariable String phoneNumber) {
        return ResponseEntity.ok(orderService.getOrdersByPhoneNumber(phoneNumber));
    }

    @Operation(summary = "모든 가게 상태 목록 조회", description = "비회원 사용자가 모든 가게의 현재 영업 상태 목록을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": true, \"code\": \"COMMON200\", \"message\": \"성공입니다.\", \"result\": [{\"storeId\": 1, \"status\": \"OPEN\", \"message\": \"현재 주문이 가능해요.\"}, {\"storeId\": 2, \"status\": \"DELAYED\", \"message\": \"현재 주문량이 많아 주문이 지연되고 있어요.\"}]}"))),
    })
    @GetMapping("/stores/status/all")
    public ResponseEntity<ApiResponse<List<StoreStatusDto>>> getAllStoreStatuses() {
        return ResponseEntity.ok(storeService.getAllStoreStatuses());
    }

    @Operation(summary = "특정 가게 상세 정보 조회", description = "비회원 사용자가 특정 가게의 상세 정보(프로필, 사진, 환불 정책 등)를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": true, \"code\": \"COMMON200\", \"message\": \"성공입니다.\", \"result\": {\"id\": 1, \"name\": \"식빵이네\", \"profileImageUrl\": \"url\", \"profileImageUrls\": [\"url1\", \"url2\"], \"refundPolicy\": \"주문 후 5분 이내 전액 환불\", \"bankAccount\": \"신한 110-218-986002\", \"cancelPhoneNumber\": \"010-1234-5678\", \"statusMessage\": \"현재 주문이 가능해요.\"}}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{\"isSuccess\": false, \"code\": \"NOTFOUND404\", \"message\": \"요청하신 리소스를 찾을 수 없습니다.\", \"result\": null}")))
    })
    @GetMapping("/stores/{storeId}/info")
    public ResponseEntity<ApiResponse<StoreInfoDto>> getStoreInfo(@PathVariable Long storeId) {
        return ResponseEntity.ok(storeService.getStoreInfo(storeId));
    }
}
