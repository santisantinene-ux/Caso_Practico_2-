package com.ufide.biblioapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // la raiz redirige al catalogo
    @GetMapping("/")
    public String home() {
        return "redirect:/libros";
    }

    // pagina de login
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // pagina de acceso denegado
    @GetMapping("/403")
    public String accesoDenegado() {
        return "403";
    }
}
