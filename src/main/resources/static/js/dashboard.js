// Archivo js/dashboard.js
document.addEventListener('DOMContentLoaded', function() {
    // Las estadísticas y listas de citas actuales son renderizadas por Thymeleaf en el servidor.
    // cargarEstadisticas();
    // cargarCitasHoy();
    let notificationPollingInterval;
    let consecutiveErrorCount = 0;
    const MAX_CONSECUTIVE_ERRORS = 5; 

    // --- Gestión de Historial de Notificaciones ---
    const notificationList = document.getElementById('notificationList');
    const notificationBadge = document.getElementById('notificationBadge');
    const clearNotificationsBtn = document.getElementById('clearNotifications');

    let history = JSON.parse(localStorage.getItem('notificationHistory') || '[]');

    const updateNotificationUI = () => {
        if (!notificationList || !notificationBadge) return;
        
        if (history.length === 0) {
            notificationList.innerHTML = '<li class="p-4 text-center text-muted small">No hay notificaciones recientes</li>';
            notificationBadge.classList.add('d-none');
        } else {
            notificationBadge.textContent = history.length;
            notificationBadge.classList.remove('d-none');
            
            notificationList.innerHTML = history.map(n => `
                <li class="p-3 border-bottom">
                    <div class="d-flex align-items-start">
                        <div class="me-2">
                            <i class="fas ${n.type === 'success' ? 'fa-check-circle text-success' : n.type === 'warning' ? 'fa-exclamation-triangle text-warning' : 'fa-info-circle text-info'}"></i>
                        </div>
                        <div class="flex-grow-1">
                            <div class="small text-dark fw-bold" style="line-height: 1.2;">${n.message}</div>
                            <div class="text-muted mt-1" style="font-size: 0.7rem;">${n.timestamp}</div>
                        </div>
                    </div>
                </li>
            `).join('');
        }
    };

    if (clearNotificationsBtn) {
        clearNotificationsBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            history = [];
            localStorage.setItem('notificationHistory', JSON.stringify(history));
            updateNotificationUI();
        });
    }

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
                consecutiveErrorCount = 0; // Resetear el contador de errores en caso de éxito
                if (notifications.length > 0) {
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

                        // Añadir al historial (máximo 10)
                        history.unshift(notification);
                });
                    if (history.length > 10) history = history.slice(0, 10);
                    localStorage.setItem('notificationHistory', JSON.stringify(history));
                    updateNotificationUI();
                }
            })
            .catch(error => {
                consecutiveErrorCount++;
                console.error(`Error fetching UI notifications (Intento ${consecutiveErrorCount}/${MAX_CONSECUTIVE_ERRORS}):`, error);
                if (consecutiveErrorCount >= MAX_CONSECUTIVE_ERRORS) {
                    clearInterval(notificationPollingInterval); // Detener el polling
                    console.error('Polling de notificaciones detenido debido a múltiples errores consecutivos.');
                    Swal.fire({
                        icon: 'error',
                        title: 'Error de Conexión',
                        text: 'No se pudieron cargar las notificaciones. Por favor, recargue la página o contacte a soporte.',
                        showConfirmButton: true
                    });
                }
            });
    };

    // Poll for new notifications every 10 seconds
    notificationPollingInterval = setInterval(fetchUINotifications, 10000); 
    fetchUINotifications(); // Fetch immediately on page load
    updateNotificationUI(); // Cargar historial inicial
});

/*
function cargarEstadisticas() {
    // Lógica redundante
}

function cargarCitasHoy() {
    // Lógica redundante
}
*/