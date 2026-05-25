import os
import json
import random
import re
import unicodedata
import urllib.error
import urllib.request
import uuid
from collections import defaultdict, deque
from datetime import datetime, timezone

from dotenv import load_dotenv
from flask import Flask, jsonify, request
from flask_cors import CORS


BASE_DIR = os.path.dirname(os.path.abspath(__file__))
load_dotenv(os.path.join(BASE_DIR, ".env"))

app = Flask(__name__)
CORS(app, resources={r"/*": {"origins": os.getenv("CHATBOT_ALLOWED_ORIGINS", "*")}})


SMILE = "\U0001F60A"
PAWS = "\U0001F43E"
DOG = "\U0001F436"
CAT = "\U0001F431"
SYRINGE = "\U0001F489"
BATH = "\U0001F6C1"
WARNING = "\u26A0\uFE0F"

SYSTEM_PROMPT = """
Eres el asistente virtual de Veterinaria Pet Clinic.
Hablas como una recepcionista veterinaria humana y amable.
Ayudas con citas, vacunas, mascotas, horarios y orientacion basica.
Nunca respondas como robot.
Manten conversaciones naturales.
Recuerda el contexto reciente del usuario.
Si el usuario proporciona datos para una cita, interpretalos correctamente.
Ejemplo:
Usuario: 'Brayan perro manana tarde'
Interpretacion:
Nombre: Brayan
Mascota: perro
Horario: tarde

Debes responder:
'Perfecto :) Tengo registrada una cita para Brayan con su perrito en horario de tarde. Ahora solo faltaria confirmar el motivo de consulta.'

Reglas de seguridad:
- No inventes diagnosticos medicos.
- No indiques medicamentos, dosis ni tratamientos sin evaluacion veterinaria.
- En casos graves como convulsiones, intoxicacion, sangrado, dificultad para respirar, atropello, dolor intenso o desmayo, recomienda acudir a una veterinaria de inmediato.
- Responde en espanol, con tono calido, profesional y breve.
- Usa emojis moderados, solo cuando aporten calidez.
- Evita parrafos enormes. Maximo 3 oraciones cortas.
""".strip()


GEMINI_API_KEY = os.getenv("GEMINI_API_KEY", "").strip()
if GEMINI_API_KEY == "tu_api_key_de_gemini":
    GEMINI_API_KEY = ""
GEMINI_MODEL = os.getenv("GEMINI_MODEL", "gemini-2.0-flash").strip()
GEMINI_TEMPERATURE = float(os.getenv("GEMINI_TEMPERATURE", "0.8"))
GEMINI_TIMEOUT = int(os.getenv("GEMINI_TIMEOUT", "20"))
MAX_HISTORY_MESSAGES = int(os.getenv("CHATBOT_HISTORY_LIMIT", "10"))

chat_histories = defaultdict(lambda: deque(maxlen=MAX_HISTORY_MESSAGES))
APP_VERSION = "2026-05-24-chatbot-natural-v2"


INTENT_KEYWORDS = {
    "emergency": (
        "emergencia", "urgencia", "convulsion", "convulsiona", "sangra", "sangrado",
        "no respira", "ahoga", "intoxic", "veneno", "atropell", "desmayo",
        "no se levanta", "dolor intenso",
    ),
    "appointment": ("cita", "agendar", "reservar", "manana", "tarde", "consulta", "turno"),
    "vaccines": ("vacuna", "vacunas", "vacunacion", "rabia", "parvovirus", "triple"),
    "baths": ("bano", "banos", "banio", "banios", "peluqueria", "grooming"),
    "deworming": ("desparasitar", "desparasitacion", "parasito", "pulga", "garrapata"),
    "hours": ("horario", "hora", "atienden", "abren", "cierran"),
    "feeding": ("alimento", "alimentacion", "comida", "croquetas", "dieta", "comer"),
    "symptoms": ("no come", "no quiere comer", "vomita", "diarrea", "decaido", "tos", "cojea", "dolor"),
    "pets": ("perro", "perros", "perrito", "perritos", "gato", "gatos", "gatito", "gatitos", "conejo", "hamster", "cobayo"),
    "greeting": ("hola", "buenos dias", "buenas tardes", "buenas noches"),
    "thanks": ("gracias", "muchas gracias", "te agradezco"),
    "farewell": ("adios", "chau", "hasta luego", "nos vemos"),
}

