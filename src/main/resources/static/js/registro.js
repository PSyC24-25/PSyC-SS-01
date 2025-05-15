document.addEventListener("DOMContentLoaded", function () {
//BOTON GESTIONA ENVIAR
    let enviar = document.getElementById("enviar")

    function registrar(event) {
        event.preventDefault()

        let data = {
            "nombre": document.getElementById("nombre").value,
            "apellidos": document.getElementById("apellidos").value,
            "contrasena": document.getElementById("contrasena").value,
            "dni": document.getElementById("dni").value,
            "tipo": document.getElementById("tipo").value,
            "especialidad": document.getElementById("especialidad").value
        }

        let csrfToken = document.querySelector('meta[name="csrf-token"]').getAttribute('content');

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
                    console.log("Error al registrar usuario: ", data)
                }
            })
            .catch(error => {
                console.log("Error en el registro: ", error)
            })
    }
    enviar.addEventListener('click', registrar);

//GESTION SI ES MÉDICO O PACIENTE

    let userType = document.getElementById("tipo")
    userType.addEventListener('change', function() {
        let valor = userType.value
        let especialidadParent = document.getElementById("especialidadParent")
        let selection = document.getElementById("especialidad")

        if (valor === "medico") {
            especialidadParent.classList.remove("esconder");
            selection.disabled = false;

        } else {
            especialidadParent.classList.add("esconder");
            selection.disabled = true;
        }
    });
});