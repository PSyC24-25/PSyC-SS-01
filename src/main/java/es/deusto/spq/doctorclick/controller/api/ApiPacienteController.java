package es.deusto.spq.doctorclick.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import es.deusto.spq.doctorclick.Utility;
import es.deusto.spq.doctorclick.model.Cita;
import es.deusto.spq.doctorclick.service.CitaService;
import es.deusto.spq.doctorclick.service.PacienteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/paciente")
public class ApiPacienteController {
    @Autowired
    PacienteService pacienteService;
    @Autowired
    CitaService citaService;

    // ===== RUTAS DE CITAS =====

    @GetMapping("/citas")
    public ResponseEntity<?> apiCitas(HttpServletRequest request) {
        try {
            String dni = Utility.obtenerDni(request);
            List<Cita> citas = citaService.obtenerCitasPorDni(dni);
            return ResponseEntity.ok(citas);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("No se pudieron obtener las citas");
        }
    }

    @PostMapping("/citas")
    public ResponseEntity apiCitasCrear(@RequestBody Map<String, String> formData, HttpServletRequest request) {
        Map<String, String> responseMap = new HashMap<>();

        try {
            String dni = Utility.obtenerDni(request);

            Long idMedico = Long.parseLong(formData.get("idMedico"));
            String razon = formData.get("razon");

            int anyo = Integer.parseInt(formData.get("anyo"));
            int mes = Integer.parseInt(formData.get("mes"));
            int dia = Integer.parseInt(formData.get("dia"));
            int hora = Integer.parseInt(formData.get("hora"));
            int minutos = Integer.parseInt(formData.get("minutos"));

            LocalDateTime fecha = LocalDateTime.of(anyo, mes, dia, hora, minutos);

            CitaService.CitaCreacionResultado citaResultado = citaService.crearCita(idMedico, dni, fecha, razon);
            if(citaResultado.equals(CitaService.CitaCreacionResultado.CITA_CREADA)) {
                return ResponseEntity.status(HttpStatus.OK).body(responseMap);
            } else {
                switch(citaResultado) {
                    case ERROR_HORA_NO_VALIDA -> responseMap.put("error", "No se pudo crear la cita. La hora no es valida.");
                    case ERROR_MEDICO_PACIENTE -> responseMap.put("error", "No se pudo crear la cita. El medico o paciente no existen.");
                }

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap);
            }
        } catch (Exception e) {
            responseMap.put("error", "Error inesperado al crear la cita: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
    }

    @GetMapping("/citas/disponibles")
    public void apiCitasDisponibles(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int anyo = Integer.parseInt(request.getParameter("anyo"));
        int mes = Integer.parseInt(request.getParameter("mes"));
        int dia = Integer.parseInt(request.getParameter("dia"));

        LocalDate fecha = LocalDate.of(anyo, mes, dia);
        Long idMedico = Long.parseLong(request.getParameter("medico"));

        List<LocalDateTime> horasDisponibles = citaService.obtenerHorasDisponibles(idMedico, fecha);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.writeValue(response.getWriter(), horasDisponibles);
    }
}
