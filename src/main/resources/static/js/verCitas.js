document.addEventListener("DOMContentLoaded", () => {
    const citasTableBody = document.getElementById("citasTableBody");

    fetch("/api/paciente/citas", {
        credentials: 'include' // <- Esto es clave
    })
        .then(resp => resp.json())
        .then(citas => {
            citasTableBody.innerHTML = "";
            if (!citas.length) {
                citasTableBody.innerHTML = `
                  <tr>
                    <td colspan="5" class="text-center p-4">No tienes citas</td>
                  </tr>`;
                return;
            }

            citas.forEach(cita => {
                const fechaHora = new Date(cita.fecha);
                const fecha = fechaHora.toISOString().split("T")[0];
                const hora  = fechaHora.toTimeString().slice(0,5);

                // 1) Crear <tr> en vez de template literal plano
                const row = document.createElement("tr");
                row.classList.add("cursor-pointer", "hover:bg-gray-100");
                // 2) Al hacer click, redirigir al detalle
                row.addEventListener("click", () => {
                    window.location.href = `/paciente/citas/${cita.id}`;
                });

                // 3) Rellenar las celdas
                row.innerHTML = `
                  <td class="border p-2">${fecha}</td>
                  <td class="border p-2">${hora}</td>
                  <td class="border p-2">${cita.medico.nombre} ${cita.medico.apellidos}</td>
                  <td class="border p-2">${cita.especialidad}</td>
                  <td class="border p-2">${cita.resumen}</td>
                `;
                citasTableBody.appendChild(row);
            });
        })
        .catch(err => {
            console.error("Error obteniendo citas:", err);
            citasTableBody.innerHTML = `
              <tr>
                <td colspan="5" class="text-center p-4 text-red-500">
                  Error cargando citas
                </td>
              </tr>`;
        });
});
