package com.ufide.eventapp.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufide.eventapp.entity.Evento;
import com.ufide.eventapp.repository.EventoRepository;

/**
 * Service de eventos.
 *
 * Ya viene con los metodos basicos. Si necesitas mas operaciones
 * (por ejemplo para tu endpoint con parametro), agregalas aca y NO en el
 * Controller (recordar el patron MVC: Controller llama a Service, no al Repository).
 */
@Service
public class EventoService {

    @Autowired
    private EventoRepository repo;

    public List<Evento> listar() {
        return repo.findAll();
    }

    public Optional<Evento> buscarPorId(Long id) {
        return repo.findById(id);
    }

    public Evento guardar(Evento e) {
        return repo.save(e);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    public List<Evento> buscarPorCategoria(String categoria) {
        return repo.findByCategoria(categoria);
    }

    public List<Evento> buscarProximos() {
        return repo.findByFechaGreaterThanEqualOrderByFechaAsc(LocalDate.now());
    }
}
