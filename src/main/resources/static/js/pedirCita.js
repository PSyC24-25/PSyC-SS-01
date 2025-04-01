document.addEventListener("DOMContentLoaded", function(){

    let enviar = document.getElementById("enviar")

    function crearCita(event){
        event.preventDefault()

        let data = {
            "fecha": document.getElementById("fecha").value,
            "hora": document.getElementById("hora").value,
            "idMedico": document.getElementById("medicoEspecialidad").value,
            "resumen": document.getElementById("resumen").value
        }

        fetch("/paciente/pedirCita", {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        })
            .then(async (response) => {
                if(response.ok) {
                    window.location.href = (data.tipoUsuario === "paciente" ? "/paciente/" : "/medico/");
                } else {
                    let data = await response.json();
                    console.log("Error al crear la cita: ", data)
                }
            })
            .catch(error => {
                console.log("Error en la creación de la cita: ", error)
            })

    }

    enviar.addEventListener('click', crearCita)



});
