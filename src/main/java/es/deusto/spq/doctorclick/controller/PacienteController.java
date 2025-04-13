package es.deusto.spq.doctorclick.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import es.deusto.spq.doctorclick.Utility;
import es.deusto.spq.doctorclick.model.Cita;
import es.deusto.spq.doctorclick.model.Medico;
import es.deusto.spq.doctorclick.service.CitaService;
import es.deusto.spq.doctorclick.service.MedicoService;
import es.deusto.spq.doctorclick.service.PacienteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.databind.type.LogicalType.DateTime;


@Controller
@RequestMapping("paciente")
public class PacienteController {

    @Autowired
    PacienteService pacienteService;
    @Autowired
    MedicoService medicoService;
    @Autowired
    CitaService citaService;

    public static class Par {
        public String nombreVisual;
        public Long idMedico;
    }

    /* ### RUTAS PARA VISTAS ### */
    @GetMapping("")
    public String indice() {
        return "pacienteIndice";
    }

    @GetMapping("/citas")
    public String citas() {
        return "verCitas";
    }

    @GetMapping("/citas/pedir")
    public String citasPedir(Model model) {
        List<Medico> medicos = medicoService.getMedicos();

        List<Par> medicosEspecialidades = new ArrayList<>();
        for(Medico medico : medicos) {
            Par par = new Par();
            par.nombreVisual = medico.getEspecialidad() + " (" + medico.getNombre() + " " + medico.getApellidos() + ")";
            par.idMedico = medico.getId();

            medicosEspecialidades.add(par);
        }


        model.addAttribute("especialidades", medicosEspecialidades);
        return "pedirCita";
    }

    /* ### RUTAS API REST ### */
    @GetMapping("/api/citas/disponibles")
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

    @GetMapping("/api/citas")
    public ResponseEntity<?> apiCitas(HttpServletRequest request) {
        try {
            String dni = Utility.obtenerDni(request);
            List<Cita> citas = pacienteService.obtenerCitasPorDni(dni);
            return ResponseEntity.ok(citas);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("No se pudieron obtener las citas");
        }
    }

    @PostMapping("/api/citas")
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
}

