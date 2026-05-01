package com.veterinariapetCcinic.veterinaria_pet_clinic.repository;

import com.veterinariapetCcinic.veterinaria_pet_clinic.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
}
