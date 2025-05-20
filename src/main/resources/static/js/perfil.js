const tipoCuenta = window.location.pathname.indexOf("paciente") > -1 ? "paciente" : "medico";

document.getElementById("baja").addEventListener("click", () => {
    if (confirm("¿Estás seguro de que quieres darte de baja?")) {
        fetch(`/api/${tipoCuenta}/perfil`, {
            method: "DELETE",
            credentials: "include"
        })
            .then(resp => {
                if (resp.ok) {
                    alert("Su cuenta se ha eliminado correctamente. Se ha cerrado la sesión.");
                    window.location.href = "/auth/logout";
                } else {
                    return resp.text().then(msg => { throw new Error(msg); });
                }
            })
            .catch(err => {
                alert("Error al dar de baja su perfil: " + err.message);
            });
    }
});
