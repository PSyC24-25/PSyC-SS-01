const diasSemana = ["Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"]
const fechaBase = new Date();
const DIAS_A_MOSTRAR = 3;

let fechaSeleccionada = null;
let offsetDiasFechaBase = 0;

const formulario = document.getElementById("formulario");

const calendario = document.getElementById("calendario");
const sugerenciaCalendario = document.getElementById("sugerencia-calendario");

const calendarioIzquierda = document.getElementById("calendarioIzquierda");
const calendarioDerecha = document.getElementById("calendarioDerecha");

const medicoEspecialidadSelect = document.getElementById("medicoEspecialidad");

const razonCita= document.getElementById("razon");

const actualizarCalendario = () => {
    fechaSeleccionada = null;
    const idMedico = +medicoEspecialidadSelect.value;

    document.getElementById("dias").innerHTML = "";

    if(idMedico < 0) {
        calendario.classList.remove("mostrar");
        sugerenciaCalendario.classList.add("mostrar");
        return;
    }

    calendario.classList.add("mostrar");
    sugerenciaCalendario.classList.remove("mostrar");

    let fechas = []
    for(let i = 0; i < DIAS_A_MOSTRAR; i++) {
        const fecha = new Date();
        fecha.setDate(fechaBase.getDate() + offsetDiasFechaBase + i);
        fechas.push(fecha)
    }

    fechas.forEach((fecha) => {
        const diaDiv = document.createElement("div");
        diaDiv.classList.add("dia");
        diaDiv.innerHTML = `<div class="titulo">${fecha.getDate()}/${fecha.getMonth()+1}/${fecha.getFullYear()}, ${diasSemana[fecha.getDay()]}</div>`

        const horasDiv = document.createElement("div");
        horasDiv.classList.add("horas");

        fetch(`/api/paciente/citas/disponibles?anyo=${fecha.getFullYear()}&mes=${fecha.getMonth()+1}&dia=${fecha.getDate()}&medico=${idMedico}`)
            .then(async (response) => {
                let horasDisponibles = await response.json();
                if(horasDisponibles.length === 0) {
                    const noHorasDiv = document.createElement("div");
                    noHorasDiv.classList.add("noHoras");
                    noHorasDiv.innerText = "No hay horas disponibles para este día.";
                    diaDiv.appendChild(noHorasDiv);
                    return;
                }

                horasDisponibles.forEach((fecha) => {
                    // Java serializa la fecha/hora con formato: [YY, MM, DD, HH, MM]
                    const hora = fecha[3];
                    const minutos = fecha[4];

                    const horaDiv = document.createElement("div");
                    horaDiv.classList.add("hora");
                    horaDiv.innerHTML = `
                        <span>${hora}:${minutos === 0 ? "00" : minutos}</span>
                        <input type="radio" name="hora" />
                    `

                    horaDiv.addEventListener("click", () => {
                        horaDiv.querySelector("input").checked = true;
                        fechaSeleccionada = fecha;
                    })

                    horasDiv.appendChild(horaDiv);
                })
                diaDiv.appendChild(horasDiv);
            })

        document.getElementById("dias").appendChild(diaDiv);
    })
}

const navegarCalendario = (offset) => {
    offsetDiasFechaBase = Math.max(0, offsetDiasFechaBase + offset);
    calendarioIzquierda.disabled = offsetDiasFechaBase === 0;
    actualizarCalendario();
}

const mostrarError = (mensaje, campos) => {
    document.querySelector(".mensajeError").classList.remove("esconder");
    document.querySelector(".mensajeError > .texto").textContent = mensaje;

    for(const campo of campos) {
        document.querySelector(`[id=${campo}]`).classList.add("error")
    }
}

const enviarFormulario = () => {
    const idMedico = +medicoEspecialidadSelect.value;
    if(fechaSeleccionada === null) {
        mostrarError("Debes de seleccionar una fecha y hora.", ["paso1"])
        return;
    }

    document.querySelectorAll(`.error`).forEach((input) => input.classList.remove("error"))
    document.querySelector(".mensajeError").classList.add("esconder");

    fetch("/api/paciente/citas", {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            "idMedico": idMedico,
            "razon": razonCita.value,
            "anyo": fechaSeleccionada[0],
            "mes": fechaSeleccionada[1],
            "dia": fechaSeleccionada[2],
            "hora": fechaSeleccionada[3],
            "minutos": fechaSeleccionada[4],
        })
    })
        .then(async (response) => {
            if(response.ok) {
                console.log("Cita creada con éxito!")
                window.location.href = "/paciente/citas"
            } else {
                let data = await response.json();

                mostrarError(data.error, data.campos)
            }
        })
        .catch(error => {
            console.log("Error en la creación de la cita: ", error)
        })
}

medicoEspecialidadSelect.addEventListener("change", actualizarCalendario);
calendarioIzquierda.addEventListener("click", () => navegarCalendario(-DIAS_A_MOSTRAR))
calendarioDerecha.addEventListener("click", () => navegarCalendario(DIAS_A_MOSTRAR))

formulario.addEventListener("submit", (e) => {
    e.preventDefault();
    enviarFormulario();
})