RESPONSE_BANK = {
    "greeting": [
        f"Hola {SMILE} Bienvenido a Veterinaria Pet Clinic. Cuentame, en que puedo ayudarte hoy con tu mascota?",
        f"Hola, que gusto atenderte {SMILE}. Puedo ayudarte con citas, vacunas, banos, horarios o cuidados para tu mascota.",
        f"Hola {PAWS} Soy el asistente de Pet Clinic. Dime que necesita tu mascota y te oriento con gusto.",
    ],
    "general": [
        f"Claro {SMILE}. Para ayudarte mejor, cuentame si tu consulta es sobre una cita, vacunas, banos, alimentacion o algun sintoma.",
        "Entiendo. Dame un poquito mas de detalle sobre tu mascota y lo que esta pasando, asi puedo orientarte mejor.",
        f"Con gusto te ayudo {PAWS}. Puedes preguntarme por horarios, servicios, citas o cuidados basicos.",
    ],
    "appointment": [
        f"Claro, te ayudo a coordinar la cita {SMILE}. Indicame nombre del cliente, tipo de mascota, horario de preferencia y motivo de consulta.",
        "Perfecto, podemos avanzar con la cita. Necesitaria nombre del cliente, mascota, horario aproximado y el motivo de la visita.",
        f"Si deseas agendar, pasame los datos principales {PAWS}: nombre, mascota, dia u horario, y motivo de consulta.",
    ],
    "symptoms": [
        f"Lamento que tu mascota este asi {PAWS}. No puedo dar un diagnostico por chat, pero si no come por mas de 24 horas, vomita, esta muy decaida o tiene dolor, lo mejor es una consulta veterinaria.",
        "Entiendo tu preocupacion. Observa si hay vomitos, diarrea, fiebre, dolor o decaimiento; si empeora o continua, conviene que la revise un veterinario.",
        f"Pobrecito {PAWS}. Puede deberse a varias causas, por eso es mejor no asumir un diagnostico. Si hay signos fuertes o persistentes, agenda una consulta cuanto antes.",
    ],
    "emergency": [
        f"{WARNING} Eso puede ser una emergencia. Si hay convulsiones, sangrado, intoxicacion, dificultad para respirar o no puede levantarse, llevalo a una veterinaria de inmediato.",
        "Lamento mucho que esten pasando por eso. Por los signos que mencionas, lo mas seguro es buscar atencion veterinaria urgente.",
    ],
    "vaccines": [
        f"Muy buena decision cuidar sus vacunas {SYRINGE}. El calendario depende de edad, especie e historial; si tienes su cartilla, podemos revisar que refuerzos necesita.",
        "Las vacunas ayudan a prevenir enfermedades importantes. Para orientarte bien, dime si es perro o gato y que edad tiene.",
    ],
    "baths": [
        f"Si, podemos orientarte con banos y cuidado de higiene {BATH}. Si tiene picazon, irritacion o mal olor fuerte, conviene revisarlo antes para cuidar su piel.",
        "Para banos, lo ideal es usar productos aptos para mascotas. La frecuencia depende del pelaje, piel y rutina de tu mascota.",
    ],
    "deworming": [
        "La desparasitacion es importante y debe ajustarse a edad, peso y especie. Lo ideal es indicarla con esos datos para evitar dosis incorrectas.",
        f"Podemos orientarte con desparasitacion interna y externa {PAWS}. Dime que mascota tienes y su peso aproximado.",
    ],
    "hours": [
        f"Con gusto {SMILE}. Nuestro horario habitual es de lunes a sabado de 8:00 a.m. a 7:00 p.m.",
        "Atendemos normalmente de lunes a sabado de 8:00 a.m. a 7:00 p.m. Si es una urgencia, conviene comunicarse directamente con la clinica.",
    ],
    "feeding": [
        "La alimentacion depende de especie, edad, peso y salud. En general, evita chocolate, cebolla, uvas, huesos cocidos y comida muy condimentada.",
        "Con gusto te oriento. Dime si es perro, gato u otra mascota, su edad aproximada y si tiene algun problema de salud.",
    ],
    "pets": [
        f"Claro {DOG}{CAT}. Atendemos perros, gatos y otras mascotas pequenas. Podemos ayudarte con consultas, vacunas, banos y desparasitacion.",
        "Si, trabajamos con mascotas comunes como perros y gatos. Dime que mascota tienes y que necesitas para orientarte mejor.",
    ],
    "thanks": [
        f"Con mucho gusto {SMILE}. Estoy aqui para ayudarte cuando necesites orientacion para tu mascota.",
        "De nada. Espero que tu mascota este muy bien; si tienes otra consulta, cuentame con confianza.",
    ],
    "farewell": [
        f"Hasta luego {SMILE}. Gracias por comunicarte con Veterinaria Pet Clinic.",
        "Nos vemos. Si notas algun signo preocupante en tu mascota, recuerda que lo mejor es una revision veterinaria.",
    ],
}


