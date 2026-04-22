// Pagos específico
function filtrarPorEstado() {
    const estado = document.getElementById('filtroEstado').value;
    const rows = document.querySelectorAll('#pagosTable tbody tr');
    
    rows.forEach(row => {
        if (!estado || row.dataset.estado === estado) {
            row.style.display = '';
        } else {
            row.style.display = 'none';
        }
    });
}

function imprimirComprobante(id) {
    window.open(`/recepcionista/pagos/comprobante/${id}`, '_blank');
}