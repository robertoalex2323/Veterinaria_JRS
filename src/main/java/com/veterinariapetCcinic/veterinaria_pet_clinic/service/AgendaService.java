package com.veterinariapetCcinic.veterinaria_pet_clinic.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.veterinariapetCcinic.veterinaria_pet_clinic.Model.Agenda;
import com.veterinariapetCcinic.veterinaria_pet_clinic.config.AppProperties;
import com.veterinariapetCcinic.veterinaria_pet_clinic.repository.AgendaRepository;

@Service
public class AgendaService {

    private final AgendaRepository agendaRepository;
    private final AppProperties appProperties;

    public AgendaService(AgendaRepository agendaRepository, AppProperties appProperties) {
        this.agendaRepository = agendaRepository;
        this.appProperties = appProperties;
    }

    @Transactional
    public Agenda guardar(Agenda agenda) {
        Objects.requireNonNull(agenda, "La agenda no puede ser nula");
        return Objects.requireNonNull(agendaRepository.save(agenda));
    }

    public List<Agenda> obtenerHorariosDelDia(LocalDate fecha) {
        return agendaRepository.findByFecha(fecha);
    }

    public List<Agenda> obtenerHorariosDisponibles(LocalDate fecha) {
        return agendaRepository.findHorariosDisponiblesByFecha(fecha);
    }

    public List<Agenda> obtenerAgendaSemanal(LocalDate inicio) {
        LocalDate fin = inicio.plusDays(6);
        return agendaRepository.findByFechaBetween(inicio, fin);
    }

    @Transactional
    public void bloquearHorario(Long id) {
        Objects.requireNonNull(id, "El ID no puede ser nulo");
        Agenda agenda = agendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));
        agenda.setDisponible(false);
        agendaRepository.save(agenda);
    }

    @Transactional
    public void liberarHorario(Long id) {
        Objects.requireNonNull(id, "El ID no puede ser nulo");
        Agenda agenda = agendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));
        agenda.setDisponible(true);
        agendaRepository.save(agenda);
    }

    public Agenda buscarAgendaDisponible(LocalDate fecha, LocalTime hora) {
        List<Agenda> agendas = agendaRepository.findAgendaDisponiblePorFechaYHora(fecha, hora);
        if (!agendas.isEmpty()) {
            return agendas.get(0);
        }
        return null;
    }

    public Agenda buscarAgendaPorFechaYHora(LocalDate fecha, LocalTime hora) {
        List<Agenda> agendas = agendaRepository.findAgendaPorFechaYHora(fecha, hora);
        if (!agendas.isEmpty()) {
            return agendas.get(0);
        }
        return null;
    }

    @Transactional
    public void generarAgendaBaseSiVacia(
            com.veterinariapetCcinic.veterinaria_pet_clinic.Model.Usuario veterinarioDefecto) {
        if (agendaRepository.count() == 0) {
            LocalDate hoy = LocalDate.now();
            for (int i = 0; i < 30; i++) {
                LocalDate fecha = hoy.plusDays(i);
                // Excluir domingos
                if (fecha.getDayOfWeek() == java.time.DayOfWeek.SUNDAY)
                    continue;

                LocalTime cursor = LocalTime.parse(appProperties.getBusiness().getStartTime());
                LocalTime fin = LocalTime.parse(appProperties.getBusiness().getEndTime());
                int duracion = appProperties.getBusiness().getDefaultSlotDuration();

                while (cursor.plusMinutes(duracion).compareTo(fin) <= 0) {
                    Agenda agenda = new Agenda();
                    agenda.setFecha(fecha);
                    agenda.setHoraInicio(cursor);
                    agenda.setHoraFin(cursor.plusMinutes(duracion));
                    agenda.setDuracionTurno(duracion);
                    agenda.setDisponible(true);
                    agenda.setVeterinario(veterinarioDefecto);
                    agendaRepository.save(agenda);
                    cursor = cursor.plusMinutes(duracion);
                }
            }
        }
    }
}