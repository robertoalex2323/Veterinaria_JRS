package com.veterinariapetCcinic.veterinaria_pet_clinic.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.veterinariapetCcinic.veterinaria_pet_clinic.Model.Agenda;
import com.veterinariapetCcinic.veterinaria_pet_clinic.service.AgendaService;

@Controller
@RequestMapping("/recepcionista/agenda")
public class RecepcionistaAgendaController {

    private final AgendaService agendaService;

    public RecepcionistaAgendaController(AgendaService agendaService) {
        this.agendaService = agendaService;
    }

    @GetMapping("/horarios")
    public String horarios(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            Model model) {
        if (fecha == null) {
            fecha = LocalDate.now();
        }

        List<Agenda> agendas = agendaService.obtenerHorariosDelDia(fecha);
        agendas.sort(Comparator.comparing(Agenda::getHoraInicio, Comparator.nullsLast(Comparator.naturalOrder())));

        model.addAttribute("fecha", fecha);
        model.addAttribute("agendas", agendas);
        return "recepcionista/agenda-horarios";
    }

    @PostMapping("/horarios/generar")
    public String generarHorarios(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaFin,
            @RequestParam Integer duracionTurno,
            RedirectAttributes redirectAttributes) {
        try {
            if (duracionTurno == null || duracionTurno <= 0) {
                throw new IllegalArgumentException("La duración del turno debe ser mayor a 0.");
            }
            if (!horaFin.isAfter(horaInicio)) {
                throw new IllegalArgumentException("La hora fin debe ser mayor a la hora inicio.");
            }

            LocalTime cursor = horaInicio;
            int creados = 0;

            while (cursor.plusMinutes(duracionTurno).compareTo(horaFin) <= 0) {
                LocalTime slotFin = cursor.plusMinutes(duracionTurno);

                Agenda existente = agendaService.buscarAgendaPorFechaYHora(fecha, cursor);
                if (existente == null) {
                    Agenda agenda = new Agenda();
                    agenda.setFecha(fecha);
                    agenda.setHoraInicio(cursor);
                    agenda.setHoraFin(slotFin);
                    agenda.setDuracionTurno(duracionTurno);
                    agenda.setDisponible(true);
                    agendaService.guardar(agenda);
                    creados++;
                }

                cursor = slotFin;
            }

            redirectAttributes.addFlashAttribute("success",
                    "Horarios generados: " + creados + ". Fecha: " + fecha);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/recepcionista/agenda/horarios?fecha=" + fecha;
    }

    @PostMapping("/horarios/bloquear/{id}")
    public String bloquear(@PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            RedirectAttributes redirectAttributes) {
        try {
            agendaService.bloquearHorario(id);
            redirectAttributes.addFlashAttribute("success", "Horario bloqueado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/recepcionista/agenda/horarios?fecha=" + fecha;
    }

    @PostMapping("/horarios/liberar/{id}")
    public String liberar(@PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            RedirectAttributes redirectAttributes) {
        try {
            agendaService.liberarHorario(id);
            redirectAttributes.addFlashAttribute("success", "Horario liberado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/recepcionista/agenda/horarios?fecha=" + fecha;
    }

    @GetMapping("/api/disponibles")
    public ResponseEntity<List<Map<String, String>>> disponibles(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        List<Map<String, String>> payload = agendaService.obtenerHorariosDisponibles(fecha).stream()
                .sorted(Comparator.comparing(Agenda::getHoraInicio, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(a -> Map.of(
                        "id", String.valueOf(a.getId()),
                        "horaInicio", a.getHoraInicio() != null ? a.getHoraInicio().format(formatter) : "",
                        "horaFin", a.getHoraFin() != null ? a.getHoraFin().format(formatter) : ""))
                .toList();
        return ResponseEntity.ok(payload);
    }
}