def normalize_text(text):
    normalized = unicodedata.normalize("NFD", text)
    without_accents = "".join(char for char in normalized if unicodedata.category(char) != "Mn")
    return without_accents.lower()


def get_session_id(payload):
    session_id = str(payload.get("session_id", "")).strip()
    return session_id or str(uuid.uuid4())


def looks_like_appointment_data(normalized_message):
    has_pet = any(pet in normalized_message for pet in ("perro", "gato", "conejo", "hamster", "cobayo"))
    has_time = any(
        time in normalized_message
        for time in ("manana", "tarde", "noche", "hoy", "lunes", "martes", "miercoles", "jueves", "viernes", "sabado")
    )
    has_name_like_word = bool(re.search(r"\b[a-zA-Z]{3,}\b", normalized_message))
    return has_pet and has_time and has_name_like_word


def contains_keyword(text, keyword):
    if " " in keyword:
        return keyword in text

    return re.search(rf"\b{re.escape(keyword)}\b", text) is not None


def detect_intent(message, history):
    normalized_message = normalize_text(message)
    words = re.findall(r"[a-zA-Z]+", normalized_message)
    is_short_message = len(words) <= 2

    immediate_intents = (
        "emergency", "symptoms", "greeting", "thanks", "farewell",
    )
    for intent in immediate_intents:
        if any(contains_keyword(normalized_message, keyword) for keyword in INTENT_KEYWORDS[intent]):
            return intent

    if any(contains_keyword(normalized_message, keyword) for keyword in INTENT_KEYWORDS["appointment"]):
        return "appointment"

    if looks_like_appointment_data(normalized_message):
        return "appointment"

    service_intents = ("vaccines", "baths", "deworming", "feeding")
    for intent in service_intents:
        if any(contains_keyword(normalized_message, keyword) for keyword in INTENT_KEYWORDS[intent]):
            return intent

    if any(contains_keyword(normalized_message, keyword) for keyword in INTENT_KEYWORDS["pets"]):
        return "pets"

    if any(contains_keyword(normalized_message, keyword) for keyword in INTENT_KEYWORDS["hours"]):
        return "hours"

    if not is_short_message and history:
        last_assistant = next(
            (item["content"] for item in reversed(history) if item.get("role") == "assistant"),
            "",
        )
        if "motivo de consulta" in normalize_text(last_assistant):
            return "appointment"

    return "general"


def serialize_history(history):
    if not history:
        return "Sin historial previo."

    lines = []
    for item in history:
        speaker = "Usuario" if item["role"] == "user" else "Asistente"
        lines.append(f"{speaker}: {item['content']}")

    return "\n".join(lines)


def build_user_prompt(message, intent, history):
    return f"""
Historial reciente:
{serialize_history(history)}

Intencion detectada: {intent}
Mensaje actual del usuario: {message}

Responde como recepcionista veterinaria de Pet Clinic. Mantente natural, breve y util.
Si el usuario esta dando datos para una cita, interpreta nombre, mascota, dia u horario y pregunta solo el dato que falte.
""".strip()


def generate_with_gemini(message, intent, history):
    if not GEMINI_API_KEY:
        return None

    url = (
        "https://generativelanguage.googleapis.com/v1beta/models/"
        f"{GEMINI_MODEL}:generateContent?key={GEMINI_API_KEY}"
    )
    payload = {
        "systemInstruction": {
            "parts": [{"text": SYSTEM_PROMPT}]
        },
        "contents": [
            {
                "role": "user",
                "parts": [{"text": build_user_prompt(message, intent, history)}],
            }
        ],
        "generationConfig": {
            "temperature": GEMINI_TEMPERATURE,
            "maxOutputTokens": 180,
        },
    }
    request_data = json.dumps(payload).encode("utf-8")
    gemini_request = urllib.request.Request(
        url,
        data=request_data,
        headers={"Content-Type": "application/json"},
        method="POST",
    )

    try:
        with urllib.request.urlopen(gemini_request, timeout=GEMINI_TIMEOUT) as response:
            data = json.loads(response.read().decode("utf-8"))
    except urllib.error.HTTPError as error:
        app.logger.warning("Gemini no respondio correctamente. HTTP %s", error.code)
        return None
    except (urllib.error.URLError, TimeoutError, ValueError) as error:
        app.logger.warning("Gemini no disponible: %s", error)
        return None

    candidates = data.get("candidates", [])
    if not candidates:
        return None

    parts = candidates[0].get("content", {}).get("parts", [])
    text = "\n".join(part.get("text", "") for part in parts).strip()
    return text or None


