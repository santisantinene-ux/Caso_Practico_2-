package com.ufide.biblioapp.dto;

import com.ufide.biblioapp.entity.Prestamo;

import java.time.LocalDate;

// lo que devuelve la API cuando se crea un prestamo, para no mandar la entidad entera
public record PrestamoResponse(
        Long id,
        Long libroId,
        String libroTitulo,
        Long usuarioId,
        String usuarioNombre,
        LocalDate fechaPrestamo,
        LocalDate fechaLimite,
        LocalDate fechaDevolucion) {

    // copia los datos del prestamo a este objeto
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
