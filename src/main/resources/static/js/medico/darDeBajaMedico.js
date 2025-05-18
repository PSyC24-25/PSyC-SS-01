document.getElementById("baja").addEventListener("click", () => {
    if (confirm("¿Estás seguro de que quieres darte de baja?")) {
        fetch(`/api/medico/miperfil`, {
            method: "DELETE",
            credentials: "include"
        })
            .then(resp => {
                if (resp.ok) {
                    alert("cuenta eliminada correctamente");
                    window.location.href = "/auth/logout";
                } else {
                    return resp.text().then(msg => { throw new Error(msg); });
                }
            })
            .catch(err => {
                alert("Error al dar de baja el perfil: " + err.message);
            });
    }
});