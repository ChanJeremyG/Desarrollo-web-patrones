package com.ufide.biblioapp.controller;

import com.ufide.biblioapp.entity.Libro;
import com.ufide.biblioapp.service.LibroService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

// REQUISITO 4: API REST de libros. @RestController serializa los retornos a
// JSON directo (Jackson) - no busca vistas Thymeleaf como LibroController.
@RestController
@RequestMapping("/api/libros")
public class LibroRestController {

    @Autowired
    private LibroService libroService;

    // GET /api/libros -> catalogo completo en JSON, PUBLICO (no requiere login).
    @GetMapping
    public List<Libro> listar() {
        return libroService.listar();
    }

    // GET /api/libros/{id} -> 200 OK con el libro, o 404 si no existe.
    @GetMapping("/{id}")
    public ResponseEntity<Libro> detalle(@PathVariable Long id) {
        return libroService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/libros -> solo BIBLIOTECARIO, @Valid @RequestBody, 201 Created.
    // Si el JSON no cumple las validaciones de la entidad (ej. titulo vacio),
    // Spring devuelve 400 Bad Request automaticamente por @Valid.
    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    @PostMapping
    public ResponseEntity<Libro> crear(@Valid @RequestBody Libro libro) {
        Libro guardado = libroService.guardar(libro);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(guardado.getId())
                .toUri();
        return ResponseEntity.created(location).body(guardado);
    }

    // PUT /api/libros/{id} -> solo BIBLIOTECARIO, 200 OK, o 404 si no existe.
    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    @PutMapping("/{id}")
    public ResponseEntity<Libro> actualizar(@PathVariable Long id, @Valid @RequestBody Libro libro) {
        if (libroService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        libro.setId(id);
        return ResponseEntity.ok(libroService.guardar(libro));
    }

    // DELETE /api/libros/{id} -> solo BIBLIOTECARIO, 204 No Content, o 404.
    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (libroService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        libroService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}