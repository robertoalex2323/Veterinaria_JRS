package com.veterinariapetCcinic.veterinaria_pet_clinic.config;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.veterinariapetCcinic.veterinaria_pet_clinic.Model.Usuario;
import com.veterinariapetCcinic.veterinaria_pet_clinic.repository.UsuarioRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        
        // 1. ADMIN
        if (!usuarioRepository.existsByUsername("admin")) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("password123"));
            admin.setNombre("Administrador");
            admin.setEmail("admin@veterinaria.com");
            admin.setRol("ADMIN");
            admin.setActivo(true);
            admin.setFechaCreacion(LocalDateTime.now());
            usuarioRepository.save(admin);
            System.out.println("✅ Usuario ADMIN creado: admin / password123");
        }

        // 2. VETERINARIO 1
        if (!usuarioRepository.existsByUsername("vet_juan")) {
            Usuario vet = new Usuario();
            vet.setUsername("vet_juan");
            vet.setPassword(passwordEncoder.encode("password123"));
            vet.setNombre("Juan García");
            vet.setEmail("juan@veterinaria.com");
            vet.setRol("VETERINARIO");
            vet.setActivo(true);
            vet.setFechaCreacion(LocalDateTime.now());
            usuarioRepository.save(vet);
            System.out.println("✅ Usuario VETERINARIO creado: vet_juan / password123");
        }

        // 3. VETERINARIO 2
        if (!usuarioRepository.existsByUsername("vet_maria")) {
            Usuario vet2 = new Usuario();
            vet2.setUsername("vet_maria");
            vet2.setPassword(passwordEncoder.encode("password123"));
            vet2.setNombre("María López");
            vet2.setEmail("maria@veterinaria.com");
            vet2.setRol("VETERINARIO");
            vet2.setActivo(true);
            vet2.setFechaCreacion(LocalDateTime.now());
            usuarioRepository.save(vet2);
            System.out.println("✅ Usuario VETERINARIO creado: vet_maria / password123");
        }

        // 4. RECEPCIONISTA 1
        if (!usuarioRepository.existsByUsername("recv_carlos")) {
            Usuario recep = new Usuario();
            recep.setUsername("recv_carlos");
            recep.setPassword(passwordEncoder.encode("password123"));
            recep.setNombre("Carlos Rodríguez");
            recep.setEmail("carlos@veterinaria.com");
            recep.setRol("RECEPCIONISTA");
            recep.setActivo(true);
            recep.setFechaCreacion(LocalDateTime.now());
            usuarioRepository.save(recep);
            System.out.println("✅ Usuario RECEPCIONISTA creado: recv_carlos / password123");
        }

        // 5. RECEPCIONISTA 2
        if (!usuarioRepository.existsByUsername("recv_ana")) {
            Usuario recep2 = new Usuario();
            recep2.setUsername("recv_ana");
            recep2.setPassword(passwordEncoder.encode("password123"));
            recep2.setNombre("Ana Martínez");
            recep2.setEmail("ana@veterinaria.com");
            recep2.setRol("RECEPCIONISTA");
            recep2.setActivo(true);
            recep2.setFechaCreacion(LocalDateTime.now());
            usuarioRepository.save(recep2);
            System.out.println("✅ Usuario RECEPCIONISTA creado: recv_ana / password123");
        }

        // 5. FARMACÉUTICO
        if (!usuarioRepository.existsByUsername("farmaceutico")) {
            Usuario farmaceutico = new Usuario();
            farmaceutico.setUsername("farmaceutico");
            farmaceutico.setPassword(passwordEncoder.encode("farmaceutico_pet_clinic"));
            farmaceutico.setNombre("Dr. Dario Arroyo");
            farmaceutico.setEmail("Dario.Arroyo@veterinaria.com");
            farmaceutico.setRol("FARMACEUTICO");
            farmaceutico.setActivo(true);
            farmaceutico.setFechaCreacion(LocalDateTime.now());
            usuarioRepository.save(farmaceutico);
            System.out.println("✅ Usuario FARMACEUTICO creado exitosamente");
        }
        
        System.out.println("🎉 Todos los usuarios iniciales han sido creados");
    }
}