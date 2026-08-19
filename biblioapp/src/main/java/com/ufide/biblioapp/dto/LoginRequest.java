package com.ufide.biblioapp.dto;

import jakarta.validation.constraints.NotBlank;

// usuario y password que llegan al login de la API
public record LoginRequest(
        @NotBlank(message = "username es obligatorio") String username,
        @NotBlank(message = "password es obligatorio") String password) {
}
