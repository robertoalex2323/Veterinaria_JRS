document.addEventListener("DOMContentLoaded", () => {
    initDate();
    initTabs();
    loadPromotions();
    loadHistory();
    updateStockIndicator();
    setMaxDateFilter(); // Establecer límite de fecha
    renderSalesChart(); // Renderizar gráfica
});

// Establecer fecha máxima permitida en el filtro (hoy)
function setMaxDateFilter() {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');
    const maxDate = `${year}-${month}-${day}`;
    
    const filterDateInput = document.getElementById("filterDate");
    if (filterDateInput) {
        filterDateInput.setAttribute("max", maxDate);
    }
}

function initHistoryFilters() {
    const searchClient = document.getElementById("searchClient");
    const filterDate = document.getElementById("filterDate");
    if (searchClient) searchClient.addEventListener("input", filterHistory);
    if (filterDate) filterDate.addEventListener("change", filterHistory);
}

function initDate() {
    const dateEl = document.getElementById("currentDate");
    const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
    const today = new Date().toLocaleDateString('es-PE', options);
    dateEl.textContent = today.charAt(0).toUpperCase() + today.slice(1);
}

// Tab Navigation
function initTabs() {
    const navItems = document.querySelectorAll(".nav-item");
    navItems.forEach(item => {
        item.addEventListener("click", () => {
            const tabId = item.getAttribute("data-tab");
            switchTab(tabId);
        });
    });
}

function switchTab(tabId) {
    // Update active nav item
    document.querySelectorAll(".nav-item").forEach(el => el.classList.remove("active"));
    const targetNav = document.querySelector(`.nav-item[data-tab="${tabId}"]`);
    if(targetNav) targetNav.classList.add("active");

    // Hide all panels
    document.querySelectorAll(".panel-container").forEach(panel => {
        panel.classList.remove("active");
        setTimeout(() => panel.style.display = "none", 50); // slight delay for smooth exit if animated
    });

    // Show target panel
    const targetPanel = document.getElementById(`${tabId}Panel`);
    if (targetPanel) {
        setTimeout(() => {
            targetPanel.style.display = "block";
            targetPanel.classList.add("active");
        }, 60);
    }
}

// Sale Logic
function updatePrice() {
    const select = document.getElementById("saleProduct");
    const selectedOption = select.options[select.selectedIndex];
    const price = selectedOption.getAttribute("data-price") || "0";
    document.getElementById("salePrice").value = parseFloat(price).toFixed(2);
    updateStockIndicator();
    validateQuantity();
    calculateTotal();
}

function calculateTotal() {
    const price = parseFloat(document.getElementById("salePrice").value) || 0;
    const quantity = parseInt(document.getElementById("saleQuantity").value) || 1;
    const total = price * quantity;
    document.getElementById("saleTotalAmount").textContent = `S/ ${total.toFixed(2)}`;
    return total;
}

function resetSaleForm() {
    document.getElementById("saleProduct").selectedIndex = 0;
    document.getElementById("saleQuantity").value = 1;
    document.getElementById("salePrice").value = "0.00";
    document.getElementById("clientName").value = "";
    document.getElementById("clientName").classList.remove("input-error");
    document.getElementById("paymentMethod").selectedIndex = 0;
    document.getElementById("saleTotalAmount").textContent = "S/ 0.00";
    document.getElementById("quantityError").style.display = "none";
    document.getElementById("clientError").style.display = "none";
    updateStockIndicator();
}

// Mock Sales DB
let salesHistory = [
    { id: "B001-00045", date: new Date().toLocaleString('es-PE'), client: "María Gonzáles", product: "Vacuna Múltiple", total: 60.00, status: "Pagado" },
    { id: "B001-00044", date: new Date(Date.now() - 3600000).toLocaleString('es-PE'), client: "Carlos Ruíz", product: "Alimento Premium Perro 15kg", total: 150.00, status: "Pagado" }
];

let todaySalesTotal = 210.00;
let todayOperationsCount = 2;

