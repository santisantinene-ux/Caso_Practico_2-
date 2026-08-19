package com.ufide.biblioapp.controller;

import com.ufide.biblioapp.entity.Libro;
import com.ufide.biblioapp.service.LibroService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/libros")
public class LibroRestController {

    @Autowired
    private LibroService libroService;

    // devuelve todos los libros, cualquiera lo puede ver
    @GetMapping
    public List<Libro> listar() {
        return libroService.listar();
    }

    // busca un libro por id, si no existe devuelve 404
    @GetMapping("/{id}")
    public ResponseEntity<Libro> obtener(@PathVariable Long id) {
        return libroService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // libros de una categoria (bonus)
    @GetMapping("/categoria/{categoria}")
    public List<Libro> porCategoria(@PathVariable String categoria) {
        return libroService.listarPorCategoria(categoria);
    }

    // crea un libro nuevo, solo lo puede hacer el bibliotecario
    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    @PostMapping
    public ResponseEntity<Libro> crear(@Valid @RequestBody Libro libro) {
        Libro guardado = libroService.guardar(libro);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }
}
