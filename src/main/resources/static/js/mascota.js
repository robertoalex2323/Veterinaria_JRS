// Mascotas específico
function filtrarPorEspecie() {
    const especie = document.getElementById('filtroEspecie').value;
    const rows = document.querySelectorAll('#mascotasTable tbody tr');
    
    rows.forEach(row => {
        if (!especie || row.dataset.especie === especie) {
            row.style.display = '';
        } else {
            row.style.display = 'none';
        }
    });
}

function calcularEdad() {
    const fechaNac = document.getElementById('fechaNacimiento');
    const edadInput = document.getElementById('edad');
    
    if (fechaNac && fechaNac.value) {
        const nacimiento = new Date(fechaNac.value);
        const hoy = new Date();
        let edad = hoy.getFullYear() - nacimiento.getFullYear();
        const mes = hoy.getMonth() - nacimiento.getMonth();
        if (mes < 0 || (mes === 0 && hoy.getDate() < nacimiento.getDate())) {
            edad--;
        }
        edadInput.value = edad > 0 ? edad : 0;
    }
}