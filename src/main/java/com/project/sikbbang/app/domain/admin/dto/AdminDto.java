package com.project.sikbbang.app.domain.admin.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AdminDto {
    private Long id;
    private String username;
    private String role;
    private Long storeId;
}
