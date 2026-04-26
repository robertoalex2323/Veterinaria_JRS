package com.veterinariapetCcinic.veterinaria_pet_clinic.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.veterinariapetCcinic.veterinaria_pet_clinic.Model.Agenda;
import com.veterinariapetCcinic.veterinaria_pet_clinic.Model.Cita;
import com.veterinariapetCcinic.veterinaria_pet_clinic.Model.Cliente;
import com.veterinariapetCcinic.veterinaria_pet_clinic.Model.Mascota;
import com.veterinariapetCcinic.veterinaria_pet_clinic.Model.Pago;
import com.veterinariapetCcinic.veterinaria_pet_clinic.Model.Usuario;
import com.veterinariapetCcinic.veterinaria_pet_clinic.service.AgendaService;
import com.veterinariapetCcinic.veterinaria_pet_clinic.service.CitaService;
import com.veterinariapetCcinic.veterinaria_pet_clinic.service.ClienteService;
import com.veterinariapetCcinic.veterinaria_pet_clinic.service.MascotaService;
import com.veterinariapetCcinic.veterinaria_pet_clinic.service.PagoService;
import com.veterinariapetCcinic.veterinaria_pet_clinic.service.UsuarioService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AgendaService agendaService;
    private final CitaService citaService;
    private final ClienteService clienteService;
    private final MascotaService mascotaService;
    private final PagoService pagoService;
    private final UsuarioService usuarioService;

    public AdminController(AgendaService agendaService,
            CitaService citaService,
            ClienteService clienteService,
            MascotaService mascotaService,
            PagoService pagoService,
            UsuarioService usuarioService) {
        this.agendaService = agendaService;
        this.citaService = citaService;
        this.clienteService = clienteService;
        this.mascotaService = mascotaService;
        this.pagoService = pagoService;
        this.usuarioService = usuarioService;
    }

    private String getNombreUsuario() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "Administrador";
    }

    @GetMapping({"", "/"})
    public String inicio() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("nombreUsuario", getNombreUsuario());
        model.addAttribute("totalUsuarios", usuarioService.contarUsuarios());
        model.addAttribute("totalClientes", clienteService.contarClientes());
        model.addAttribute("totalMascotas", mascotaService.contarMascotas());
        model.addAttribute("totalCitas", citaService.contarCitas());
        model.addAttribute("totalPagos", pagoService.contarPagos());
        model.addAttribute("totalAgendas", agendaService.contarAgendas());
        return "admin/dashboard";
    }

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        model.addAttribute("nombreUsuario", getNombreUsuario());
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        model.addAttribute("usuarios", usuarios != null ? usuarios : new ArrayList<>());
        return "admin/usuarios";
    }

    @GetMapping("/clientes")
    public String listarClientes(Model model) {
        model.addAttribute("nombreUsuario", getNombreUsuario());
        List<Cliente> clientes = clienteService.listarTodos();
        model.addAttribute("clientes", clientes != null ? clientes : new ArrayList<>());
        return "admin/clientes";
    }

    @GetMapping("/mascotas")
    public String listarMascotas(Model model) {
        model.addAttribute("nombreUsuario", getNombreUsuario());
        List<Mascota> mascotas = mascotaService.listarTodos();
        model.addAttribute("mascotas", mascotas != null ? mascotas : new ArrayList<>());
        return "admin/mascotas";
    }

    @GetMapping("/citas")
    public String listarCitas(Model model) {
        model.addAttribute("nombreUsuario", getNombreUsuario());
        List<Cita> citas = citaService.listarTodas();
        model.addAttribute("citas", citas != null ? citas : new ArrayList<>());
        return "admin/citas";
    }

    @GetMapping("/agenda")
    public String listarAgenda(Model model) {
        model.addAttribute("nombreUsuario", getNombreUsuario());
        List<Agenda> agendas = agendaService.listarTodas();
        model.addAttribute("agendas", agendas != null ? agendas : new ArrayList<>());
        return "admin/agenda";
    }

    @GetMapping("/pagos")
    public String listarPagos(Model model) {
        model.addAttribute("nombreUsuario", getNombreUsuario());
        List<Pago> pagos = pagoService.listarTodos();
        model.addAttribute("pagos", pagos != null ? pagos : new ArrayList<>());
        return "admin/pagos";
    }
}
