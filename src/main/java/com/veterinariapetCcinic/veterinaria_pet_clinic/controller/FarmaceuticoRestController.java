package com.veterinariapetCcinic.veterinaria_pet_clinic.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.veterinariapetCcinic.veterinaria_pet_clinic.model.Medicamento;
import com.veterinariapetCcinic.veterinaria_pet_clinic.model.RecetaEstado;
import com.veterinariapetCcinic.veterinaria_pet_clinic.model.RecetaMedica;
import com.veterinariapetCcinic.veterinaria_pet_clinic.repository.MedicamentoRepository;
import com.veterinariapetCcinic.veterinaria_pet_clinic.repository.RecetaMedicaRepository;
import com.veterinariapetCcinic.veterinaria_pet_clinic.service.FarmaceuticoService;

@RestController
@RequestMapping("/api/farmaceutico")
public class FarmaceuticoRestController {

    private final FarmaceuticoService farmaceuticoService;
    private final MedicamentoRepository medicamentoRepository;
    private final RecetaMedicaRepository recetaRepository;

    public FarmaceuticoRestController(
            FarmaceuticoService farmaceuticoService,
            MedicamentoRepository medicamentoRepository,
            RecetaMedicaRepository recetaRepository) {

        this.farmaceuticoService = farmaceuticoService;
        this.medicamentoRepository = medicamentoRepository;
        this.recetaRepository = recetaRepository;
    }

    // ===========================
    // ESTADÍSTICAS
    // ===========================

    @GetMapping("/stats")
    public Map<String, Object> getStats() {

        Map<String, Object> stats = new HashMap<>();

        stats.put("recetasPendientes",
                farmaceuticoService.contarRecetasPendientes());

        stats.put("stockBajo",
                farmaceuticoService.contarStockBajo());

        stats.put("dispensados", 0L);
        stats.put("ventasHoy", 0.0);

        return stats;
    }

    // ===========================
    // RECETAS
    // ===========================

    @GetMapping("/recetas/pendientes")
    public List<RecetaMedica> getRecetasPendientes() {
        return recetaRepository.findByEstado(RecetaEstado.PENDIENTE);
    }

    @GetMapping("/recetas/verificar/{id}")
    public Map<String, Object> verificarReceta(@PathVariable Long id) {

        var validacion = farmaceuticoService.validarReceta(id);

        Map<String, Object> resultado = new HashMap<>();

        resultado.put("valida", validacion.valida());
        resultado.put("errores", validacion.errores());
        resultado.put("advertencias", validacion.advertencias());
        resultado.put("alternativas",
                validacion.alternativasPorMedicamento());

        return resultado;
    }

    @PostMapping("/recetas/dispensar/{id}")
    public Map<String, Object> dispensarReceta(@PathVariable Long id) {

        var resultado = farmaceuticoService.dispensarReceta(id);

        Map<String, Object> response = new HashMap<>();

        response.put("dispensado", resultado.dispensado());
        response.put("errores", resultado.errores());
        response.put("advertencias", resultado.advertencias());

        return response;
    }

    // ===========================
    // MEDICAMENTOS
    // ===========================

    @GetMapping("/medicamentos")
    public List<Medicamento> getMedicamentos(
            @RequestParam(required = false) String search) {

        if (search != null && !search.isEmpty()) {
            return medicamentoRepository
                    .findByNombreContainingIgnoreCase(search);
        }

        return medicamentoRepository.findAll();
    }

    @GetMapping("/medicamentos/stock-bajo")
    public List<Medicamento> getMedicamentosStockBajo() {
        return medicamentoRepository.findByStockLessThan(5);
    }

    // ===========================
    // ACTUALIZAR STOCK
    // ===========================

    @PutMapping("/medicamentos/{id}/stock")
    public Map<String, Object> actualizarStock(
            @PathVariable Long id,
            @RequestParam Integer stock) {

        Map<String, Object> response = new HashMap<>();

        try {

            Medicamento med = medicamentoRepository.findById(id)
                    .orElseThrow(() ->
                            new RuntimeException("Medicamento no encontrado"));

            med.setStock(stock);

            medicamentoRepository.save(med);

            response.put("success", true);
            response.put("message",
                    "Stock actualizado correctamente");

            response.put("medicamento", med);

        } catch (Exception e) {

            response.put("success", false);
            response.put("message", e.getMessage());

        }

        return response;
    }

    // ===========================
    // AGREGAR MEDICAMENTO
    // ===========================

    @PostMapping("/medicamentos")
    public Medicamento agregarMedicamento(
            @RequestBody Medicamento medicamento) {

        return medicamentoRepository.save(medicamento);
    }

    // ===========================
    // VENTAS
    // ===========================

    @PostMapping("/ventas")
    public Map<String, Object> registrarVenta(
            @RequestBody VentaRequest venta) {

        Medicamento med = medicamentoRepository
                .findById(venta.medicamentoId)
                .orElseThrow();

        if (med.getStock() < venta.cantidad) {
            throw new RuntimeException("Stock insuficiente");
        }

        med.setStock(med.getStock() - venta.cantidad);

        medicamentoRepository.save(med);

        Map<String, Object> response = new HashMap<>();

        response.put("exito", true);

        response.put("total",
                med.getPrecio()
                        .multiply(BigDecimal.valueOf(venta.cantidad)));

        response.put("medicamento", med.getNombre());

        response.put("cantidad", venta.cantidad);

        return response;
    }

    // ===========================
    // RECORD
    // ===========================

    public record VentaRequest(
            Long medicamentoId,
            Integer cantidad,
            String cliente,
            String pago) {
    }
}