function processSale() {
    const select = document.getElementById("saleProduct");
    const product = select.options[select.selectedIndex].text;
    const client = document.getElementById("clientName").value.trim();
    const quantity = parseInt(document.getElementById("saleQuantity").value) || 0;
    const total = calculateTotal();
    const selectedOption = select.options[select.selectedIndex];
    const stock = parseInt(selectedOption.getAttribute("data-stock")) || 0;

    // Validaciones
    if (!select.value || total <= 0) {
        showToast("Por favor seleccione un producto válido.", "error");
        return;
    }
    if (!client || client.length < 3) {
        showToast("Por favor ingrese un nombre de cliente válido (mín. 3 caracteres).", "error");
        return;
    }
    if (quantity <= 0) {
        showToast("La cantidad debe ser mayor a 0.", "error");
        return;
    }
    if (stock > 0 && quantity > stock) {
        showToast(`No hay suficiente stock. Disponible: ${stock} unidades.`, "error");
        return;
    }

    // Add to history
    const newSale = {
        id: `B001-${String(salesHistory.length + 45).padStart(5, '0')}`,
        date: new Date().toLocaleString('es-PE'),
        client: client,
        product: product,
        total: total,
        status: "Pagado"
    };

    salesHistory.unshift(newSale); // add to top
    
    // Update Dashboard Stats
    todaySalesTotal += total;
    todayOperationsCount++;
    document.getElementById("todaySales").textContent = `S/ ${todaySalesTotal.toFixed(2)}`;
    document.getElementById("todayOperations").textContent = todayOperationsCount;
    document.getElementById("todayClients").textContent = todayOperationsCount; // Assuming 1 operation = 1 client for simplicity

    showToast("Venta registrada con éxito.");
    resetSaleForm();
    loadHistory(); // refresh table
    refreshChart(); // refresh chart
}

