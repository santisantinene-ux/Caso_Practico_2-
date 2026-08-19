package com.ufide.biblioapp.dto;

import jakarta.validation.constraints.NotNull;

// lo que recibe la API para crear un prestamo
public record PrestamoRequest(
        @NotNull(message = "libroId es obligatorio") Long libroId,
        @NotNull(message = "usuarioId es obligatorio") Long usuarioId) {
}
