package com.ufide.biblioapp.controller;

import com.ufide.biblioapp.entity.Prestamo;
import com.ufide.biblioapp.entity.Rol;
import com.ufide.biblioapp.entity.Usuario;
import com.ufide.biblioapp.service.LibroService;
import com.ufide.biblioapp.service.PrestamoService;
import com.ufide.biblioapp.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
public class PrestamoController {

    @Autowired
    private PrestamoService prestamoService;

    @Autowired
    private LibroService libroService;

    @Autowired
    private UsuarioService usuarioService;

    // REQUISITO 3: ver prestamos. Cualquier usuario logueado puede entrar,
    // pero el BIBLIOTECARIO ve TODOS los prestamos (+ la seccion de atrasados)
    // y el LECTOR solo los suyos. La separacion se hace aca en el metodo.
    @GetMapping("/prestamos")
    @PreAuthorize("hasRole('BIBLIOTECARIO') or hasRole('LECTOR')")
    public String listar(Authentication auth, Model model) {
        if (esBibliotecario(auth)) {
            model.addAttribute("prestamos", prestamoService.listarTodos());
            // REQUISITO 5: consulta JPQL prestamosAtrasados() tambien visible
            // en HTML para el bibliotecario.
            model.addAttribute("atrasados", prestamoService.prestamosAtrasados());
        } else {
            // LECTOR: buscar el Usuario completo a partir del username y
            // listar solo sus prestamos (findByUsuario).
            Usuario lector = usuarioService.buscarPorUsername(auth.getName());
            model.addAttribute("prestamos", prestamoService.listarPorUsuario(lector));
        }
        return "prestamos";
    }

    @GetMapping("/prestamos/nuevo")
    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    public String mostrarFormNuevo(@RequestParam(required = false) Long libroId, Model model) {
        Prestamo prestamo = new Prestamo();
        if (libroId != null) {
            prestamo.setLibro(new com.ufide.biblioapp.entity.Libro());
            prestamo.getLibro().setId(libroId);
        }
        model.addAttribute("prestamo", prestamo);
        cargarFormulario(model);
        return "prestamo-form";
    }

    @PostMapping("/prestamos")
    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    public String guardar(@Valid @ModelAttribute("prestamo") Prestamo prestamo,
                          BindingResult result,
                          Model model,
                          RedirectAttributes ra) {
        if (result.hasErrors()) {
            cargarFormulario(model);
            return "prestamo-form";
        }
        try {
            prestamoService.registrar(prestamo);
            ra.addFlashAttribute("ok", "Prestamo registrado correctamente");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/prestamos";
    }

    @PostMapping("/prestamos/{id}/devolver")
    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    public String devolver(@PathVariable Long id, RedirectAttributes ra) {
        try {
            prestamoService.devolver(id);
            ra.addFlashAttribute("ok", "Devolucion registrada correctamente");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/prestamos";
    }

    private void cargarFormulario(Model model) {
        model.addAttribute("libros", libroService.listar());
        model.addAttribute("usuarios", usuarioService.listarLectores());
    }

    // Compara la authority de la sesion contra el enum Rol (no contra strings).
    private boolean esBibliotecario(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(g -> g.getAuthority().equals("ROLE_" + Rol.BIBLIOTECARIO.name()));
    }
}