package com.veterinariapetCcinic.veterinaria_pet_clinic.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.veterinariapetCcinic.veterinaria_pet_clinic.Model.Cita;
import com.veterinariapetCcinic.veterinaria_pet_clinic.Model.Consulta;
import com.veterinariapetCcinic.veterinaria_pet_clinic.Model.Mascota;
import com.veterinariapetCcinic.veterinaria_pet_clinic.Model.SignosVitales;
import com.veterinariapetCcinic.veterinaria_pet_clinic.Model.Usuario;
import com.veterinariapetCcinic.veterinaria_pet_clinic.repository.CitaRepository;
import com.veterinariapetCcinic.veterinaria_pet_clinic.repository.UsuarioRepository;
import com.veterinariapetCcinic.veterinaria_pet_clinic.service.CitaService;
import com.veterinariapetCcinic.veterinaria_pet_clinic.service.ConsultaService;
import com.veterinariapetCcinic.veterinaria_pet_clinic.service.MascotaService;
import com.veterinariapetCcinic.veterinaria_pet_clinic.service.SignosVitalesService;

@Controller
@RequestMapping("/veterinaria")
public class VeterinariaController {

    private final CitaRepository citaRepository;
    private final CitaService citaService;
    private final MascotaService mascotaService;
    private final ConsultaService consultaService;
    private final SignosVitalesService signosVitalesService;
    private final UsuarioRepository usuarioRepository;

    public VeterinariaController(CitaRepository citaRepository,
                                  CitaService citaService,
                                  MascotaService mascotaService,
                                  ConsultaService consultaService,
                                  SignosVitalesService signosVitalesService,
                                  UsuarioRepository usuarioRepository) {
        this.citaRepository = citaRepository;
        this.citaService = citaService;
        this.mascotaService = mascotaService;
        this.consultaService = consultaService;
        this.signosVitalesService = signosVitalesService;
        this.usuarioRepository = usuarioRepository;
    }

