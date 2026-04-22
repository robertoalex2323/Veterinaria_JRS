// Perfil específico
document.addEventListener('DOMContentLoaded', function() {
    const editBtn = document.getElementById('editarPerfil');
    if (editBtn) {
        editBtn.addEventListener('click', mostrarFormularioEdicion);
    }
});

function mostrarFormularioEdicion() {
    const viewMode = document.getElementById('viewMode');
    const editMode = document.getElementById('editMode');
    
    if (viewMode && editMode) {
        viewMode.style.display = 'none';
        editMode.style.display = 'block';
    }
}

function cancelarEdicion() {
    const viewMode = document.getElementById('viewMode');
    const editMode = document.getElementById('editMode');
    
    if (viewMode && editMode) {
        viewMode.style.display = 'block';
        editMode.style.display = 'none';
    }
}

function cambiarPassword() {
    const nuevaPass = prompt('Ingrese nueva contraseña:');
    if (nuevaPass && nuevaPass.length >= 6) {
        fetch('/api/recepcionista/cambiar-password', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ password: nuevaPass })
        })
        .then(response => response.json())
        .then(data => {
            alert(data.message || 'Contraseña actualizada');
        })
        .catch(error => alert('Error al cambiar contraseña'));
    } else if (nuevaPass) {
        alert('La contraseña debe tener al menos 6 caracteres');
    }
}