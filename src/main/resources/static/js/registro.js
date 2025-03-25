document.addEventListener("DOMContentLoaded", function () {
//BOTON GESTIONA ENVIAR
    let enviar = document.getElementById("enviar")

    function registrar(event) {
        event.preventDefault()

        let data = {
            "nombre": document.getElementById("nombre").value,
            "apellido": document.getElementById("apellido").value,
            "contrasena": document.getElementById("contrasena").value,
            "dni": document.getElementById("dni").value,
            "tipo": document.getElementById("tipo").value,
            "especialidad": document.getElementById("especialidad").value
        }
        alert(JSON.stringify(data))


        let csrfToken = document.querySelector('meta[name="csrf-token"]').getAttribute('content');

        fetch('/registro', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': csrfToken
            },
            body: JSON.stringify(data)
        })
            .then(response => response.json())
            .then(data => {
                console.log("Usuario registrado con éxito: ", data)
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
        let selection = document.getElementById("especialidad")

        if (valor === "medico") {
            selection.style.visibility = "visible";
            selection.disabled = false;

        } else {
            selection.style.visibility = "hidden";
            selection.disabled = true;
        }
    });
});