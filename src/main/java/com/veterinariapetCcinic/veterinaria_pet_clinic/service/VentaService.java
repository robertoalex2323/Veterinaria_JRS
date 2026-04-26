package com.veterinariapetCcinic.veterinaria_pet_clinic.service;

import com.veterinariapetCcinic.veterinaria_pet_clinic.model.Venta;
import org.springframework.stereotype.Service;

@Service
public class VentaService {

    public Venta procesarVenta(Venta venta) {
        if (venta.getPrecioUnitario() != null && venta.getCantidad() != null) {
            venta.setTotal(venta.getPrecioUnitario() * venta.getCantidad());
        }
        return venta; 
    }
}