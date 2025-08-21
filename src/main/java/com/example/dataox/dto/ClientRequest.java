package com.example.dataox.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClientRequest {
    @NotBlank
    private String name;

    @NotNull
    private String email;

    @NotNull
    private String address;

    @NotNull
    private boolean active;
}
