document.addEventListener("DOMContentLoaded", () => {

    let fechas = document.getElementsByClassName("fecha");
    for (let i = 0; i < fechas.length; i++) {
        let fechaTexto = fechas[i].textContent;
        if (fechaTexto) {
            fechas[i].textContent = fechaTexto.replace('T', ' ');
            return;
        }
    }
});