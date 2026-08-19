package com.ufide.biblioapp.dto;

import com.ufide.biblioapp.entity.Prestamo;

import java.time.LocalDate;

// lo que devuelve la API al crear un prestamo (asi no serializo la entidad y sus relaciones lazy)
public record PrestamoResponse(
        Long id,
        Long libroId,
        String libroTitulo,
        Long usuarioId,
        String usuarioNombre,
        LocalDate fechaPrestamo,
        LocalDate fechaLimite,
        LocalDate fechaDevolucion) {

    // arma el DTO a partir del prestamo
    public static PrestamoResponse desde(Prestamo p) {
        return new PrestamoResponse(
                p.getId(),
                p.getLibro().getId(),
                p.getLibro().getTitulo(),
                p.getUsuario().getId(),
                p.getUsuario().getNombreCompleto(),
                p.getFechaPrestamo(),
                p.getFechaLimite(),
                p.getFechaDevolucion());
    }
}
