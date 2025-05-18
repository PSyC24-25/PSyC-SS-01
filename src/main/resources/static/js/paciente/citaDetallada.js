document.addEventListener("DOMContentLoaded", function() {

    let url = window.location.pathname.split("/").pop();
    fetch("/api/paciente/citas/"+ url, {
        method: "GET",
        headers: {
            'Content-Type': 'application/json'
        }
    }).then(async (response) => {
        let data = await response.json();

        if (response.ok) {
            let med = document.getElementById("medico");
            let fecha = document.getElementById("fecha");
            let fechahora = document.getElementById("hora");
            let especialidad = document.getElementById("especialidad");
            let resumen = document.getElementById("resumen");

            med.textContent = data.medico.nombre
            fecha.textContent = data.fecha.split("T")[0];
            fechahora.textContent = data.fecha.split("T")[1];
            especialidad.textContent = data.medico.especialidad
            resumen.textContent = data.resumen

            const fechaString = new Date(data.fecha);
            const fechaActual = new Date();


            if (fechaString.getTime() < fechaActual.getTime()) {
                let boton = document.getElementById("cancelarBtn");
                boton.style.display = "none";
            }
        }
    }).catch(error => {
        console.log("Error: ", error)
    })

});
