package edu.tecnm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String mostrarInicio() {
    	return "redirect:/productos";
    }
    
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }
}