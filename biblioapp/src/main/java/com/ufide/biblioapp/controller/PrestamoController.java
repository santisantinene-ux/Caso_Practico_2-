package com.ufide.biblioapp.controller;

import com.ufide.biblioapp.entity.Libro;
import com.ufide.biblioapp.entity.Usuario;
import com.ufide.biblioapp.service.LibroService;
import com.ufide.biblioapp.service.PrestamoService;
import com.ufide.biblioapp.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/prestamos")
public class PrestamoController {

    @Autowired
    private PrestamoService prestamoService;

    @Autowired
    private LibroService libroService;

    @Autowired
    private UsuarioService usuarioService;

    // muestra todos los prestamos, solo el bibliotecario porque son de todos
    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("prestamos", prestamoService.listarTodos());
        model.addAttribute("atrasados", prestamoService.listarAtrasados());
        model.addAttribute("soloMios", false);
        return "prestamos";
    }

    // el lector solo ve sus prestamos, agarro el usuario que esta logueado
    @GetMapping("/mios")
    public String misPrestamos(Authentication auth, Model model) {
        Usuario usuario = usuarioService.buscarPorUsername(auth.getName());
        model.addAttribute("prestamos", prestamoService.listarPorUsuario(usuario));
        model.addAttribute("soloMios", true);
        return "prestamos";
    }

    // form para registrar un prestamo, solo bibliotecario
    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("libros", libroService.listar());
        model.addAttribute("usuarios", usuarioService.listar());
        return "prestamo-form";
    }

    // guarda el prestamo, solo bibliotecario
    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    @PostMapping
    public String registrar(@RequestParam Long libroId,
                            @RequestParam Long usuarioId,
                            RedirectAttributes ra) {
        Libro libro = libroService.buscarPorId(libroId).orElse(null);
        Usuario usuario = usuarioService.buscarPorId(usuarioId);
        if (libro == null || usuario == null) {
            ra.addFlashAttribute("error", "Libro o usuario invalido.");
            return "redirect:/prestamos/nuevo";
        }
        try {
            prestamoService.registrar(libro, usuario);
            ra.addFlashAttribute("mensaje", "Prestamo registrado correctamente.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/prestamos";
    }

    // marca un prestamo como devuelto, solo bibliotecario
    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    @PostMapping("/{id}/devolver")
    public String devolver(@PathVariable Long id, RedirectAttributes ra) {
        try {
            prestamoService.devolver(id);
            ra.addFlashAttribute("mensaje", "Devolucion registrada correctamente.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/prestamos";
    }
}
