package com.ufide.clase4base.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// CLASE 6 - PARTE A.4: descomentar cuando agreguemos las validaciones
// import jakarta.validation.constraints.Max;
// import jakarta.validation.constraints.Min;
// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.Size;

/**
 * Modelo de datos del Curso.
 *
 * CLASE 4: convertir esta clase en una Entity de JPA agregando:
 * - @Entity
 * - @Table(name = "cursos")
 * - @Id + @GeneratedValue(strategy = GenerationType.IDENTITY) sobre el id
 * - @Column(nullable = false) sobre el nombre
 *
 * CLASE 6 (CRUD + Validaciones): descomentar las anotaciones de validacion
 * (jakarta.validation) y el campo imagenUrl cuando lo necesitemos.
 */

@Entity
@Table(name = "cursos")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // CLASE 6 - PARTE A.4: descomentar @NotBlank y @Size
    // @NotBlank(message = "El nombre es obligatorio")
    // @Size(max = 100, message = "El nombre no puede tener mas de 100 caracteres")
    @Column(nullable = false)
    private String nombre;

    // CLASE 6 - PARTE A.4: descomentar @Size
    // @Size(max = 500, message = "La descripcion no puede tener mas de 500 caracteres")
    private String descripcion;

    // CLASE 6 - PARTE A.4: descomentar @Min y @Max
    // @Min(value = 1, message = "Los creditos deben ser al menos 1")
    // @Max(value = 8, message = "Los creditos no pueden superar los 8")
    private int creditos;

    // CLASE 6 - PARTE A.4: descomentar @NotBlank y @Size
    // @NotBlank(message = "El profesor es obligatorio")
    // @Size(max = 80)
    private String profesor;

    // CLASE 6 - PARTE E (bonus Firebase): descomentar el campo imagenUrl
    // private String imagenUrl;

    /** Constructor vacio - obligatorio cuando esta clase pase a ser @Entity. */
    public Curso() {
    }

    public Curso(Long id, String nombre, String descripcion, int creditos, String profesor) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.creditos = creditos;
        this.profesor = profesor;
    }

    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCreditos() {
        return creditos;
    }

    public void setCreditos(int creditos) {
        this.creditos = creditos;
    }

    public String getProfesor() {
        return profesor;
    }

    public void setProfesor(String profesor) {
        this.profesor = profesor;
    }

    // CLASE 6 - PARTE E (bonus Firebase): descomentar el getter/setter de imagenUrl
    /*
    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }
    */
}
