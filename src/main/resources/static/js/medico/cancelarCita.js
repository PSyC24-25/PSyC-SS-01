document.getElementById("cancelarBtn").addEventListener("click", () => {
    if (confirm("¿Estás seguro de que quieres cancelar esta cita?")) {
        const citaId = window.location.pathname.split("/").pop();

        fetch(`/api/medico/citas/${citaId}`, {
            method: "DELETE",
            credentials: "include"
        })
            .then(resp => {
                if (resp.ok) {
                    alert("Cita cancelada correctamente.");
                    window.location.href = "/medico/citas";
                } else {
                    return resp.text().then(msg => { throw new Error(msg); });
                }
            })
            .catch(err => {
                alert("Error al cancelar la cita: " + err.message);
            });
    }
});