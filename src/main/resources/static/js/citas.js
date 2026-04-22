// Citas específico
document.addEventListener('DOMContentLoaded', function() {
    const fechaInput = document.getElementById('fechaCita');
    if (fechaInput) {
        fechaInput.addEventListener('change', cargarHorariosDisponibles);
    }
});

function cargarHorariosDisponibles() {
    const fecha = document.getElementById('fechaCita').value;
    const select = document.getElementById('horaCita');
    
    if (fecha && select) {
        fetch(`/api/recepcionista/horarios/disponibles?fecha=${fecha}`)
            .then(response => response.json())
            .then(horarios => {
                select.innerHTML = '<option value="">Seleccionar hora</option>';
                horarios.forEach(h => {
                    select.innerHTML += `<option value="${h}">${h}</option>`;
                });
            })
            .catch(error => console.error('Error:', error));
    }
}

function cancelarCita(id, mascota) {
    if (confirm(`¿Cancelar cita de "${mascota}"?`)) {
        window.location.href = `/recepcionista/citas/cancelar/${id}`;
    }
}