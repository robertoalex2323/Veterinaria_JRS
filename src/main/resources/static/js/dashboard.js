// Archivo js/dashboard.js
document.addEventListener('DOMContentLoaded', function() {
    // Las estadísticas y listas de citas actuales son renderizadas por Thymeleaf en el servidor.
    // cargarEstadisticas();
    // cargarCitasHoy();

    // --- UI Notifications Polling ---
    const fetchUINotifications = () => {
        fetch('/recepcionista/api/ui-notifications')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok ' + response.statusText);
                }
                return response.json();
            })
            .then(notifications => {
                notifications.forEach(notification => {
                    // Display notification using SweetAlert2 Toast
                    Swal.fire({
                        toast: true,
                        position: 'top-end',
                        showConfirmButton: false,
                        timer: 5000,
                        timerProgressBar: true,
                        icon: notification.type, // 'success', 'info', 'warning', 'error'
                        title: notification.message,
                        didOpen: (toast) => {
                            toast.addEventListener('mouseenter', Swal.stopTimer);
                            toast.addEventListener('mouseleave', Swal.resumeTimer);
                        }
                    });
                });
            })
            .catch(error => {
                console.error('Error fetching UI notifications:', error);
            });
    };

    // Poll for new notifications every 10 seconds
    setInterval(fetchUINotifications, 10000); 
    fetchUINotifications(); // Fetch immediately on page load
});

/*
function cargarEstadisticas() {
    // Lógica redundante
}

function cargarCitasHoy() {
    // Lógica redundante
}
*/