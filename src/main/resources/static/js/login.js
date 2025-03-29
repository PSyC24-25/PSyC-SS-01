document.addEventListener("DOMContentLoaded", function(){

    let enviar = document.getElementById("enviar")

    function logear(event){
        event.preventDefault()
        let data = {
            'username' : document.getElementById("username").value,
            'password' : document.getElementById("password").value,
            'tipoUsuario': document.getElementById("usuario").value
        }
        console.log(data)

        let csrfToken = document.querySelector('meta[name="csrf-token"]').getAttribute('content');

        fetch('/login', {
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
                console.log("Error en el registro:", error)
            })
    }
    enviar.addEventListener('click',logear)
});