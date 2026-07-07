package com.ufide.eventapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ufide.eventapp.entity.Evento;
import com.ufide.eventapp.service.EventoService;

import jakarta.validation.Valid;

/**
 * Controlador de eventos - estado base del Caso Practico 1.
 *
 * Endpoints ya implementados:
 *   GET /eventos          -> listar todos
 *   GET /eventos/{id}     -> detalle
 *
 * Endpoints que tenes que implementar (Caso Practico):
 *   GET  /eventos/categoria/{categoria}   -> filtrar por categoria (endpoint paramétrico)
 *   GET  /eventos/nuevo                   -> mostrar form vacio
 *   POST /eventos                         -> guardar nuevo con validaciones
 *   GET  /eventos/{id}/editar             -> mostrar form precargado
 *   POST /eventos/{id}                    -> actualizar
 *   POST /eventos/{id}/eliminar           -> borrar
 */
@Controller
@RequestMapping("/eventos")
public class EventoController {

    @Autowired
    private EventoService service;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("eventos", service.listar());
        return "eventos";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        Evento evento = service.buscarPorId(id).orElse(null);
        model.addAttribute("evento", evento);
        return "evento";
    }

    @GetMapping("/nuevo")
    public String formNuevo(Model model) {
        model.addAttribute("evento", new Evento());
        return "eventos/form";
    }

    @PostMapping
    public String guardar(@Valid @ModelAttribute Evento evento,
                          BindingResult result,
                          RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "eventos/form";
        }
        service.guardar(evento);
        ra.addFlashAttribute("ok", "Evento guardado exitosamente");
        return "redirect:/eventos";
    }

    @GetMapping("/{id}/editar")
    public String formEditar(@PathVariable Long id, Model model) {
        Evento evento = service.buscarPorId(id).orElse(null);
        if (evento == null) {
            return "redirect:/eventos";
        }
        model.addAttribute("evento", evento);
        return "eventos/form";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute Evento evento,
                             BindingResult result,
                             RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "eventos/form";
        }
        evento.setId(id);
        service.guardar(evento);
        ra.addFlashAttribute("ok", "Evento actualizado exitosamente");
        return "redirect:/eventos";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        service.eliminar(id);
        ra.addFlashAttribute("ok", "Evento eliminado exitosamente");
        return "redirect:/eventos";
    }

    @GetMapping("/categoria/{categoria}")
    public String porCategoria(@PathVariable String categoria, Model model) {
        model.addAttribute("eventos", service.buscarPorCategoria(categoria));
        model.addAttribute("filtro", "Categoria: " + categoria);
        return "eventos";
    }
}
