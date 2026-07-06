package com.ufide.eventapp.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ufide.eventapp.entity.Evento;

/**
 * Repositorio de eventos.
 *
 * Ya extiende JpaRepository (te trae findAll, findById, save, deleteById, count).
 * Tambien viene con UN query method de ejemplo.
 *
 * Como parte del Caso Practico necesitas agregar al menos UN query method
 * adicional para soportar el endpoint con parametro que se pide.
 */
public interface EventoRepository extends JpaRepository<Evento, Long> {

    /** Devuelve eventos filtrados por categoria (ya implementado, sirve de ejemplo). */
    List<Evento> findByCategoria(String categoria);

    /** Devuelve eventos cuya fecha sea posterior o igual a la fecha dada. */
    List<Evento> findByFechaGreaterThanEqualOrderByFechaAsc(LocalDate fecha);

    // TODO Caso Practico: aca podes agregar otros query methods que necesites.
    // Ejemplo (no obligatorio):
    //   List<Evento> findByNombreContainingIgnoreCase(String texto);
}
