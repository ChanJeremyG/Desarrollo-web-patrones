package com.ufide.biblioapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
// REQUISITO 3: @EnableMethodSecurity activa los @PreAuthorize que estan en
// los controllers. Sin esta anotacion se ignoran silenciosamente.
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // La API REST se consume desde Postman (sin CSRF token), por eso se
            // desactiva CSRF para /api/** - igual que en el proyecto de ejemplo.
            // Las rutas HTML (formularios) siguen protegidas por CSRF.
            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
            .authorizeHttpRequests(auth -> auth
                // Publico: catalogo de libros (GET), login, pagina 403 y la
                // API de consulta de libros. POST /api/libros queda publico a
                // nivel de URL pero lo protege el @PreAuthorize del metodo.
                .requestMatchers("/libros", "/libros/**", "/login", "/403", "/css/**", "/js/**",
                                 "/api/libros", "/api/libros/**").permitAll()
                // Todo lo demas (prestamos, API de prestamos) requiere estar
                // logueado. La restriccion POR ROL vive en @PreAuthorize.
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/libros", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            // REQUISITO 3: si un usuario autenticado no tiene el rol necesario,
            // Spring Security lanza AccessDeniedException y lo redirige a /403.
            .exceptionHandling(ex -> ex.accessDeniedPage("/403"));

        return http.build();
    }
}