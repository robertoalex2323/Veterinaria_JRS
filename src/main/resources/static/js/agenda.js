// Agenda específico
function cambiarFecha(dias) {
    const fechaActual = document.getElementById('fechaActual').value;
    const nuevaFecha = new Date(fechaActual);
    nuevaFecha.setDate(nuevaFecha.getDate() + dias);
    
    window.location.href = `/recepcionista/agenda?fecha=${nuevaFecha.toISOString().split('T')[0]}`;
}

function reservarHorario(id, hora) {
    if (confirm(`¿Reservar horario de las ${hora}?`)) {
        window.location.href = `/recepcionista/citas/nueva?horarioId=${id}`;
    }
}