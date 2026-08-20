package com.ufide.biblioapp.service;

import com.ufide.biblioapp.entity.Rol;
import com.ufide.biblioapp.entity.Usuario;
import com.ufide.biblioapp.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // REQUISITO 3: el rol viene del enum (Rol), nunca de un String suelto.
        // Spring Security necesita el prefijo "ROLE_" para que hasRole() funcione.
        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())))
                .build();
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    // Solo usuarios LECTOR, para el formulario de prestamo (el bibliotecario
    // le presta libros a los lectores). Filtra contra el enum, no contra strings.
    public List<Usuario> listarLectores() {
        return usuarioRepository.findAll().stream()
                .filter(u -> u.getRol() == Rol.LECTOR)
                .toList();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username).orElse(null);
    }
}