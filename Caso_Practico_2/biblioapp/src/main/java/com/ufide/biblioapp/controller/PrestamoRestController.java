package com.ufide.biblioapp.controller;

import com.ufide.biblioapp.entity.Prestamo;
import com.ufide.biblioapp.service.PrestamoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// REQUISITO 4 + REQUISITO 5: GET /api/prestamos/atrasados usa la consulta
// JPQL prestamosAtrasados() de PrestamoRepository y devuelve 200 con la lista.
@RestController
@RequestMapping("/api/prestamos")
public class PrestamoRestController {

    @Autowired
    private PrestamoService prestamoService;

    // Listar prestamos atrasados es una funcion del bibliotecario, igual que
    // ver el listado completo de prestamos (REQUISITO 3).
    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    @GetMapping("/atrasados")
    public List<Prestamo> atrasados() {
        return prestamoService.prestamosAtrasados();
    }
}