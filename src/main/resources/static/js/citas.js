document.addEventListener('DOMContentLoaded', function() {
    // Submit form automatically when date changes
    const fechaInput = document.getElementById('fechaFiltro');
    if (fechaInput) {
        fechaInput.addEventListener('change', function() {
            document.getElementById('filtroCitasForm').submit();
        });
    }

    // Initialize Select2 if available for better pet searching in forms
    if (typeof jQuery !== 'undefined' && typeof jQuery.fn.select2 !== 'undefined') {
        $('#mascotaId').select2({
            theme: 'bootstrap-5',
            placeholder: 'Busca una mascota o cliente...'
        });
    }
});

function confirmarCancelacion(id) {
    const motivo = prompt('Por favor, ingrese el motivo de cancelación:');
    if (motivo !== null) {
        // Enviar con motivo
        window.location.href = `/recepcionista/citas/cancelar/${id}?motivo=${encodeURIComponent(motivo)}`;
    }
}