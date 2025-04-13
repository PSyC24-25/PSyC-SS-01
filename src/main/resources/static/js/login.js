document.addEventListener("DOMContentLoaded", function(){

    let enviar = document.getElementById("enviar")

    function logear(event){
        event.preventDefault()
        let data = {
            'username' : document.getElementById("username").value,
            'password' : document.getElementById("password").value,
            'tipoUsuario': document.getElementById("usuario").value
        }

        let csrfToken = document.querySelector('meta[name="csrf-token"]').getAttribute('content');

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
                    console.log("Error al loguear usuario: ", data)
                }
        })
            .catch(error => {
                console.log("Error en el registro:", error)
            })
    }
    enviar.addEventListener('click',logear)
});