package com.ufide.biblioapp.repository;

import com.ufide.biblioapp.dto.LibroRanking;
import com.ufide.biblioapp.entity.Prestamo;
import com.ufide.biblioapp.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    // libros mas prestados (bonus)
    @Query("SELECT new com.ufide.biblioapp.dto.LibroRanking(p.libro.titulo, COUNT(p)) "
            + "FROM Prestamo p GROUP BY p.libro ORDER BY COUNT(p) DESC")
    List<LibroRanking> ranking();

    // todos los prestamos; JOIN FETCH para evitar el error de lazy en la vista
    @Query("SELECT p FROM Prestamo p JOIN FETCH p.libro JOIN FETCH p.usuario ORDER BY p.fechaPrestamo DESC")
    List<Prestamo> findAllConLibroYUsuario();

    // los prestamos de un solo usuario (para "mis prestamos" del lector)
    @Query("SELECT p FROM Prestamo p JOIN FETCH p.libro JOIN FETCH p.usuario WHERE p.usuario = :usuario ORDER BY p.fechaPrestamo DESC")
    List<Prestamo> findByUsuario(Usuario usuario);

    // Requisito 5.3: prestamos sin devolver y con fecha limite vencida
    @Query("SELECT p FROM Prestamo p JOIN FETCH p.libro JOIN FETCH p.usuario "
            + "WHERE p.fechaDevolucion IS NULL AND p.fechaLimite < CURRENT_DATE "
            + "ORDER BY p.fechaLimite ASC")
    List<Prestamo> prestamosAtrasados();
}
