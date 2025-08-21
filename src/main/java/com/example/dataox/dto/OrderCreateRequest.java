package com.example.dataox.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderCreateRequest {
    @NotBlank
    private String name;
    @NotNull
    private Long supplierId;
    @NotNull
    private Long consumerId;
    @NotNull @Positive
    private BigDecimal price;
}
