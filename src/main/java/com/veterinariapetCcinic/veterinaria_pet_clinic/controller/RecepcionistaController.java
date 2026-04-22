package com.veterinariapetCcinic.veterinaria_pet_clinic.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.veterinariapetCcinic.veterinaria_pet_clinic.Model.Cita;
import com.veterinariapetCcinic.veterinaria_pet_clinic.Model.Cliente;
import com.veterinariapetCcinic.veterinaria_pet_clinic.Model.Mascota;
import com.veterinariapetCcinic.veterinaria_pet_clinic.Model.Pago;
import com.veterinariapetCcinic.veterinaria_pet_clinic.service.AgendaService;
import com.veterinariapetCcinic.veterinaria_pet_clinic.service.CitaService;
import com.veterinariapetCcinic.veterinaria_pet_clinic.service.ClienteService;
import com.veterinariapetCcinic.veterinaria_pet_clinic.service.MascotaService;
import com.veterinariapetCcinic.veterinaria_pet_clinic.service.NotificacionService;
import com.veterinariapetCcinic.veterinaria_pet_clinic.service.PagoService;

@Controller
@RequestMapping("/recepcionista")
public class RecepcionistaController {

    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private MascotaService mascotaService;
    
    @Autowired
    private CitaService citaService;
    
    @Autowired
    private PagoService pagoService;
    
    @Autowired
    private AgendaService agendaService;
    
    @Autowired
    private NotificacionService notificacionService;

