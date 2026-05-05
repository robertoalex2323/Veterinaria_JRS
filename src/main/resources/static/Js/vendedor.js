document.addEventListener("DOMContentLoaded", () => {
    initDate();
    initTabs();
    loadPromotions();
    loadHistory(); // load initial mock data
});

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
    document.getElementById("paymentMethod").selectedIndex = 0;
    document.getElementById("saleTotalAmount").textContent = "S/ 0.00";
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
    const total = calculateTotal();

    if (!select.value || total <= 0) {
        showToast("Por favor seleccione un producto válido.", "error");
        return;
    }
    if (!client) {
        showToast("Por favor ingrese el nombre del cliente.", "error");
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

// Toast Notification
function showToast(message, type = "success") {
    const toast = document.getElementById("toast");
    const msgEl = document.getElementById("toastMessage");
    const icon = toast.querySelector("i");
    
    msgEl.textContent = message;
    
    if (type === "error") {
        toast.style.borderLeftColor = "var(--accent-color)";
        icon.className = "fa-solid fa-circle-exclamation";
        icon.style.color = "var(--accent-color)";
    } else {
        toast.style.borderLeftColor = "var(--success-color)";
        icon.className = "fa-solid fa-circle-check";
        icon.style.color = "var(--success-color)";
    }

    toast.classList.add("show");
    
    setTimeout(() => {
        toast.classList.remove("show");
    }, 3000);
}
