document.addEventListener("DOMContentLoaded", function(){

    let cancelar = document.getElementById("cancelar")
    let id = document.getElementById("idCita").innerText

    function cancelarCita(event){
        event.preventDefault()
        let data = {
            'idCita' : document.getElementById("idCita").innerText
        }

        let csrfToken = document.querySelector('meta[name="csrf-token"]').getAttribute('content');

        fetch('/api/medico/citas/' + id , {
            method: 'DELETE',
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
                    console.log("Error al loguear usuario: ", data)
                }
            })
            .catch(error => {
                console.log("Error en el registro:", error)
            })
    }
    cancelar.addEventListener('click',cancelarCita)
});