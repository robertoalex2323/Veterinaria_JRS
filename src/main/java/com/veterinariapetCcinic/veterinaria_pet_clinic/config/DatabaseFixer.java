package com.veterinariapetCcinic.veterinaria_pet_clinic.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class DatabaseFixer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🛠️ Verificando estructura de base de datos...");
        try {
            jdbcTemplate
                    .execute("ALTER TABLE citas ADD COLUMN IF NOT EXISTS recordatorio_enviado BOOLEAN DEFAULT FALSE");
            jdbcTemplate.execute("UPDATE citas SET recordatorio_enviado = FALSE WHERE recordatorio_enviado IS NULL");
            System.out.println("✅ Columna 'recordatorio_enviado' verificada y NULLs corregidos.");
        } catch (Exception e) {
            System.out.println("ℹ️ DatabaseFixer: " + e.getMessage());
        }
    }
}
