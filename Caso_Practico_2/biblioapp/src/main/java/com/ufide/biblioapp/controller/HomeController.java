package com.ufide.biblioapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/libros";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // REQUISITO 3: pagina de acceso denegado. SecurityConfig redirige aca
    // cuando @PreAuthorize bloquea a un usuario autenticado sin el rol.
    // Se mapea con @RequestMapping (sin limitar el metodo HTTP) porque el
    // forward interno llega con el mismo metodo del request original: si un
    // POST o PUT se bloquea, el forward a /403 tambien es POST.
    @RequestMapping("/403")
    public String accesoDenegado() {
        return "403";
    }
}