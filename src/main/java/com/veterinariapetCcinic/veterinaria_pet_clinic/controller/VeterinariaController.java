package com.veterinariapetCcinic.veterinaria_pet_clinic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/veterinaria")
public class VeterinariaController {

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("currentPage", "dashboard");
        return "veterinaria/dashboard";
    }

    @GetMapping("/pacientes")
    public String pacientes(Model model) {
        model.addAttribute("currentPage", "pacientes");
        return "veterinaria/pacientes";
    }

    @GetMapping("/consultas")
    public String consultas(Model model) {
        model.addAttribute("currentPage", "consultas");
        return "veterinaria/consultas";
    }

    @GetMapping("/agenda")
    public String agenda(Model model) {
        model.addAttribute("currentPage", "agenda");
        return "veterinaria/agenda";
    }

    @GetMapping("/historial")
    public String historial(Model model) {
        model.addAttribute("currentPage", "historial");
        return "veterinaria/historial";
    }

    @GetMapping("/vacunas")
    public String vacunas(Model model) {
        model.addAttribute("currentPage", "vacunas");
        return "veterinaria/vacunas";
    }

    @GetMapping("/alertas")
    public String alertas(Model model) {
        model.addAttribute("currentPage", "alertas");
        return "veterinaria/alertas";
    }

    @GetMapping("/diagnostico-ia")
    public String diagnosticoIA(Model model) {
        model.addAttribute("currentPage", "diagnostico");
        return "veterinaria/diagnostico-ia";
    }

    @GetMapping("/perfil")
    public String perfil(Model model) {
        model.addAttribute("currentPage", "perfil");
        return "veterinaria/perfil";
    }
}