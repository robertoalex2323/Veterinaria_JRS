document.addEventListener("DOMContentLoaded", () => {
    initDate();
    initTabs();
    loadPromotions();
    loadHistory();
    updateStockIndicator();
});

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
    const filterDate = document.getElementById("filterDate").value;
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
