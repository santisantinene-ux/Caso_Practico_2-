package com.ufide.biblioapp.repository;

import com.ufide.biblioapp.entity.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Long> {

    // Spring Data arma la query sola a partir del nombre del metodo
    List<Libro> findByCategoriaIgnoreCase(String categoria);
}
