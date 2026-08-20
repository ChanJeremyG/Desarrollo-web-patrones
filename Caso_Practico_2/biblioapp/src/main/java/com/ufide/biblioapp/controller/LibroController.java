package com.ufide.biblioapp.controller;

import com.ufide.biblioapp.entity.Libro;
import com.ufide.biblioapp.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class LibroController {

    @Autowired
    private LibroService libroService;

    @GetMapping("/libros")
    public String listar(Model model) {
        model.addAttribute("libros", libroService.listar());
        return "libros";
    }

    @GetMapping("/libros/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("libro", libroService.buscarPorId(id).orElse(null));
        return "libro-detalle";
    }

    // ==========================================================
    // CASO PRACTICO 2 - REQUISITO 2:
    // Aca vas a agregar las rutas de PrestamoController (o un
    // controller nuevo PrestamoController.java) para registrar
    // prestamos y devoluciones, protegidas con @PreAuthorize
    // segun el Requisito 3.
    // ==========================================================
}
