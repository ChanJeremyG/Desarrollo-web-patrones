package com.ufide.biblioapp.service;

import com.ufide.biblioapp.entity.Libro;
import com.ufide.biblioapp.entity.Prestamo;
import com.ufide.biblioapp.entity.Rol;
import com.ufide.biblioapp.entity.Usuario;
import com.ufide.biblioapp.repository.PrestamoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PrestamoService {

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    private LibroService libroService;

    @Autowired
    private UsuarioService usuarioService;

    public List<Prestamo> listarTodos() {
        return prestamoRepository.findAllConLibroYUsuario();
    }

    public List<Prestamo> listarPorUsuario(Usuario usuario) {
        return prestamoRepository.findByUsuario(usuario);
    }

    // REQUISITO 5: la consulta JPQL prestamosAtrasados() conectada a la API
    // y a la vista del dashboard del bibliotecario.
    public List<Prestamo> prestamosAtrasados() {
        return prestamoRepository.prestamosAtrasados();
    }

    // REQUISITO 2: registrar un prestamo descuenta una copia disponible y
    // calcula la fecha limite (14 dias despues de la fecha del prestamo).
    @Transactional
    public Prestamo registrar(Prestamo prestamo) {
        Libro libro = prestamo.getLibro();
        if (libro == null || libro.getId() == null) {
            throw new IllegalArgumentException("Debe seleccionar un libro");
        }
        Usuario usuario = prestamo.getUsuario();
        if (usuario == null || usuario.getId() == null) {
            throw new IllegalArgumentException("Debe seleccionar un usuario");
        }

        // Se recargan las entidades completas a partir del id que envio el form
        libro = libroService.buscarPorId(libro.getId())
                .orElseThrow(() -> new IllegalArgumentException("El libro seleccionado no existe"));
        usuario = usuarioService.buscarPorId(usuario.getId())
                .orElseThrow(() -> new IllegalArgumentException("El usuario seleccionado no existe"));

        if (libro.getCopiasDisponibles() <= 0) {
            throw new IllegalStateException("No hay copias disponibles de \"" + libro.getTitulo() + "\"");
        }

        prestamo.setLibro(libro);
        prestamo.setUsuario(usuario);
        if (prestamo.getFechaLimite() == null) {
            prestamo.setFechaLimite(prestamo.getFechaPrestamo().plusDays(14));
        }

        libroService.descontarCopia(libro);
        return prestamoRepository.save(prestamo);
    }

    // REQUISITO 2: marcar una devolucion suma la copia de vuelta y guarda
    // la fecha de devolucion.
    @Transactional
    public Prestamo devolver(Long id) {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El prestamo no existe"));
        if (prestamo.getFechaDevolucion() == null) {
            prestamo.setFechaDevolucion(LocalDate.now());
            libroService.devolverCopia(prestamo.getLibro());
        }
        return prestamoRepository.save(prestamo);
    }
}