(function () {
    'use strict';

    window.addEventListener('load', () => {
        const forms = document.getElementsByClassName('signUp-Validation');

        for (const form of forms) {
            form.addEventListener('submit', (event) => {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            }, false);
        }
    }, false);
}());