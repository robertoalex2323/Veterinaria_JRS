package com.veterinariapetCcinic.veterinaria_pet_clinic.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.veterinariapetCcinic.veterinaria_pet_clinic.Model.Cita;
import com.veterinariapetCcinic.veterinaria_pet_clinic.repository.CitaRepository;

@Service
public class CitaService {
    
    private static final Logger log = LoggerFactory.getLogger(CitaService.class);

    private final CitaRepository citaRepository;
    private final NotificacionService notificacionService;
    private final AgendaService agendaService;
    
    public CitaService(CitaRepository citaRepository, NotificacionService notificacionService, AgendaService agendaService) {
        this.citaRepository = citaRepository;
        this.notificacionService = notificacionService;
        this.agendaService = agendaService;
    }
    
    @Transactional
    public Cita guardar(Cita cita) {
        validarDisponibilidad(cita.getFechaHora());
        
        // Bloquear horario en la agenda automáticamente
        com.veterinariapetCcinic.veterinaria_pet_clinic.Model.Agenda agenda = agendaService.buscarAgendaDisponible(cita.getFechaHora().toLocalDate(), cita.getFechaHora().toLocalTime());
        if (agenda != null) {
            agendaService.bloquearHorario(agenda.getId());
        }
        
        Cita citaGuardada = citaRepository.save(cita);
        log.info("Cita agendada exitosamente: ID {} para mascota {}", citaGuardada.getId(), citaGuardada.getMascota().getNombre());
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
        log.info("Cita ID {} confirmada por el veterinario", id);
        notificacionService.enviarNotificacionVeterinario(cita);
        return citaRepository.save(cita);
    }
    
    @Transactional
    public void cancelarCita(Long id, String motivo) {
        Cita cita = buscarPorId(id);
        cita.setEstado("CANCELADA");
        cita.setObservaciones("Cancelada: " + motivo);
        citaRepository.save(cita);
        log.warn("Cita ID {} cancelada. Motivo: {}", id, motivo);
        
        // Liberar horario en la agenda automáticamente
        com.veterinariapetCcinic.veterinaria_pet_clinic.Model.Agenda agenda = agendaService.buscarAgendaPorFechaYHora(cita.getFechaHora().toLocalDate(), cita.getFechaHora().toLocalTime());
        if (agenda != null) {
            agendaService.liberarHorario(agenda.getId());
        }
        
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
    
    private void validarDisponibilidad(LocalDateTime fechaHora) {
        // 1. Validar horarios lógicos (no pasado)
        if (fechaHora.isBefore(LocalDateTime.now())) {
            log.error("Intento de agendamiento fallido: Fecha pasada {}", fechaHora);
            throw new RuntimeException("No se pueden agendar citas en fechas u horas del pasado.");
        }
        
        // 2. Validar el horario de atención (ej. 08:00 a 20:00)
        LocalTime hora = fechaHora.toLocalTime();
        if (hora.isBefore(LocalTime.of(8, 0)) || hora.isAfter(LocalTime.of(20, 0))) {
            throw new RuntimeException("La cita debe estar dentro del horario de atención (08:00 - 20:00).");
        }
        
        // 3. Vincular con Agenda (verificar si el horario existe y está disponible)
        com.veterinariapetCcinic.veterinaria_pet_clinic.Model.Agenda agenda = agendaService.buscarAgendaDisponible(fechaHora.toLocalDate(), hora);
        if (agenda == null) {
            log.warn("Horario no disponible en agenda para: {}", fechaHora);
            throw new RuntimeException("El horario seleccionado no existe en la agenda o ya no está disponible.");
        }

        // Inteligencia: Validar cruces usando la duración REAL del turno definido en la agenda
        int duracion = (agenda.getDuracionTurno() != null) ? agenda.getDuracionTurno() : 30;
        LocalDateTime inicio = fechaHora.minusMinutes(duracion - 1);
        LocalDateTime fin = fechaHora.plusMinutes(duracion - 1);

        long cantidad = citaRepository.countByFechaHoraBetweenAndEstado(inicio, fin, "AGENDADA");
        if (cantidad > 0) {
            log.warn("Intento de agendamiento fallido: Cruce de horario en {} para duración de {} min", fechaHora, duracion);
            throw new RuntimeException("Ya existe una cita agendada cerca de este horario (cruce de 30 minutos).");
        }
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
