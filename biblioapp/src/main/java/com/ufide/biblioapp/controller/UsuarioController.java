package com.ufide.biblioapp.controller;

import com.ufide.biblioapp.entity.Rol;
import com.ufide.biblioapp.entity.Usuario;
import com.ufide.biblioapp.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// ABM de usuarios (bonus). todo esto es solo para el bibliotecario
@Controller
@RequestMapping("/usuarios")
@PreAuthorize("hasRole('BIBLIOTECARIO')")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        return "usuarios";
    }

    // form para crear un usuario nuevo, le paso un usuario vacio y los roles
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", Rol.values());
        return "usuario-form";
    }

    // form para editar, ya con los datos del usuario cargados (menos la clave)
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Usuario usuario = usuarioService.buscarPorId(id);
        if (usuario == null) {
            ra.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/usuarios";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", Rol.values());
        return "usuario-form";
    }

    // crea el usuario. la clave es obligatoria y se encripta en el service
    @PostMapping
    public String crear(@RequestParam String username,
                        @RequestParam String password,
                        @RequestParam String nombreCompleto,
                        @RequestParam(required = false) String email,
                        @RequestParam String rol,
                        RedirectAttributes ra) {
        if (password == null || password.isBlank()) {
            ra.addFlashAttribute("error", "El password es obligatorio al crear un usuario.");
            return "redirect:/usuarios/nuevo";
        }
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(password);
        usuario.setNombreCompleto(nombreCompleto);
        usuario.setEmail(email);
        usuario.setRol(rol);
        try {
            usuarioService.crear(usuario);
            ra.addFlashAttribute("mensaje", "Usuario creado correctamente.");
            return "redirect:/usuarios";
        } catch (IllegalArgumentException e) {              // el rol no es valido
            ra.addFlashAttribute("error", e.getMessage());
        } catch (DataIntegrityViolationException e) {        // el username ya existe
            ra.addFlashAttribute("error", "El nombre de usuario ya existe.");
        }
        return "redirect:/usuarios/nuevo";
    }

    // edita el usuario. si la clave va en blanco, se deja la que ya tenia
    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
                             @RequestParam String username,
                             @RequestParam(required = false) String password,
                             @RequestParam String nombreCompleto,
                             @RequestParam(required = false) String email,
                             @RequestParam String rol,
                             RedirectAttributes ra) {
        Usuario datos = new Usuario();
        datos.setUsername(username);
        datos.setNombreCompleto(nombreCompleto);
        datos.setEmail(email);
        datos.setRol(rol);
        try {
            usuarioService.actualizar(id, datos, password);
            ra.addFlashAttribute("mensaje", "Usuario actualizado correctamente.");
            return "redirect:/usuarios";
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        } catch (DataIntegrityViolationException e) {
            ra.addFlashAttribute("error", "El nombre de usuario ya existe.");
        }
        return "redirect:/usuarios/" + id + "/editar";
    }

    // borra el usuario. no dejo que se borre a si mismo, y si tiene prestamos
    // muestro un aviso en vez de que tire error
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        Usuario objetivo = usuarioService.buscarPorId(id);
        if (objetivo == null) {
            ra.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/usuarios";
        }
        if (objetivo.getUsername().equals(auth.getName())) {
            ra.addFlashAttribute("error", "No podes eliminar tu propia cuenta mientras estas logueado.");
            return "redirect:/usuarios";
        }
        try {
            usuarioService.eliminar(id);
            ra.addFlashAttribute("mensaje", "Usuario eliminado correctamente.");
        } catch (DataIntegrityViolationException e) {
            ra.addFlashAttribute("error", "No se puede eliminar: el usuario tiene prestamos asociados.");
        }
        return "redirect:/usuarios";
    }
}
