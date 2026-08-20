package com.ufide.biblioapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "libros")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El titulo es obligatorio")
    @Size(max = 150)
    private String titulo;

    @NotBlank(message = "El autor es obligatorio")
    @Size(max = 100)
    private String autor;

    @NotBlank(message = "El ISBN es obligatorio")
    @Size(max = 20)
    private String isbn;

    @NotBlank(message = "La categoria es obligatoria")
    @Size(max = 60)
    private String categoria;

    @NotNull
    @Positive(message = "Debe haber al menos 1 copia total")
    private Integer copiasTotales;

    @NotNull
    @PositiveOrZero(message = "Las copias disponibles no pueden ser negativas")
    private Integer copiasDisponibles;

    // ==========================================================
    // CASO PRACTICO 2 - REQUISITO 2:
    // Este campo se relaciona con Prestamo mediante un @ManyToOne
    // desde el lado de Prestamo (Prestamo.libro -> Libro).
    // No hace falta agregar nada aca en Libro para que la relacion
    // funcione (el lado "uno" de la relacion no necesita anotacion),
    // salvo que decidas navegar tambien Libro -> lista de Prestamo
    // con un @OneToMany(mappedBy = "libro") — eso es opcional.
    // ==========================================================

    public Libro() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Integer getCopiasTotales() { return copiasTotales; }
    public void setCopiasTotales(Integer copiasTotales) { this.copiasTotales = copiasTotales; }

    public Integer getCopiasDisponibles() { return copiasDisponibles; }
    public void setCopiasDisponibles(Integer copiasDisponibles) { this.copiasDisponibles = copiasDisponibles; }
}
