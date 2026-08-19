package com.ufide.biblioapp.controller;

import com.ufide.biblioapp.service.PasswordResetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

// las pantallas del "olvide mi contrasena" (bonus). cualquiera las puede ver
@Controller
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    // pantalla donde el usuario escribe su correo
    @GetMapping("/forgot")
    public String formularioForgot() {
        return "forgot";
    }

    // manda el link de recuperacion al correo
    @PostMapping("/forgot")
    public String enviarLink(@RequestParam String email, RedirectAttributes ra) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        passwordResetService.solicitarRecuperacion(email, baseUrl);
        ra.addFlashAttribute("mensaje",
                "Si el correo esta registrado, te enviamos un enlace para restablecer la contrasena.");
        return "redirect:/forgot";
    }

    // pantalla para poner la nueva contrasena; antes reviso que el token sirva
    @GetMapping("/reset")
    public String formularioReset(@RequestParam String token, Model model) {
        if (!passwordResetService.tokenValido(token)) {
            model.addAttribute("tokenInvalido", true);
            return "reset";
        }
        model.addAttribute("token", token);
        return "reset";
    }

    @PostMapping("/reset")
    public String restablecer(@RequestParam String token,
                              @RequestParam String password,
                              RedirectAttributes ra) {
        boolean ok = passwordResetService.restablecer(token, password);
        if (!ok) {
            ra.addFlashAttribute("error", "El enlace no es valido o ya expiro. Pedi uno nuevo.");
            return "redirect:/forgot";
        }
        ra.addFlashAttribute("mensaje", "Contrasena actualizada. Ya podes ingresar.");
        return "redirect:/login";
    }
}
