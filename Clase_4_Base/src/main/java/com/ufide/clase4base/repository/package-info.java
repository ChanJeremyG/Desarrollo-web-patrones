/**
 * Capa Repository - acceso a la base de datos.
 *
 * En clase 4 se crea aqui:
 *
 *   CursoRepository.java
 *     package com.ufide.clase4base.repository;
 *
 *     import org.springframework.data.jpa.repository.JpaRepository;
 *     import com.ufide.clase4base.entity.Curso;
 *
 *     public interface CursoRepository
 *           extends JpaRepository<Curso, Long> {
 *     }
 *
 * Reglas:
 *  - Es una INTERFACE, no una clase.
 *  - Extiende JpaRepository<Entidad, TipoDelId>.
 *  - No se implementa - Spring lo hace solo en tiempo de arranque.
 *  - Solo deberia hablar con la base de datos. Cero logica de negocio.
 */
package com.ufide.clase4base.repository;
