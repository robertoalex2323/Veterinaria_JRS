package com.veterinariapetCcinic.veterinaria_pet_clinic.service;

import com.veterinariapetCcinic.veterinaria_pet_clinic.model.Venta;
import com.veterinariapetCcinic.veterinaria_pet_clinic.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.LinkedHashMap;
import java.time.LocalDateTime;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;

    public VentaService(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

    public Venta procesarVenta(Venta venta) {
        if (venta.getPrecioUnitario() != null && venta.getCantidad() != null) {
            venta.setTotal(venta.getPrecioUnitario() * venta.getCantidad());
        }
        if (venta.getFechaVenta() == null) {
            venta.setFechaVenta(LocalDateTime.now());
        }
        return ventaRepository.save(venta);
    }

    public Map<String, Object> generarBoletaDigital(Long id) {
        Venta venta = ventaRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venta no encontrada con ID: " + id));

        Map<String, Object> boleta = new LinkedHashMap<>();
        
        boleta.put("id_venta", venta.getId());
        boleta.put("fecha_emision", LocalDateTime.now());
        boleta.put("empresa", "Veterinaria Pet Clinic");
        boleta.put("producto", venta.getProductoNombre());
        boleta.put("cantidad", venta.getCantidad());
        boleta.put("total_pagado", venta.getTotal());
        boleta.put("estado", "PAGADO");
        boleta.put("mensaje", "Gracias por su compra en Pet Clinic 2026");
    
        return boleta;
    }
}