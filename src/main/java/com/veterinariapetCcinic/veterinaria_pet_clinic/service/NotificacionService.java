package com.veterinariapetCcinic.veterinaria_pet_clinic.service;

import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.veterinariapetCcinic.veterinaria_pet_clinic.Model.Cita;
import com.veterinariapetCcinic.veterinaria_pet_clinic.Model.Cliente;

@Service
public class NotificacionService {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    public void enviarConfirmacionCita(Cita cita) {
        String mensaje = String.format("""
            ✅ Su cita ha sido agendada exitosamente.
            📅 Fecha: %s
            🐕 Mascota: %s
            🏥 Motivo: %s
                
            Gracias por confiar en nosotros.""",
            cita.getFechaHora().format(FORMATTER),
            cita.getMascota().getNombre(),
            cita.getMotivo()
        );
        System.out.println("📧 Enviando SMS/Email a: " + cita.getMascota().getCliente().getTelefono());
        System.out.println("📝 Mensaje:\n" + mensaje);
        System.out.println("--- Notificación enviada ---\n");
    }
    
    public void enviarCancelacionCita(Cita cita) {
        String mensaje = String.format("""
            ⚠️ Su cita para el %s ha sido CANCELADA.
            🐕 Mascota: %s
                
            Para reagendar, comuníquese al 123-456-789.""",
            cita.getFechaHora().format(FORMATTER),
            cita.getMascota().getNombre()
        );
        System.out.println("📧 Notificación de cancelación enviada a: " + 
            cita.getMascota().getCliente().getTelefono());
        System.out.println("📝 Mensaje:\n" + mensaje);
    }
    
    public void enviarRecordatorioCita(Cita cita) {
        String mensaje = String.format("""
            🔔 RECORDATORIO: Mañana %s a las %s tiene una cita para su mascota %s.
            Por favor llegar con 10 minutos de anticipación.""",
            cita.getFechaHora().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            cita.getFechaHora().toLocalTime(),
            cita.getMascota().getNombre()
        );
        System.out.println("📧 Recordatorio enviado a: " + cita.getMascota().getCliente().getTelefono());
        System.out.println("📝 Mensaje:\n" + mensaje);
    }
    
    public void enviarNotificacionVeterinario(Cita cita) {
        String mensaje = String.format("""
            🏥 Nueva cita asignada:
            📅 Fecha: %s
            🐕 Mascota: %s
            👤 Dueño: %s
            📝 Motivo: %s""",
            cita.getFechaHora().format(FORMATTER),
            cita.getMascota().getNombre(),
            cita.getMascota().getCliente().getNombre(),
            cita.getMotivo()
        );
        System.out.println("📧 Notificando al veterinario ID: " + 
            (cita.getVeterinario() != null ? cita.getVeterinario().getId() : "No asignado"));
        System.out.println("📝 Mensaje:\n" + mensaje);
    }
    
    public void enviarInformeCliente(Cliente cliente, String mensaje) {
        String informe = String.format("""
            📋 INFORMACIÓN IMPORTANTE
                
            Estimado(a) %s,
                
            %s
                
            Atentamente,
            Veterinaria Pet Clinic""",
            cliente.getNombre(),
            mensaje
        );
        System.out.println("📧 Enviando informe a: " + cliente.getTelefono() + " / " + cliente.getEmail());
        System.out.println("📝 Mensaje:\n" + informe);
    }
    
    public void enviarConfirmacionPago(Cliente cliente, Double monto, String metodoPago) {
        String mensaje = String.format("""
            💰 PAGO REGISTRADO
                
            Cliente: %s
            Monto: S/ %.2f
            Método: %s
                
            Gracias por su pago.""",
            cliente.getNombre(),
            monto,
            metodoPago
        );
        System.out.println("📧 Confirmación de pago enviada a: " + cliente.getTelefono());
        System.out.println("📝 Mensaje:\n" + mensaje);
    }
}