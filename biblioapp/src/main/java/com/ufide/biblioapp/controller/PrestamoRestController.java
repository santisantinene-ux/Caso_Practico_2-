package com.ufide.biblioapp.controller;

import com.ufide.biblioapp.dto.PrestamoRequest;
import com.ufide.biblioapp.dto.PrestamoResponse;
import com.ufide.biblioapp.entity.Libro;
import com.ufide.biblioapp.entity.Prestamo;
import com.ufide.biblioapp.entity.Usuario;
import com.ufide.biblioapp.service.LibroService;
import com.ufide.biblioapp.service.PrestamoService;
import com.ufide.biblioapp.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoRestController {

    @Autowired
    private PrestamoService prestamoService;

    @Autowired
    private LibroService libroService;

    @Autowired
    private UsuarioService usuarioService;

    // GET /api/prestamos/atrasados -> 200. usa la consulta JPQL del Req 5.3.
    // solo BIBLIOTECARIO puede ver prestamos ajenos
    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    @GetMapping("/atrasados")
    public List<Prestamo> atrasados() {
        return prestamoService.listarAtrasados();
    }

    // registrar prestamo (bonus): 201 ok, 404 si no existe libro/usuario,
    // 409 si no hay copias. devuelvo un DTO para no serializar las relaciones lazy
    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody PrestamoRequest req) {
        Libro libro = libroService.buscarPorId(req.libroId()).orElse(null);
        Usuario usuario = usuarioService.buscarPorId(req.usuarioId());
        if (libro == null || usuario == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            Prestamo guardado = prestamoService.registrar(libro, usuario);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(PrestamoResponse.desde(guardado));
        } catch (IllegalStateException e) {
            // no hay copias -> 409 conflicto
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
