package com.ufide.primerproyecto;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("cursos")
public class CursosController {

    @GetMapping
    public String ListarCursos(Model modelo) {
        modelo.addAttribute("cursos", new String[] { "Java", "Python", "JavaScript" });
        return "cursos";
    }

    @GetMapping("/{cursoId}")
    public String getMethodName(Model modelo, @PathVariable String cursoId) {
        modelo.addAttribute("cursoId", cursoId);
        return "curso";
    }

}
