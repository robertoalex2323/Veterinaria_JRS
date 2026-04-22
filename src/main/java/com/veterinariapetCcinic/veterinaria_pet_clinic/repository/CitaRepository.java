package com.veterinariapetCcinic.veterinaria_pet_clinic.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.veterinariapetCcinic.veterinaria_pet_clinic.Model.Cita;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
    
    List<Cita> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);
    
    List<Cita> findByEstado(String estado);
    
    List<Cita> findByMascotaId(Long mascotaId);
    
    @Query("SELECT c FROM Cita c WHERE DATE(c.fechaHora) = :fecha")
    List<Cita> findCitasByFecha(@Param("fecha") LocalDate fecha);
    
    @Query("SELECT c FROM Cita c WHERE c.mascota.cliente.id = :clienteId")
    List<Cita> findCitasByClienteId(@Param("clienteId") Long clienteId);
    
    @Query("SELECT c FROM Cita c WHERE c.mascota.cliente.telefono = :telefono")
    List<Cita> findCitasByClienteTelefono(@Param("telefono") String telefono);
    
    long countByFechaHoraBetweenAndEstado(LocalDateTime inicio, LocalDateTime fin, String estado);
    
    @Query("SELECT c FROM Cita c WHERE c.estado = 'AGENDADA' AND c.fechaHora > :ahora")
    List<Cita> findCitasPendientes(@Param("ahora") LocalDateTime ahora);
    
    @Query("SELECT c FROM Cita c WHERE c.estado = 'AGENDADA' AND DATE(c.fechaHora) = :fecha")
    List<Cita> findCitasPendientesByFecha(@Param("fecha") LocalDate fecha);
}