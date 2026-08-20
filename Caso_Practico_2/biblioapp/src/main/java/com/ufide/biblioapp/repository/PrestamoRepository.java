package com.ufide.biblioapp.repository;

import com.ufide.biblioapp.entity.Prestamo;
import com.ufide.biblioapp.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    // Listado con JOIN FETCH para evitar el problema N+1 al mostrar
    // prestamo.getLibro().getTitulo() y prestamo.getUsuario().getUsername()
    // en las vistas (mismo patron que Curso/Profesor de la Semana 9).
    @Query("SELECT p FROM Prestamo p JOIN FETCH p.libro JOIN FETCH p.usuario")
    List<Prestamo> findAllConLibroYUsuario();

    // REQUISITO 3: "un LECTOR solo puede ver sus propios prestamos".
    // El nombre exacto findByUsuario(Usuario usuario) deja que Spring Data
    // JPA lo implemente solo a partir de la firma; se anade JOIN FETCH para
    // traer tambien el libro sin disparar consultas extra por cada prestamo.
    @Query("SELECT p FROM Prestamo p JOIN FETCH p.libro JOIN FETCH p.usuario WHERE p.usuario = :usuario")
    List<Prestamo> findByUsuario(@Param("usuario") Usuario usuario);

    // REQUISITO 5 - Ejemplo 3 (propia): prestamos atrasados.
    // Un prestamo esta atrasado si NO se devolvio (fechaDevolucion IS NULL)
    // y ya paso la fecha limite (fechaLimite es anterior a hoy). CURRENT_DATE
    // es la funcion JPQL que devuelve la fecha actual.
    @Query("SELECT p FROM Prestamo p JOIN FETCH p.libro JOIN FETCH p.usuario " +
           "WHERE p.fechaDevolucion IS NULL AND p.fechaLimite < CURRENT_DATE")
    List<Prestamo> prestamosAtrasados();
}