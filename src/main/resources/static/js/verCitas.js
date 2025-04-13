document.addEventListener("DOMContentLoaded", () => {
    const citasTableBody = document.getElementById("citasTableBody");

    fetch("/api/paciente/citas")
        .then(response => response.json())
        .then(citas => {
            citasTableBody.innerHTML = "";
            if (citas.length === 0) {
                citasTableBody.innerHTML = "<tr><td colspan='3' class='text-center p-4'>No tienes citas</td></tr>";
                return;
            }
            citas.forEach(cita => {
                const fechaHora = new Date(cita.fecha);
                const fecha= fechaHora.toISOString().split("T")[0];
                const hora = fechaHora.toTimeString().split(" ")[0].substring(0,5);

                const row = `
                    <tr>
                        <td class="border p-2">${fecha}</td>
                        <td class="border p-2">${hora}</td>
                        <td class="border p-2">${cita.medico.nombre} ${cita.medico.apellidos}</td>
                        <td class="border p-2">${cita.especialidad}</td>
                        <td class="border p-2">${cita.resumen}</td>
                    </tr>`;
                citasTableBody.innerHTML += row;
            });
        })
        .catch(error => {
            console.error("Error obteniendo citas:", error);
            citasTableBody.innerHTML = "<tr><td colspan='3' class='text-center p-4 text-red-500'>Error cargando citas</td></tr>";
        });
});
