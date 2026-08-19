package com.ufide.biblioapp.controller;

import com.ufide.biblioapp.service.LibroService;
import com.ufide.biblioapp.service.PrestamoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// paginas HTML del catalogo (con Thymeleaf)
@Controller
public class LibroController {

    @Autowired
    private LibroService libroService;

    @Autowired
    private PrestamoService prestamoService;

    // catalogo con todos los libros
    @GetMapping("/libros")
    public String listar(Model model) {
        model.addAttribute("libros", libroService.listar());
        return "libros";
    }

    // detalle de un libro
    @GetMapping("/libros/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("libro", libroService.buscarPorId(id).orElse(null));
        return "libro-detalle";
    }

    // ranking de los mas prestados (bonus)
    @GetMapping("/ranking")
    public String ranking(Model model) {
        model.addAttribute("ranking", prestamoService.ranking());
        return "ranking";
    }
}
