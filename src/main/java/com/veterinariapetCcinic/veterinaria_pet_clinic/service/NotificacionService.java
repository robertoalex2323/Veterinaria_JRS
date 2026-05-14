package com.veterinariapetCcinic.veterinaria_pet_clinic.service;

import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.veterinariapetCcinic.veterinaria_pet_clinic.Model.Cita;
import com.veterinariapetCcinic.veterinaria_pet_clinic.Model.Cliente;
import com.veterinariapetCcinic.veterinaria_pet_clinic.repository.CitaRepository;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.LocalDate;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Autowired
    private CitaRepository citaRepository;

    @Value("${twilio.account-sid:}")
    private String twilioAccountSid;

    @Value("${twilio.auth-token:}")
    private String twilioAuthToken;

    @Value("${twilio.whatsapp-number:whatsapp:+14155238886}")
    private String twilioWhatsappNumber;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @PostConstruct
    public void initTwilio() {
        if (twilioAccountSid != null && !twilioAccountSid.isEmpty() &&
                twilioAuthToken != null && !twilioAuthToken.isEmpty()) {
            Twilio.init(twilioAccountSid, twilioAuthToken);
            log.info("📱 Twilio WhatsApp inicializado.");
        }
    }

    public void enviarConfirmacionCita(Cita cita) {
        String mensaje = String.format("""
                ✅ Su cita ha sido agendada exitosamente.
                📅 Fecha: %s
                🐕 Mascota: %s
                🏥 Motivo: %s

                Gracias por confiar en nosotros.""",
                cita.getFechaHora().format(FORMATTER),
                cita.getMascota().getNombre(),
                cita.getMotivo());

        log.info("📧 Enviando SMS/Email a: {}", cita.getMascota().getCliente().getTelefono());
        log.info("📝 Mensaje:\n{}", mensaje);

        // Enviar Correo Electrónico
        String emailDestino = cita.getMascota().getCliente().getEmail();
        if (emailDestino != null && !emailDestino.trim().isEmpty() && mailSender != null) {
            try {
                org.springframework.mail.SimpleMailMessage mailMessage = new org.springframework.mail.SimpleMailMessage();
                mailMessage.setTo(emailDestino);
                mailMessage.setSubject("Confirmación de Cita Veterinaria - " + cita.getMascota().getNombre());
                mailMessage.setText(mensaje);
                mailSender.send(mailMessage);
                log.info("✅ Correo enviado exitosamente a: {}", emailDestino);
            } catch (Exception e) {
                log.error("❌ Error al enviar correo a {}: {}", emailDestino, e.getMessage());
            }
        } else {
            log.warn("⚠️ El cliente no tiene correo registrado o el servicio de correo no está configurado.");
        }

        // Enviar WhatsApp
        if (twilioAccountSid != null && !twilioAccountSid.isEmpty() && twilioAuthToken != null
                && !twilioAuthToken.isEmpty()) {
            try {
                String telefonoCliente = cita.getMascota().getCliente().getTelefono();
                if (!telefonoCliente.startsWith("+")) {
                    telefonoCliente = "+51" + telefonoCliente;
                }

                String numeroDestino = "whatsapp:" + telefonoCliente;

                com.twilio.rest.api.v2010.account.Message twilioMessage = com.twilio.rest.api.v2010.account.Message
                        .creator(
                                new com.twilio.type.PhoneNumber(numeroDestino),
                                new com.twilio.type.PhoneNumber(twilioWhatsappNumber),
                                mensaje)
                        .create();

                log.info("✅ WhatsApp enviado exitosamente a {}. SID: {}", telefonoCliente, twilioMessage.getSid());
            } catch (Exception e) {
                log.error("❌ Error al enviar WhatsApp: {}", e.getMessage());
            }
        } else {
            log.warn("⚠️ Twilio no configurado. No se envió WhatsApp.");
        }

        log.info("--- Notificación enviada ---\n");
    }

    public void enviarCancelacionCita(Cita cita) {
        String mensaje = String.format("""
                ⚠️ Su cita para el %s ha sido CANCELADA.
                🐕 Mascota: %s

                Para reagendar, comuníquese al 123-456-789.""",
                cita.getFechaHora().format(FORMATTER),
                cita.getMascota().getNombre());
        log.info("📧 Notificación de cancelación enviada a: {}", cita.getMascota().getCliente().getTelefono());
        log.info("📝 Mensaje:\n{}", mensaje);
    }

    public void enviarRecordatorioCita(Cita cita) {
        String mensaje = String.format("""
                🔔 RECORDATORIO: Mañana %s a las %s tiene una cita para su mascota %s.
                Por favor llegar con 10 minutos de anticipación.""",
                cita.getFechaHora().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                cita.getFechaHora().toLocalTime(),
                cita.getMascota().getNombre());
        log.info("📧 Recordatorio enviado a (consola/SMS): {}", cita.getMascota().getCliente().getTelefono());
        log.info("📝 Mensaje:\n{}", mensaje);

        String emailDestino = cita.getMascota().getCliente().getEmail();
        if (emailDestino != null && !emailDestino.trim().isEmpty() && mailSender != null) {
            try {
                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setTo(emailDestino);
                mailMessage.setSubject("Recordatorio de Cita Veterinaria - " + cita.getMascota().getNombre());
                mailMessage.setText(mensaje);
                mailSender.send(mailMessage);
                log.info("✅ Correo enviado exitosamente a: {}", emailDestino);
            } catch (Exception e) {
                log.error("❌ Error al enviar correo a {}: {}", emailDestino, e.getMessage());
            }
        } else {
            log.warn("⚠️ El cliente no tiene correo registrado o el servicio de correo no está configurado.");
        }

        // ENVÍO POR WHATSAPP (TWILIO)
        if (twilioAccountSid != null && !twilioAccountSid.isEmpty() && twilioAuthToken != null
                && !twilioAuthToken.isEmpty()) {
            try {
                // Formateamos el número (Asumimos que el número del cliente requiere código de
                // país, ejemplo +51 para Perú)
                // Si tu base de datos ya tiene el "+51", puedes omitir agregarlo.
                String telefonoCliente = cita.getMascota().getCliente().getTelefono();
                if (!telefonoCliente.startsWith("+")) {
                    telefonoCliente = "+51" + telefonoCliente; // <-- CAMBIA ESTO por tu código de país
                }

                String numeroDestino = "whatsapp:" + telefonoCliente;

                Message twilioMessage = Message.creator(
                        new PhoneNumber(numeroDestino),
                        new PhoneNumber(twilioWhatsappNumber),
                        mensaje).create();

                log.info("✅ WhatsApp enviado exitosamente a {}. SID: {}", telefonoCliente, twilioMessage.getSid());
            } catch (Exception e) {
                log.error("❌ Error al enviar WhatsApp: {}", e.getMessage());
            }
        } else {
            log.warn("⚠️ Twilio no configurado. No se envió WhatsApp.");
        }
    }

    @Scheduled(cron = "0 0 8 * * ?") // Ejecuta todos los días a las 8:00 AM
    @Transactional
    public void programarRecordatoriosManana() {
        LocalDate manana = LocalDate.now().plusDays(1);
        log.info("⏳ Iniciando proceso de envío de recordatorios para citas de mañana ({})...", manana);

        List<Cita> citasManana = citaRepository.findCitasPendientesParaRecordatorio(manana);

        if (citasManana.isEmpty()) {
            log.info("✅ No hay citas pendientes para recordar mañana.");
            return;
        }

        for (Cita cita : citasManana) {
            try {
                enviarRecordatorioCita(cita);
                cita.setRecordatorioEnviado(true);
                citaRepository.save(cita);
            } catch (Exception e) {
                log.error("❌ Error al enviar recordatorio a la cita {}: {}", cita.getId(), e.getMessage());
            }
        }
        log.info("✅ Finalizó el envío de recordatorios. Total procesados: {}", citasManana.size());
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
                cita.getMotivo());
        log.info("📧 Notificando al veterinario ID: {}",
                (cita.getVeterinario() != null ? cita.getVeterinario().getId() : "No asignado"));
        log.info("📝 Mensaje:\n{}", mensaje);
    }

    public void enviarInformeCliente(Cliente cliente, String mensaje) {
        String informe = String.format("""
                📋 INFORMACIÓN IMPORTANTE

                Estimado(a) %s,

                %s

                Atentamente,
                Veterinaria Pet Clinic""",
                cliente.getNombre(),
                mensaje);
        log.info("📧 Enviando informe a: {} / {}", cliente.getTelefono(), cliente.getEmail());
        log.info("📝 Mensaje:\n{}", informe);
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
                metodoPago);
        log.info("📧 Confirmación de pago enviada a: {}", cliente.getTelefono());
        log.info("📝 Mensaje:\n{}", mensaje);
    }
}