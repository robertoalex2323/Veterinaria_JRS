document.addEventListener('DOMContentLoaded', function() {

    // =========================
    // TOGGLE PASSWORD
    // =========================
    const togglePassword = document.getElementById('togglePassword');
    const passwordInput = document.getElementById('password');

    if (togglePassword && passwordInput) {
        togglePassword.addEventListener('click', function() {
            const type = passwordInput.getAttribute('type') === 'password'
                ? 'text'
                : 'password';

            passwordInput.setAttribute('type', type);

            const icon = this.querySelector('i');
            if (icon) {
                icon.classList.toggle('fa-eye');
                icon.classList.toggle('fa-eye-slash');
            }
        });
    }

    // =========================
    // LOGIN UX (SPRING SECURITY)
    // =========================
    const loginForm = document.getElementById('loginForm');
    const btnLogin = document.querySelector('.btn-login');

    if (loginForm && btnLogin) {
        loginForm.addEventListener('submit', function() {

            // NO preventDefault → deja que Spring Security procese

            btnLogin.innerHTML = '<i class="fa-solid fa-circle-notch fa-spin"></i> Ingresando...';
            btnLogin.disabled = true;
        });
    }

    // =========================
    // ANIMACIÓN INPUTS
    // =========================
    const inputs = document.querySelectorAll('.input-field input, input');

    inputs.forEach((input, index) => {
        input.style.opacity = '0';
        input.style.transform = 'translateY(10px)';
        input.style.transition = 'all 0.4s ease';

        setTimeout(() => {
            input.style.opacity = '1';
            input.style.transform = 'translateY(0)';
        }, 100 * (index + 1));
    });

});