let tipoUsuario = "paciente";

const botonesTipoUsuario = document.querySelectorAll(".tipoUsuario > div");
botonesTipoUsuario.forEach((boton) => boton.addEventListener("click", () => {
    botonesTipoUsuario.forEach((boton) => boton.classList.remove("activo"));
    boton.classList.add("activo")

    tipoUsuario = boton.getAttribute("data-valor")
    console.log(tipoUsuario)

    let especialidadParent = document.getElementById("especialidadParent")
    let selection = document.getElementById("especialidad")

    if (tipoUsuario === "medico") {
        especialidadParent.classList.remove("esconder");
        selection.disabled = false;
    } else {
        especialidadParent.classList.add("esconder");
        selection.disabled = true;
    }
}))

document.addEventListener("DOMContentLoaded", function () {
//BOTON GESTIONA ENVIAR
    let enviar = document.getElementById("enviar")

    function registrar(event) {
        event.preventDefault()

        let data = {
            "nombre": document.getElementById("nombre").value,
            "apellidos": document.getElementById("apellidos").value,
            "contrasena": document.getElementById("contrasena").value,
            "contrasena2": document.getElementById("contrasena2").value,
            "dni": document.getElementById("dni").value,
            "tipo": tipoUsuario,
            "especialidad": document.getElementById("especialidad").value
        }

        let csrfToken = document.querySelector('meta[name="csrf-token"]').getAttribute('content');

        document.querySelectorAll(`input.error`).forEach((input) => input.classList.remove("error"))
        document.querySelector(".mensajeError").classList.add("esconder");

        fetch('/api/auth/registro', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': csrfToken
            },
            body: JSON.stringify(data)
        })
            .then(async (response) => {
                if(response.ok) {
                    window.location.href = (data.tipo === "paciente" ? "/paciente/" : "/medico/");
                } else {
                    let data = await response.json();

                    document.querySelector(".mensajeError").classList.remove("esconder");
                    document.querySelector(".mensajeError > .texto").textContent = data.error;
                    for(const campo of data.campos) {
                        console.log(campo)
                        document.querySelector(`[id=${campo}]`).classList.add("error")
                    }
                }
            })
            .catch(error => {
                console.log("Error en el registro: ", error)
            })
    }
    enviar.addEventListener('click', registrar);
});