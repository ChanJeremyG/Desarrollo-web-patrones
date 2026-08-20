package com.ufide.biblioapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "prestamos")
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // REQUISITO 2: asociacion @ManyToOne hacia Libro (lado "muchos").
    // Se usa LAZY igual que en cursosapp (Curso -> Profesor) para no traer
    // siempre el libro completo; las consultas de listado usan JOIN FETCH
    // para evitar el problema N+1.
    @NotNull(message = "El libro es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "libro_id", nullable = false)
    private Libro libro;

    @NotNull(message = "El usuario es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotNull(message = "La fecha de prestamo es obligatoria")
    private java.time.LocalDate fechaPrestamo;

    // 14 dias despues de fechaPrestamo. NO se valida con @NotNull porque el
    // formulario no la envia: PrestamoService.registrar() la calcula sola.
    private java.time.LocalDate fechaLimite;

    // NULL mientras el libro no se devuelve
    private java.time.LocalDate fechaDevolucion;

    public Prestamo() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Libro getLibro() { return libro; }
    public void setLibro(Libro libro) { this.libro = libro; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public java.time.LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public void setFechaPrestamo(java.time.LocalDate fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }

    public java.time.LocalDate getFechaLimite() { return fechaLimite; }
    public void setFechaLimite(java.time.LocalDate fechaLimite) { this.fechaLimite = fechaLimite; }

    public java.time.LocalDate getFechaDevolucion() { return fechaDevolucion; }
    public void setFechaDevolucion(java.time.LocalDate fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }
}