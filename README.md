# 🐾 Veterinaria Pet Clinic
**Sistema Inteligente de Monitoreo y Seguimiento de Pacientes con IA**

![GitHub repo size](https://img.shields.io/github/repo-size/robertoalex2323/Veterinaria_Pet_Clinic?style=for-the-badge)
![GitHub last commit](https://img.shields.io/github/last-commit/robertoalex2323/Veterinaria_Pet_Clinic?style=for-the-badge)

Este proyecto propone el desarrollo de un **sistema inteligente de monitoreo y seguimiento** para veterinarias. Utiliza técnicas de **Machine Learning** como base de inteligencia artificial para mejorar la calidad de atención médica animal mediante el análisis de datos clínicos y el monitoreo continuo de las mascotas.

---

### 📌 El problema que resolvemos
| Problema | Impacto |
| :--- | :--- |
| **Falta de monitoreo preventivo** | Las enfermedades en mascotas suelen detectarse cuando ya están avanzadas. |
| **Gestión manual de datos** | La pérdida de información clínica dificulta diagnósticos precisos. |
| **Atención reactiva** | Se actúa solo ante la emergencia, reduciendo la esperanza de vida del paciente. |

### ✨ ¿Cómo funciona?
El sistema centraliza la información clínica y aplica modelos de IA para:
1.  **📊 Análisis Predictivo:** Identificar patrones de salud basados en historial clínico.
2.  **🐕 Seguimiento Inteligente:** Alertas personalizadas según la raza, edad y condición.
3.  **📈 Dashboard Médico:** Visualización de la evolución del paciente en tiempo real.

---

### 🏗️ Arquitectura del Proyecto (Módulo Recepcionista)
```text
Veterinaria_Pet_Clinic/
│
├── 📁 src/main/java/com/petclinic/
│   ├── 📁 controllers/      # Gestión de rutas (Módulo Recepcionista)
│   ├── 📁 models/           # Entidades (Mascota, Dueño, Cita)
│   ├── 📁 services/         # Lógica de negocio y ML
│   └── 📁 repository/       # Conexión a Base de Datos
│
├── 📁 resources/            # Configuraciones y recursos estáticos
├── 📄 pom.xml               # Dependencias de Spring Boot
└── 📖 README.md             # Documentación principal
