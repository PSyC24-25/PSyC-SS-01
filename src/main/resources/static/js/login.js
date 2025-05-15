document.addEventListener("DOMContentLoaded", function(){

    let enviar = document.getElementById("enviar")

    function logear(event){
        event.preventDefault()
        let data = {
            'dni' : document.getElementById("dni").value,
            'contrasena' : document.getElementById("contrasena").value,
            'tipoUsuario': document.getElementById("usuario").value
        }

        let csrfToken = document.querySelector('meta[name="csrf-token"]').getAttribute('content');

        document.querySelectorAll(`input.error`).forEach((input) => input.classList.remove("error"))
        document.querySelector(".mensajeError").classList.add("esconder");

        fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': csrfToken
                  },
            body: JSON.stringify(data)
        })
            .then(async (response) => {
                if(response.ok) {
                    window.location.href = (data.tipoUsuario === "paciente" ? "/paciente/" : "/medico/");
                } else {
                    let data = await response.json();

                    document.querySelector(".mensajeError").classList.remove("esconder");
                    document.querySelector(".mensajeError > .texto").textContent = data.error;
                    for(const campo of data.campos) {
                        console.log(campo)
                        document.querySelector(`input[id=${campo}]`).classList.add("error")
                    }
                }
        })
            .catch(error => {
                console.log("Error en el registro:", error)
            })
    }
    enviar.addEventListener('click',logear)
});