package com.veterinariapetCcinic.veterinaria_pet_clinic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.veterinariapetCcinic.veterinaria_pet_clinic.service.FarmaceuticoService;

@Controller
public class FarmaceuticoController {

    private final FarmaceuticoService farmaceuticoService;

    public FarmaceuticoController(FarmaceuticoService farmaceuticoService) {
        this.farmaceuticoService = farmaceuticoService;
    }

    @GetMapping("/farmaceutico")
    public String farmaceuticoPanel(
            @RequestParam(value = "recetaId", required = false) Long recetaId,
            Model model) {

        model.addAttribute("recetasPendientes", farmaceuticoService.obtenerRecetasPendientes());
        model.addAttribute("stockBajo", farmaceuticoService.contarStockBajo());
        model.addAttribute("medicamentosStockBajo", farmaceuticoService.listarMedicamentosBajoStock());

        if (recetaId != null) {
            model.addAttribute("validacion", farmaceuticoService.validarReceta(recetaId));
        }
        return "farmaceutico";
    }

    @PostMapping("/farmaceutico/dispensar")
    public String dispensarReceta(@RequestParam("recetaId") Long recetaId,
                                  RedirectAttributes redirectAttributes) {
        var resultado = farmaceuticoService.dispensarReceta(recetaId);
        redirectAttributes.addFlashAttribute("dispensado", resultado.dispensado());
        redirectAttributes.addFlashAttribute("errores", resultado.errores());
        redirectAttributes.addFlashAttribute("advertencias", resultado.advertencias());
        return "redirect:/farmaceutico?recetaId=" + recetaId;
    }
}