def extract_appointment_details(message):
    normalized = normalize_text(message)
    details = []
    pet_match = re.search(r"\b(perro|perrito|gato|gatito|conejo|hamster|cobayo)\b", normalized)
    time_match = re.search(r"\b(manana|tarde|noche|hoy|lunes|martes|miercoles|jueves|viernes|sabado)\b", normalized)
    name_match = re.search(r"\b([A-Z][a-z]{2,})\b", normalize_text(message).title())
    ignored_names = {
        "Perro", "Perrito", "Gato", "Gatito", "Conejo", "Hamster", "Cobayo",
        "Manana", "Tarde", "Noche", "Hoy", "Lunes", "Martes", "Miercoles",
        "Jueves", "Viernes", "Sabado", "Cita", "Consulta", "Agendar",
        "Reservar", "Hola", "Gracias",
    }

    if name_match and name_match.group(1) not in ignored_names:
        details.append(f"cliente {name_match.group(1)}")
    if pet_match:
        details.append(f"mascota {pet_match.group(1)}")
    if time_match:
        details.append(f"horario {time_match.group(1)}")

    return ", ".join(details)


def choose_response(intent, history):
    options = RESPONSE_BANK.get(intent, RESPONSE_BANK["general"])
    last_assistant = next(
        (item["content"] for item in reversed(history or []) if item.get("role") == "assistant"),
        "",
    )
    fresh_options = [option for option in options if option != last_assistant]
    return random.choice(fresh_options or options)


def fallback_response(message, intent, history=None):
    if intent == "emergency":
        return choose_response("emergency", history)

    if intent == "appointment":
        details = extract_appointment_details(message)
        if details:
            return (
                f"Perfecto {SMILE} Tengo anotado: {details}. Solo faltaria confirmar el motivo de consulta para coordinar mejor la atencion."
            )
        return choose_response("appointment", history)

    if intent in RESPONSE_BANK:
        return choose_response(intent, history)

    return choose_response("general", history)


def save_message(session_id, role, content):
    chat_histories[session_id].append(
        {
            "role": role,
            "content": content,
            "timestamp": datetime.now(timezone.utc).isoformat(),
        }
    )


def build_response(message, session_id):
    history = chat_histories[session_id]
    intent = detect_intent(message, history)
    gemini_response = generate_with_gemini(message, intent, history)
    provider = "gemini" if gemini_response else "local-fallback"
    response = gemini_response or fallback_response(message, intent, history)

    save_message(session_id, "user", message)
    save_message(session_id, "assistant", response)

    return {
        "response": response,
        "intent": intent,
        "session_id": session_id,
        "history_size": len(chat_histories[session_id]),
        "timestamp": datetime.now(timezone.utc).isoformat(),
        "provider": provider,
    }


@app.post("/chat")
def chat():
    payload = request.get_json(silent=True) or {}
    message = str(payload.get("message", "")).strip()
    session_id = get_session_id(payload)

    if not message:
        return jsonify({"error": "El campo 'message' es obligatorio."}), 400

    try:
        return jsonify(build_response(message, session_id))
    except (urllib.error.URLError, urllib.error.HTTPError, TimeoutError, ValueError, KeyError) as error:
        app.logger.exception("Error generando respuesta del chatbot")
        intent = detect_intent(message, chat_histories[session_id])
        response = fallback_response(message, intent, chat_histories[session_id])
        save_message(session_id, "user", message)
        save_message(session_id, "assistant", response)

        return jsonify(
            {
                "response": response,
                "intent": intent,
                "session_id": session_id,
                "provider": "local-fallback",
                "warning": str(error),
                "timestamp": datetime.now(timezone.utc).isoformat(),
            }
        )


@app.post("/reset")
def reset():
    payload = request.get_json(silent=True) or {}
    session_id = str(payload.get("session_id", "")).strip()

    if session_id:
        chat_histories.pop(session_id, None)

    return jsonify({"status": "ok", "session_id": session_id})


@app.get("/health")
def health():
    return jsonify(
        {
            "status": "ok",
            "service": "pet-clinic-chatbot",
            "gemini_enabled": bool(GEMINI_API_KEY),
            "model": GEMINI_MODEL if GEMINI_API_KEY else None,
            "version": APP_VERSION,
        }
    )


if __name__ == "__main__":
    port = int(os.getenv("PORT", "5000"))
    app.run(host="0.0.0.0", port=port, debug=os.getenv("FLASK_DEBUG") == "1")
