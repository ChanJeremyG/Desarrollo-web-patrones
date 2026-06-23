package com.ufide.primerproyecto;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model modelo, @RequestParam String nombre) {
        modelo.addAttribute("nombre", nombre.toUpperCase());
        return "home";
    }
}