    private String getNombreUsuario() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "Recepcionista";
    }

    // ============ DASHBOARD ============
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("nombreUsuario", getNombreUsuario());
        
        // Usando los métodos existentes
        model.addAttribute("totalClientes", clienteService.contarClientes());
        model.addAttribute("totalMascotas", mascotaService.contarMascotas());
        model.addAttribute("citasHoy", citaService.contarCitasHoy());
        
        // Para ingresos del día usando tus métodos existentes
        Double ingresosHoy = pagoService.getTotalPagosDelDia();
        model.addAttribute("ingresosHoy", ingresosHoy != null ? ingresosHoy : 0.0);
        
        // Próximas citas del día
        List<Cita> proximasCitas = citaService.obtenerCitasDelDia(LocalDate.now());
        model.addAttribute("proximasCitas", proximasCitas != null ? proximasCitas : new ArrayList<>());
        
        // Últimos pagos del día
        List<Pago> ultimosPagos = pagoService.obtenerPagosDelDia();
        model.addAttribute("ultimosPagos", ultimosPagos != null ? ultimosPagos : new ArrayList<>());
        
        return "recepcionista/dashboard";
    }

    // ============ GESTIÓN DE CLIENTES ============
    @GetMapping("/clientes")
    public String listarClientes(@RequestParam(required = false) String buscar, Model model) {
        model.addAttribute("nombreUsuario", getNombreUsuario());
        
        List<Cliente> clientes;
        if (buscar != null && !buscar.isEmpty()) {
            clientes = clienteService.buscarClientes(buscar);
        } else {
            clientes = clienteService.listarTodos();
        }
        model.addAttribute("clientes", clientes != null ? clientes : new ArrayList<>());
        model.addAttribute("buscar", buscar);
        return "recepcionista/clientes";
    }

    @GetMapping("/clientes/nuevo")
    public String nuevoClienteForm(Model model) {
        model.addAttribute("nombreUsuario", getNombreUsuario());
        model.addAttribute("cliente", new Cliente());
        return "recepcionista/cliente-form";
    }

    @PostMapping("/clientes/guardar")
    public String guardarCliente(@ModelAttribute Cliente cliente, RedirectAttributes redirectAttributes) {
        try {
            clienteService.guardar(cliente);
            redirectAttributes.addFlashAttribute("success", "Cliente guardado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/recepcionista/clientes";
    }

    @GetMapping("/clientes/editar/{id}")
    public String editarClienteForm(@PathVariable Long id, Model model) {
        model.addAttribute("nombreUsuario", getNombreUsuario());
        try {
            Cliente cliente = clienteService.buscarPorId(id);
            model.addAttribute("cliente", cliente);
        } catch (Exception e) {
            model.addAttribute("error", "Cliente no encontrado");
            return "redirect:/recepcionista/clientes";
        }
        return "recepcionista/cliente-form";
    }

    @PostMapping("/clientes/actualizar/{id}")
    public String actualizarCliente(@PathVariable Long id, @ModelAttribute Cliente cliente, RedirectAttributes redirectAttributes) {
        try {
            cliente.setId(id);
            clienteService.actualizar(cliente);
            redirectAttributes.addFlashAttribute("success", "Cliente actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/recepcionista/clientes";
    }

    @GetMapping("/clientes/eliminar/{id}")
    public String eliminarCliente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            clienteService.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Cliente eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/recepcionista/clientes";
    }

    @GetMapping("/clientes/ver/{id}")
    public String verCliente(@PathVariable Long id, Model model) {
        model.addAttribute("nombreUsuario", getNombreUsuario());
        try {
            Cliente cliente = clienteService.obtenerClienteConMascotas(id);
            model.addAttribute("cliente", cliente);
        } catch (Exception e) {
            model.addAttribute("error", "Cliente no encontrado");
            return "redirect:/recepcionista/clientes";
        }
        return "recepcionista/cliente-detalle";
    }

    // ============ GESTIÓN DE MASCOTAS ============
    @GetMapping("/mascotas")
    public String listarMascotas(Model model) {
        model.addAttribute("nombreUsuario", getNombreUsuario());
        List<Mascota> mascotas = mascotaService.listarTodos();
        model.addAttribute("mascotas", mascotas != null ? mascotas : new ArrayList<>());
        return "recepcionista/mascotas";
    }

    @GetMapping("/mascotas/nuevo")
    public String nuevaMascotaForm(Model model) {
        model.addAttribute("nombreUsuario", getNombreUsuario());
        model.addAttribute("mascota", new Mascota());
        model.addAttribute("clientes", clienteService.listarTodos());
        return "recepcionista/mascota-form";
    }

    @PostMapping("/mascotas/guardar")
    public String guardarMascota(@ModelAttribute Mascota mascota, @RequestParam Long clienteId, RedirectAttributes redirectAttributes) {
        try {
            mascotaService.registrarMascota(clienteId, mascota);
            redirectAttributes.addFlashAttribute("success", "Mascota registrada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/recepcionista/mascotas";
    }

    @GetMapping("/mascotas/editar/{id}")
    public String editarMascotaForm(@PathVariable Long id, Model model) {
        model.addAttribute("nombreUsuario", getNombreUsuario());
        try {
            Mascota mascota = mascotaService.buscarPorId(id);
            model.addAttribute("mascota", mascota);
            model.addAttribute("clientes", clienteService.listarTodos());
        } catch (Exception e) {
            model.addAttribute("error", "Mascota no encontrada");
            return "redirect:/recepcionista/mascotas";
        }
        return "recepcionista/mascota-form";
    }

    @PostMapping("/mascotas/actualizar/{id}")
    public String actualizarMascota(@PathVariable Long id, @ModelAttribute Mascota mascota, RedirectAttributes redirectAttributes) {
        try {
            mascota.setId(id);
            mascotaService.actualizar(mascota);
            redirectAttributes.addFlashAttribute("success", "Mascota actualizada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/recepcionista/mascotas";
    }

    @GetMapping("/mascotas/eliminar/{id}")
    public String eliminarMascota(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            mascotaService.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Mascota eliminada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/recepcionista/mascotas";
    }

    // ============ GESTIÓN DE CITAS ============
    @GetMapping("/citas")
    public String listarCitas(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha, Model model) {
        model.addAttribute("nombreUsuario", getNombreUsuario());
        
        if (fecha == null) {
            fecha = LocalDate.now();
        }
        
        List<Cita> citas = citaService.obtenerCitasDelDia(fecha);
        model.addAttribute("citas", citas != null ? citas : new ArrayList<>());
        model.addAttribute("fecha", fecha);
        return "recepcionista/citas";
    }

    @GetMapping("/citas/nueva")
    public String nuevaCitaForm(Model model) {
        model.addAttribute("nombreUsuario", getNombreUsuario());
        model.addAttribute("cita", new Cita());
        model.addAttribute("mascotas", mascotaService.listarTodos());
        return "recepcionista/cita-form";
    }

    @PostMapping("/citas/guardar")
    public String guardarCita(@RequestParam Long mascotaId, 
                              @RequestParam String fecha,
                              @RequestParam String hora,
                              @RequestParam(required = false) String motivo,
                              RedirectAttributes redirectAttributes) {
        try {
            // Obtener la mascota
            Mascota mascota = mascotaService.buscarPorId(mascotaId);
            
            // Crear la cita
            Cita cita = new Cita();
            cita.setMascota(mascota);
            cita.setMotivo(motivo);
            cita.setEstado("AGENDADA");
            
            // Parsear fecha y hora
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime fechaHora = LocalDateTime.parse(fecha + " " + hora, formatter);
            cita.setFechaHora(fechaHora);
            
            // Guardar usando el método guardar que ya tiene notificación
            citaService.guardar(cita);
            
            redirectAttributes.addFlashAttribute("success", "Cita agendada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/recepcionista/citas";
    }

    @GetMapping("/citas/cancelar/{id}")
    public String cancelarCita(@PathVariable Long id, @RequestParam(required = false) String motivo, RedirectAttributes redirectAttributes) {
        try {
            String motivoCancelacion = motivo != null ? motivo : "Cancelada por recepcionista";
            citaService.cancelarCita(id, motivoCancelacion);
            redirectAttributes.addFlashAttribute("success", "Cita cancelada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/recepcionista/citas";
    }

    // ============ GESTIÓN DE AGENDA ============
    @GetMapping("/agenda")
    public String verAgenda(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha, Model model) {
        model.addAttribute("nombreUsuario", getNombreUsuario());
        
        if (fecha == null) {
            fecha = LocalDate.now();
        }
        
        List<Cita> citasDelDia = citaService.obtenerCitasDelDia(fecha);
        model.addAttribute("citas", citasDelDia != null ? citasDelDia : new ArrayList<>());
        model.addAttribute("fecha", fecha);
        return "recepcionista/agenda";
    }

    // ============ GESTIÓN DE PAGOS ============
    @GetMapping("/pagos")
    public String listarPagos(Model model) {
        model.addAttribute("nombreUsuario", getNombreUsuario());
        List<Pago> pagos = pagoService.listarTodos();
        model.addAttribute("pagos", pagos != null ? pagos : new ArrayList<>());
        return "recepcionista/pagos";
    }

    @GetMapping("/pagos/nuevo")
    public String nuevoPagoForm(Model model) {
        model.addAttribute("nombreUsuario", getNombreUsuario());
        model.addAttribute("pago", new Pago());
        model.addAttribute("clientes", clienteService.listarTodos());
        return "recepcionista/pago-form";
    }

    @PostMapping("/pagos/guardar")
    public String guardarPago(@RequestParam Long clienteId,
                              @RequestParam Double monto,
                              @RequestParam String metodoPago,
                              @RequestParam(required = false) Long citaId,
                              RedirectAttributes redirectAttributes) {
        try {
            Cliente cliente = clienteService.buscarPorId(clienteId);
            
            Pago pago = new Pago();
            pago.setCliente(cliente);
            pago.setMonto(monto);
            pago.setMetodoPago(metodoPago);
            pago.setEstado("PAGADO");
            
            if (citaId != null) {
                pagoService.registrarPago(pago, citaId);
            } else {
                pagoService.guardar(pago);
            }
            
            notificacionService.enviarConfirmacionPago(cliente, monto, metodoPago);
            
            redirectAttributes.addFlashAttribute("success", "Pago registrado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/recepcionista/pagos";
    }

    // ============ PERFIL ============
    @GetMapping("/perfil")
    public String perfil(Model model) {
        model.addAttribute("nombreUsuario", getNombreUsuario());
        return "recepcionista/perfil";
    }
    
    // ============ MACHINE LEARNING ============
    @GetMapping("/ml/diagnostico")
    public String mlDiagnostico(Model model) {
        model.addAttribute("nombreUsuario", getNombreUsuario());
        List<Mascota> mascotas = mascotaService.listarTodos();
        model.addAttribute("mascotas", mascotas != null ? mascotas : new ArrayList<>());
        return "recepcionista/ml-diagnostico";
    }
}