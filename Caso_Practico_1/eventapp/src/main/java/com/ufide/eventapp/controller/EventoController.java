package com.ufide.eventapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ufide.eventapp.entity.Evento;
import com.ufide.eventapp.service.EventoService;

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

    // TODO Caso Practico 1: agregar aca los endpoints del CRUD y el GET con parametro.
}