    private String getUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "Veterinario";
    }

    private Usuario getUsuarioActual() {
        return usuarioRepository.findByUsername(getUsername()).orElse(null);
    }

    // ============ DASHBOARD ============
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("currentPage", "dashboard");
        usuarioRepository.findByUsername(getUsername()).ifPresent(u ->
            model.addAttribute("nombreUsuario", u.getNombre()));

        LocalDate hoy = LocalDate.now();
        LocalDateTime inicio = hoy.atStartOfDay();
        LocalDateTime fin = hoy.atTime(LocalTime.MAX);

        List<Mascota> mascotas = mascotaService.listarTodosConCliente();
        mascotas.sort((a, b) -> Long.compare(
                b.getId() != null ? b.getId() : 0L,
                a.getId() != null ? a.getId() : 0L));

        List<Cita> citasHoy = citaRepository.findCitasDelDiaConDatos(inicio, fin);
        Map<Long, Cita> citaHoyPorMascota = new HashMap<>();
        for (Cita cita : citasHoy) {
            if (cita.getMascota() != null && cita.getMascota().getId() != null) {
                citaHoyPorMascota.put(cita.getMascota().getId(), cita);
            }
        }

        Map<Long, SignosVitales> ultimoSignoPorMascota = new HashMap<>();
        Map<Long, String> estadoPorMascota = new HashMap<>();
        Map<Long, String> prioridadPorMascota = new HashMap<>();
        Map<Long, String> prioridadTextoPorMascota = new HashMap<>();

        int criticos = 0;
        for (Mascota mascota : mascotas) {
            SignosVitales ultimoSigno = null;
            List<SignosVitales> signos = signosVitalesService.ultimosRegistrosDeMascota(mascota.getId(), 1);
            if (signos != null && !signos.isEmpty()) {
                ultimoSigno = signos.get(0);
                ultimoSignoPorMascota.put(mascota.getId(), ultimoSigno);
            }

            Cita citaHoy = citaHoyPorMascota.get(mascota.getId());
            boolean esCritico = esTriajeCritico(ultimoSigno);
            if (esCritico) {
                criticos++;
            }

            estadoPorMascota.put(mascota.getId(), obtenerEstadoPaciente(citaHoy, ultimoSigno, esCritico));
            prioridadPorMascota.put(mascota.getId(), obtenerPrioridadPaciente(citaHoy, ultimoSigno, esCritico));
            prioridadTextoPorMascota.put(mascota.getId(), obtenerPrioridadTexto(citaHoy, ultimoSigno, esCritico));
        }

        long pendientes = citasHoy.stream()
                .filter(c -> "AGENDADA".equalsIgnoreCase(c.getEstado()))
                .count();
        long atendidos = citasHoy.stream()
                .filter(c -> "EN_CONSULTA".equalsIgnoreCase(c.getEstado())
                          || "ATENDIDA".equalsIgnoreCase(c.getEstado()))
                .count();

        model.addAttribute("fecha", hoy);
        model.addAttribute("mascotas", mascotas);
        model.addAttribute("citaHoyPorMascota", citaHoyPorMascota);
        model.addAttribute("ultimoSignoPorMascota", ultimoSignoPorMascota);
        model.addAttribute("estadoPorMascota", estadoPorMascota);
        model.addAttribute("prioridadPorMascota", prioridadPorMascota);
        model.addAttribute("prioridadTextoPorMascota", prioridadTextoPorMascota);
        model.addAttribute("atendidosHoy", atendidos);
        model.addAttribute("pendientesHoy", pendientes);
        model.addAttribute("criticosHoy", criticos);
        model.addAttribute("totalMascotas", mascotas.size());
        return "Veterinaria/dashboard";
    }

    private boolean esTriajeCritico(SignosVitales signo) {
        if (signo == null) {
            return false;
        }
        return (signo.getTemperatura() != null && (signo.getTemperatura() >= 40 || signo.getTemperatura() <= 36))
                || (signo.getFrecuenciaCardiaca() != null && signo.getFrecuenciaCardiaca() > 180)
                || (signo.getFrecuenciaRespiratoria() != null && signo.getFrecuenciaRespiratoria() > 60);
    }

    private String obtenerEstadoPaciente(Cita citaHoy, SignosVitales ultimoSigno, boolean critico) {
        if (critico) {
            return "Triaje critico";
        }
        if (citaHoy != null && "AGENDADA".equalsIgnoreCase(citaHoy.getEstado())) {
            return "Pendiente de triaje";
        }
        if (citaHoy != null && ("EN_CONSULTA".equalsIgnoreCase(citaHoy.getEstado())
                || "ATENDIDA".equalsIgnoreCase(citaHoy.getEstado()))) {
            return "Triaje realizado";
        }
        if (ultimoSigno != null) {
            return "Con triaje registrado";
        }
        return "Registrado desde recepcion";
    }

    private String obtenerPrioridadPaciente(Cita citaHoy, SignosVitales ultimoSigno, boolean critico) {
        if (critico) {
            return "danger";
        }
        if (citaHoy != null && "AGENDADA".equalsIgnoreCase(citaHoy.getEstado())) {
            return "warning";
        }
        if (ultimoSigno != null || (citaHoy != null && "ATENDIDA".equalsIgnoreCase(citaHoy.getEstado()))) {
            return "normal";
        }
        return "warning";
    }

    private String obtenerPrioridadTexto(Cita citaHoy, SignosVitales ultimoSigno, boolean critico) {
        if (critico) {
            return "Alta";
        }
        if (citaHoy != null && "AGENDADA".equalsIgnoreCase(citaHoy.getEstado())) {
            return "Media";
        }
        if (ultimoSigno != null || (citaHoy != null && "ATENDIDA".equalsIgnoreCase(citaHoy.getEstado()))) {
            return "Baja";
        }
        return "Media";
    }

    // ============ PACIENTES — usa JOIN FETCH para evitar LazyInitializationException ============
    @GetMapping("/pacientes")
    public String pacientes(Model model) {
        model.addAttribute("currentPage", "pacientes");
        usuarioRepository.findByUsername(getUsername()).ifPresent(u ->
            model.addAttribute("nombreUsuario", u.getNombre()));

        LocalDate hoy = LocalDate.now();
        LocalDateTime inicio = hoy.atStartOfDay();
        LocalDateTime fin    = hoy.atTime(LocalTime.MAX);

        // JOIN FETCH trae mascota + cliente en una sola query → sin Lazy problems
        List<Cita> todasHoy = citaRepository.findCitasDelDiaConDatos(inicio, fin);

        List<Cita> esperandoTriaje = todasHoy.stream()
                .filter(c -> "AGENDADA".equalsIgnoreCase(c.getEstado()))
                .collect(Collectors.toList());

        List<Cita> triajeCompletado = todasHoy.stream()
                .filter(c -> "EN_CONSULTA".equalsIgnoreCase(c.getEstado())
                          || "ATENDIDA".equalsIgnoreCase(c.getEstado()))
                .collect(Collectors.toList());

        List<Long> mascotasConCitaHoy = todasHoy.stream()
                .filter(c -> c.getMascota() != null && c.getMascota().getId() != null)
                .map(c -> c.getMascota().getId())
                .collect(Collectors.toList());

        List<Mascota> pacientesRegistrados = mascotaService.listarTodosConCliente().stream()
                .filter(m -> m.getId() != null && !mascotasConCitaHoy.contains(m.getId()))
                .collect(Collectors.toList());

        model.addAttribute("esperandoTriaje",  esperandoTriaje);
        model.addAttribute("triajeCompletado", triajeCompletado);
        model.addAttribute("pacientesRegistrados", pacientesRegistrados);
        model.addAttribute("totalPendientes",  esperandoTriaje.size());
        model.addAttribute("totalAtendidos",   triajeCompletado.size());
        model.addAttribute("totalRegistrados", pacientesRegistrados.size());
        model.addAttribute("fecha", hoy);

        return "Veterinaria/pacientes";
    }

    // ============ INICIAR TRIAJE ============
    @PostMapping("/pacientes/triaje")
    public String iniciarTriaje(
            @RequestParam Long citaId,
            @RequestParam Double peso,
            @RequestParam Double temperatura,
            @RequestParam Integer frecuenciaCardiaca,
            @RequestParam Integer frecuenciaRespiratoria,
            @RequestParam(required = false) String observaciones,
            @RequestParam(required = false) String planTratamiento,
            @RequestParam(required = false) String planTratamientoPersonalizado,
            RedirectAttributes redirectAttributes) {
        try {
            Cita cita = citaService.buscarPorId(citaId);
            Usuario veterinario = getUsuarioActual();

            String planFinal = "Otro".equalsIgnoreCase(planTratamiento)
                    ? planTratamientoPersonalizado
                    : planTratamiento;

            String obsCompleta = (observaciones != null && !observaciones.isBlank() ? observaciones : "");
            if (planFinal != null && !planFinal.isBlank()) {
                obsCompleta += (obsCompleta.isEmpty() ? "" : " | ") + "Plan: " + planFinal;
            }

            // Crear consulta
            Consulta consulta = consultaService.crearConsulta(
                    cita.getMascota().getId(),
                    veterinario != null ? veterinario.getId() : null,
                    cita.getMotivo(),
                    peso, temperatura,
                    frecuenciaCardiaca, frecuenciaRespiratoria,
                    obsCompleta.isEmpty() ? null : obsCompleta
            );

            // Guardar signos vitales
            signosVitalesService.crearSignosVitales(
                    cita.getMascota().getId(),
                    consulta.getId(),
                    peso, temperatura,
                    frecuenciaCardiaca, frecuenciaRespiratoria,
                    observaciones
            );

            // Cambiar estado de la cita
            cita.setEstado("EN_CONSULTA");
            citaService.actualizar(cita);

            redirectAttributes.addFlashAttribute("success",
                    "Triaje iniciado para " + cita.getMascota().getNombre());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al iniciar triaje: " + e.getMessage());
        }
        return "redirect:/veterinaria/pacientes";
    }

    // ============ HISTORIAL CLÍNICO ============
    @GetMapping("/historial")
    public String historial(Model model) {
        model.addAttribute("currentPage", "historial");
        usuarioRepository.findByUsername(getUsername()).ifPresent(u ->
            model.addAttribute("nombreUsuario", u.getNombre()));

        List<Mascota> mascotas = mascotaService.listarTodos();
        model.addAttribute("mascotas", mascotas != null ? mascotas : new ArrayList<>());

        if (mascotas != null && !mascotas.isEmpty()) {
            cargarHistorialMascota(mascotas.get(0).getId(), model);
        }
        return "Veterinaria/historial";
    }

    @GetMapping("/historial/{mascotaId}")
    public String historialMascota(@PathVariable Long mascotaId, Model model) {
        model.addAttribute("currentPage", "historial");
        usuarioRepository.findByUsername(getUsername()).ifPresent(u ->
            model.addAttribute("nombreUsuario", u.getNombre()));

        List<Mascota> mascotas = mascotaService.listarTodos();
        model.addAttribute("mascotas", mascotas != null ? mascotas : new ArrayList<>());
        cargarHistorialMascota(mascotaId, model);
        return "Veterinaria/historial";
    }

    private void cargarHistorialMascota(Long mascotaId, Model model) {
        try {
            Mascota mascota = mascotaService.buscarPorId(mascotaId);
            model.addAttribute("mascotaSeleccionada", mascota);

            List<Consulta> consultas = consultaService.buscarPorMascota(mascotaId);
            model.addAttribute("consultas", consultas != null ? consultas : new ArrayList<>());

            List<SignosVitales> signos = signosVitalesService.ultimosRegistrosDeMascota(mascotaId, 10);
            if (signos != null && !signos.isEmpty()) {
                model.addAttribute("ultimoSigno", signos.get(0));
            }
            model.addAttribute("historialSignos", signos != null ? signos : new ArrayList<>());

        } catch (Exception e) {
            model.addAttribute("errorHistorial", "No se pudo cargar el historial: " + e.getMessage());
        }
    }

    // ============ API JSON para historial ============
    @GetMapping("/api/historial/{mascotaId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiHistorial(@PathVariable Long mascotaId) {
        try {
            Mascota m = mascotaService.buscarPorId(mascotaId);
            List<Consulta> consultas = consultaService.buscarPorMascota(mascotaId);
            List<SignosVitales> signos = signosVitalesService.ultimosRegistrosDeMascota(mascotaId, 10);

            SignosVitales ultimoSigno = (signos != null && !signos.isEmpty()) ? signos.get(0) : null;

            List<Map<String, Object>> pesoData = new ArrayList<>();
            if (signos != null) {
                for (int i = signos.size() - 1; i >= 0; i--) {
                    SignosVitales sv = signos.get(i);
                    if (sv.getPeso() != null) {
                        pesoData.add(Map.of(
                            "fecha", sv.getFechaRegistro().toLocalDate().toString(),
                            "peso",  sv.getPeso()
                        ));
                    }
                }
            }

            Map<String, Object> response = new java.util.HashMap<>();
            response.put("mascota", Map.of(
                "id",      m.getId(),
                "nombre",  m.getNombre(),
                "especie", m.getEspecie() != null  ? m.getEspecie()  : "",
                "raza",    m.getRaza()    != null  ? m.getRaza()     : "",
                "edad",    m.getEdad()    != null  ? m.getEdad()     : 0,
                "dueño",   m.getCliente().getNombre()
            ));
            response.put("ultimoSigno", ultimoSigno != null ? Map.of(
                "peso",                 ultimoSigno.getPeso()                != null ? ultimoSigno.getPeso()                : 0,
                "temperatura",          ultimoSigno.getTemperatura()         != null ? ultimoSigno.getTemperatura()         : 0,
                "frecuenciaCardiaca",   ultimoSigno.getFrecuenciaCardiaca()  != null ? ultimoSigno.getFrecuenciaCardiaca()  : 0,
                "frecuenciaRespiratoria", ultimoSigno.getFrecuenciaRespiratoria() != null ? ultimoSigno.getFrecuenciaRespiratoria() : 0
            ) : null);
            response.put("pesoData",       pesoData);
            response.put("totalConsultas", consultas != null ? consultas.size() : 0);

            String ultimoEstado = (consultas != null && !consultas.isEmpty())
                    ? (consultas.get(0).getEstado() != null ? consultas.get(0).getEstado() : "Sin consultas")
                    : "Sin consultas";
            response.put("ultimoEstado", ultimoEstado);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ============ Otros endpoints ============
    @GetMapping("/agenda")
    public String agenda(Model model) {
        model.addAttribute("currentPage", "agenda");
        return "Veterinaria/agenda";
    }

    @GetMapping("/vacunas")
    public String vacunas(Model model) {
        model.addAttribute("currentPage", "vacunas");
        return "Veterinaria/vacunas";
    }

    @GetMapping("/alertas")
    public String alertas(Model model) {
        model.addAttribute("currentPage", "alertas");
        return "Veterinaria/alertas";
    }

    @GetMapping("/diagnostico-ia")
    public String diagnosticoIA(Model model) {
        model.addAttribute("currentPage", "diagnostico");
        return "Veterinaria/diagnostico-ia";
    }

    @GetMapping("/perfil")
    public String perfil(Model model) {
        model.addAttribute("currentPage", "perfil");
        return "Veterinaria/perfil";
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("currentPage", "settings");
        return "Veterinaria/settings";
    }
}
