package com.veterinariapetCcinic.veterinaria_pet_clinic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.veterinariapetCcinic.veterinaria_pet_clinic.model.Medicamento;

public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {
    List<Medicamento> findByNombreContainingIgnoreCase(String nombre);
    List<Medicamento> findByPresentacion(String presentacion);
    List<Medicamento> findByStockLessThan(Integer stock);
}