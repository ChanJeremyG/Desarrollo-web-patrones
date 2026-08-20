package com.ufide.biblioapp.repository;

import com.ufide.biblioapp.entity.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Long> {

    // REQUISITO 5 - Ejemplo 2 (dado): libros que nunca se prestaron.
    // LEFT JOIN con condicion ON: registros de "libros" que no tienen
    // ningun Prestamo asociado (p.id IS NULL).
    @Query("SELECT l FROM Libro l LEFT JOIN Prestamo p ON p.libro = l WHERE p.id IS NULL")
    List<Libro> librosNuncaPrestados();
}