function loadHistory() {
    const tbody = document.getElementById("salesTableBody");
    tbody.innerHTML = "";

    if (salesHistory.length === 0) {
        tbody.innerHTML = `<tr><td colspan="6" style="text-align: center; color: var(--text-secondary);">No hay ventas registradas hoy.</td></tr>`;
        return;
    }

    salesHistory.forEach(sale => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td><strong>${sale.id}</strong></td>
            <td style="color: var(--text-secondary); font-size: 0.85rem;">${sale.date}</td>
            <td>${sale.client}</td>
            <td>${sale.product}</td>
            <td style="font-weight: 600; color: var(--success-color);">S/ ${sale.total.toFixed(2)}</td>
            <td><span class="badge badge-success">${sale.status}</span></td>
        `;
        tbody.appendChild(tr);
    });
}

// Fetch Promotions from API
async function loadPromotions() {
    try {
        const response = await fetch('/api/v1/vendedor/promociones/activas');
        if (response.ok) {
            const promociones = await response.json();
            const listEl = document.getElementById("promosList");
            listEl.innerHTML = "";
            
            document.getElementById("activePromos").textContent = promociones.length;

            promociones.forEach(promo => {
                const item = document.createElement("div");
                item.className = "promo-item";
                item.innerHTML = `
                    <i class="fa-solid fa-bolt"></i>
                    <p>${promo}</p>
                `;
                listEl.appendChild(item);
            });
        }
    } catch (error) {
        console.error("Error cargando promociones:", error);
        // Fallback mock data in case API fails
        const listEl = document.getElementById("promosList");
        listEl.innerHTML = `
            <div class="promo-item"><i class="fa-solid fa-bolt"></i><p>10% de descuento en Alimentos Premium por compras mayores a S/120</p></div>
            <div class="promo-item"><i class="fa-solid fa-bolt"></i><p>2x1 en Juguetes y Accesorios los días Martes y Viernes</p></div>
        `;
    }
}

// Validación de cantidad en tiempo real
function validateQuantity() {
    const select = document.getElementById("saleProduct");
    const quantity = parseInt(document.getElementById("saleQuantity").value) || 0;
    const selectedOption = select.options[select.selectedIndex];
    const stock = parseInt(selectedOption.getAttribute("data-stock")) || 0;
    const quantityError = document.getElementById("quantityError");
    
    if (stock > 0 && quantity > stock) {
        quantityError.textContent = `Stock insuficiente. Disponible: ${stock}`;
        quantityError.style.display = "block";
        document.getElementById("saleQuantity").classList.add("input-error");
    } else {
        quantityError.style.display = "none";
        document.getElementById("saleQuantity").classList.remove("input-error");
    }
}

// Validación del nombre del cliente en tiempo real
function validateClientName() {
    const clientName = document.getElementById("clientName").value.trim();
    const clientError = document.getElementById("clientError");
    
    if (clientName && clientName.length < 3) {
        clientError.textContent = "Mínimo 3 caracteres";
        clientError.style.display = "block";
        document.getElementById("clientName").classList.add("input-error");
    } else if (!clientName) {
        clientError.style.display = "none";
        document.getElementById("clientName").classList.remove("input-error");
    } else {
        clientError.style.display = "none";
        document.getElementById("clientName").classList.remove("input-error");
    }
}

// Actualizar indicador de stock cuando cambia el producto
function updateStockIndicator() {
    const select = document.getElementById("saleProduct");
    const selectedOption = select.options[select.selectedIndex];
    const stock = parseInt(selectedOption.getAttribute("data-stock")) || 0;
    const indicator = document.getElementById("stockIndicator");
    
    if (stock === 0) {
        indicator.textContent = "Sin Stock";
        indicator.classList.remove("low");
        indicator.classList.add("out");
        select.classList.add("input-error");
    } else if (stock <= 10) {
        indicator.textContent = `Stock bajo: ${stock}`;
        indicator.classList.remove("out");
        indicator.classList.add("low");
        select.classList.remove("input-error");
    } else {
        indicator.textContent = `${stock} disponibles`;
        indicator.classList.remove("out", "low");
        select.classList.remove("input-error");
    }
}

// Filtrar historial por cliente y fecha
function filterHistory() {
    const searchClient = document.getElementById("searchClient").value.toLowerCase();
    let filterDate = document.getElementById("filterDate").value;
    
    // Validación adicional: rechazar fechas futuras
    if (filterDate) {
        const selectedDate = new Date(filterDate + "T00:00:00");
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        
        if (selectedDate > today) {
            showToast("No puedes seleccionar fechas futuras. Se mostrarán todas las ventas.", "error");
            document.getElementById("filterDate").value = "";
            filterDate = "";
        }
    }
    
    const tbody = document.getElementById("salesTableBody");
    const noResults = document.getElementById("noResults");
    const rows = tbody.querySelectorAll("tr");
    let visibleCount = 0;
    
    rows.forEach(row => {
        const cells = row.querySelectorAll("td");
        if (cells.length >= 3) {
            const client = cells[2].textContent.toLowerCase();
            const date = cells[1].textContent;
            const dateOnly = date.split(" ")[0]; // Extract date part
            
            const matchClient = client.includes(searchClient);
            const matchDate = !filterDate || dateOnly === filterDate;
            
            if (matchClient && matchDate) {
                row.style.display = "table-row";
                visibleCount++;
            } else {
                row.style.display = "none";
            }
        }
    });
    
    noResults.style.display = visibleCount === 0 ? "block" : "none";
}

// Exportar historial a CSV/Excel
function exportarHistorial() {
    const searchClient = document.getElementById("searchClient").value.toLowerCase();
    const filterDate = document.getElementById("filterDate").value;
    
    let filteredSales = salesHistory.filter(sale => {
        const matchClient = sale.client.toLowerCase().includes(searchClient);
        const saleDate = sale.date.split(" ")[0];
        const matchDate = !filterDate || saleDate === filterDate;
        return matchClient && matchDate;
    });
    
    if (filteredSales.length === 0) {
        showToast("No hay datos para exportar con los filtros aplicados.", "error");
        return;
    }
    
    // Crear CSV
    let csv = "N° Boleta,Fecha,Cliente,Producto,Total,Estado\n";
    filteredSales.forEach(sale => {
        csv += `"${sale.id}","${sale.date}","${sale.client}","${sale.product}","S/ ${sale.total.toFixed(2)}","${sale.status}"\n`;
    });
    
    // Crear blob y descargar
    const blob = new Blob([csv], { type: "text/csv;charset=utf-8;" });
    const link = document.createElement("a");
    const url = URL.createObjectURL(blob);
    const timestamp = new Date().toISOString().slice(0, 10);
    
    link.setAttribute("href", url);
    link.setAttribute("download", `Historial_Ventas_${timestamp}.csv`);
    link.style.visibility = "hidden";
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    
    showToast(`Exportados ${filteredSales.length} registros exitosamente.`);
}

// Toast Notification mejorado
function showToast(message, type = "success") {
    const toast = document.getElementById("toast");
    const msgEl = document.getElementById("toastMessage");
    const icon = toast.querySelector("i");
    
    msgEl.textContent = message;
    
    if (type === "error") {
        toast.style.borderLeftColor = "var(--accent-color)";
        icon.className = "fa-solid fa-circle-exclamation";
        icon.style.color = "var(--accent-color)";
        toast.style.background = "rgba(244, 63, 94, 0.05)";
    } else {
        toast.style.borderLeftColor = "var(--success-color)";
        icon.className = "fa-solid fa-circle-check";
        icon.style.color = "var(--success-color)";
        toast.style.background = "rgba(16, 185, 129, 0.05)";
    }

    toast.classList.add("show");
    
    setTimeout(() => {
        toast.classList.remove("show");
    }, 4000);
}

// ============ SALES CHART ============

// Variable global para la gráfica
let salesChart = null;

// Generar datos de ventas de los últimos 7 días
function getSalesData() {
    const data = [];
    const labels = [];
    
    for (let i = 6; i >= 0; i--) {
        const date = new Date();
        date.setDate(date.getDate() - i);
        const dateStr = date.toLocaleDateString('es-PE', { month: 'short', day: 'numeric' });
        labels.push(dateStr);
        
        // Calcular ventas del día basado en salesHistory
        let dailySales = 0;
        const dayStart = new Date(date);
        dayStart.setHours(0, 0, 0, 0);
        const dayEnd = new Date(date);
        dayEnd.setHours(23, 59, 59, 999);
        
        salesHistory.forEach(sale => {
            const saleDate = new Date(sale.date);
            if (saleDate >= dayStart && saleDate <= dayEnd) {
                dailySales += sale.total;
            }
        });
        
        data.push(dailySales);
    }
    
    return { labels, data };
}

// Renderizar gráfica de ventas
function renderSalesChart() {
    const canvas = document.getElementById("salesChart");
    if (!canvas) return;
    
    // Destruir gráfica anterior si existe
    if (salesChart) {
        salesChart.destroy();
    }
    
    const { labels, data } = getSalesData();
    
    // Calcular estadísticas
    const totalSales = data.reduce((a, b) => a + b, 0);
    const avgSales = totalSales / data.length;
    const maxSales = Math.max(...data);
    const minSales = Math.min(...data);
    
    // Crear contexto de gráfica
    const ctx = canvas.getContext('2d');
    
    // Calcular línea de promedio
    const avgLine = Array(data.length).fill(avgSales);
    
    salesChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [
                {
                    label: 'Ventas (S/)',
                    data: data,
                    borderColor: '#059669',
                    backgroundColor: 'rgba(5, 150, 105, 0.1)',
                    borderWidth: 3,
                    fill: true,
                    pointRadius: 6,
                    pointHoverRadius: 8,
                    pointBackgroundColor: '#059669',
                    pointBorderColor: '#fff',
                    pointBorderWidth: 2,
                    tension: 0.4,
                    spanGaps: true
                },
                {
                    label: 'Promedio (S/)',
                    data: avgLine,
                    borderColor: '#f59e0b',
                    borderWidth: 2,
                    borderDash: [5, 5],
                    fill: false,
                    pointRadius: 0,
                    pointHoverRadius: 0,
                    tension: 0
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: true,
                    position: 'top',
                    labels: {
                        font: {
                            size: 13,
                            weight: '600',
                            family: "'Playfair Display', serif"
                        },
                        color: '#64748b',
                        padding: 15,
                        usePointStyle: true
                    }
                },
                tooltip: {
                    backgroundColor: 'rgba(0, 0, 0, 0.8)',
                    padding: 12,
                    titleFont: { size: 14, weight: 'bold' },
                    bodyFont: { size: 13 },
                    callbacks: {
                        label: function(context) {
                            return context.dataset.label + ': S/ ' + context.parsed.y.toFixed(2);
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: function(value) {
                            return 'S/ ' + value.toFixed(0);
                        },
                        font: { size: 12 },
                        color: '#64748b'
                    },
                    grid: {
                        color: 'rgba(203, 213, 225, 0.1)',
                        drawBorder: false
                    }
                },
                x: {
                    ticks: {
                        font: { size: 12 },
                        color: '#64748b'
                    },
                    grid: {
                        display: false,
                        drawBorder: false
                    }
                }
            }
        }
    });
    
    // Actualizar estadísticas
    updateChartStats(totalSales, avgSales, maxSales);
}

// Actualizar estadísticas bajo la gráfica
function updateChartStats(total, avg, max) {
    const statsEl = document.getElementById("chartStats");
    if (!statsEl) return;
    
    const prevTotal = window.lastTotalSales || 0;
    const changePercent = prevTotal > 0 ? ((total - prevTotal) / prevTotal * 100).toFixed(0) : 0;
    const changeClass = total >= prevTotal ? '' : 'negative';
    const changeSymbol = total >= prevTotal ? '↑' : '↓';
    
    statsEl.innerHTML = `
        <div class="chart-stat">
            <div class="chart-stat-label">Total 7 Días</div>
            <div class="chart-stat-value">S/ ${total.toFixed(2)}</div>
            <div class="chart-stat-change ${changeClass}">${changeSymbol} ${Math.abs(changePercent)}%</div>
        </div>
        <div class="chart-stat">
            <div class="chart-stat-label">Promedio/Día</div>
            <div class="chart-stat-value">S/ ${avg.toFixed(2)}</div>
        </div>
        <div class="chart-stat">
            <div class="chart-stat-label">Máximo/Día</div>
            <div class="chart-stat-value">S/ ${max.toFixed(2)}</div>
        </div>
    `;
    
    window.lastTotalSales = total;
}

// Actualizar gráfica cuando se registra una nueva venta
function refreshChart() {
    if (document.getElementById("salesChart")) {
        renderSalesChart();
    }
}
