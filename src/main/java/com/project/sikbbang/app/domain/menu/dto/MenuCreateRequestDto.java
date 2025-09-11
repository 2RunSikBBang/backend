package com.project.sikbbang.app.domain.menu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class MenuCreateRequestDto {

    @NotBlank
    private String name;

    @NotNull
    private Integer price;

    private Integer minOrderQuantity;
    private Integer maxOrderQuantity;

    @NotNull
    private Boolean isRepresentative;
}
