package com.veterinariapetCcinic.veterinaria_pet_clinic.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.veterinariapetCcinic.veterinaria_pet_clinic.Model.Cita;
import com.veterinariapetCcinic.veterinaria_pet_clinic.repository.CitaRepository;

@Service
public class CitaService {
    
    private final CitaRepository citaRepository;
    private final NotificacionService notificacionService;
    
    public CitaService(CitaRepository citaRepository, NotificacionService notificacionService) {
        this.citaRepository = citaRepository;
        this.notificacionService = notificacionService;
    }
    
    @Transactional
    public Cita guardar(Cita cita) {
        if (!validarDisponibilidad(cita.getFechaHora())) {
            throw new RuntimeException("El horario no está disponible");
        }
        Cita citaGuardada = citaRepository.save(cita);
        notificacionService.enviarConfirmacionCita(citaGuardada);
        return citaGuardada;
    }
    
    @Transactional
    public Cita actualizar(Cita cita) {
        return citaRepository.save(cita);
    }
    
    @Transactional
    public Cita confirmarCita(Long id) {
        Cita cita = buscarPorId(id);
        cita.setEstado("CONFIRMADA");
        notificacionService.enviarNotificacionVeterinario(cita);
        return citaRepository.save(cita);
    }
    
    @Transactional
    public void cancelarCita(Long id, String motivo) {
        Cita cita = buscarPorId(id);
        cita.setEstado("CANCELADA");
        cita.setObservaciones("Cancelada: " + motivo);
        citaRepository.save(cita);
        notificacionService.enviarCancelacionCita(cita);
    }
    
    public Cita buscarPorId(Long id) {
        return citaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + id));
    }
    
    public List<Cita> listarTodas() {
        return citaRepository.findAll();
    }
    
    public List<Cita> obtenerCitasDelDia(LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.atTime(LocalTime.MAX);
        return citaRepository.findByFechaHoraBetween(inicio, fin);
    }
    
    public List<Cita> obtenerCitasPorEstado(String estado) {
        return citaRepository.findByEstado(estado);
    }
    
    public List<Cita> obtenerCitasPorCliente(Long clienteId) {
        return citaRepository.findCitasByClienteId(clienteId);
    }
    
    public List<Cita> obtenerCitasPendientes() {
        return citaRepository.findCitasPendientes(LocalDateTime.now());
    }
    
    public List<Cita> listarCitasPendientesPago() {
        return citaRepository.findByEstado("ATENDIDA");
    }
    
    private boolean validarDisponibilidad(LocalDateTime fechaHora) {
        LocalDateTime inicio = fechaHora.minusMinutes(30);
        LocalDateTime fin = fechaHora.plusMinutes(30);
        long cantidad = citaRepository.countByFechaHoraBetweenAndEstado(inicio, fin, "AGENDADA");
        return cantidad == 0;
    }
    
    public long contarCitasHoy() {
        return obtenerCitasDelDia(LocalDate.now()).size();
    }
    
    public long contarCitasConfirmadas() {
        return citaRepository.findByEstado("CONFIRMADA").size();
    }
    
    @Transactional
    public void eliminar(Long id) {
        citaRepository.deleteById(id);
    }

}
