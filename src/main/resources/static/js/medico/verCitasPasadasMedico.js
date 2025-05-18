fetch("http://localhost:8080/api/medico/citasPasadas", {
    method: 'GET',
    headers: {
        'Content-Type': 'application/json'
    },
})
    .then(async (response) => {
        let data = await response.json();
        if (response.ok) {
            const tabla = document.getElementById("tabla-citas");

            data.forEach(cita => {
                const fila = document.createElement("tr");

                fila.classList.add("cursor-pointer", "hover:bg-gray-100");

                fila.addEventListener("click", () => {
                    window.location.href = `/medico/citas/${cita.id}`;
                });
                fila.innerHTML = `
                    <td>${cita.fecha}</td>
                    <td>${cita.medico.nombre}</td>
                    <td>${cita.especialidad}</td>
                    <td>${cita.resumen || '-'}</td>
                    <td>${cita.id}</td>
                `;

                tabla.appendChild(fila);
            });
        } else {
            console.log("Error al obtener citas pasadas:", data);
        }
    })
    .catch(error => {
        console.log("Error en la petición:", error);
    });
