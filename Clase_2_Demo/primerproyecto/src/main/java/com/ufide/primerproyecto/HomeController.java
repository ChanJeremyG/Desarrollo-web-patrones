package com.ufide.primerproyecto;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model modelo, @RequestParam String studentName) {
        modelo.addAttribute("nombre", studentName);
        List<String> cursos = List.of(
                "Java",
                "SpringBoot",
                "Web");

        modelo.addAttribute("cursos", cursos);

        return "home";
    }
}