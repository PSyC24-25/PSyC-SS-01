package es.deusto.spq.doctorclick.controller.api;

import es.deusto.spq.doctorclick.Utility;
import es.deusto.spq.doctorclick.model.Cita;
import es.deusto.spq.doctorclick.model.Medico;
import es.deusto.spq.doctorclick.service.CitaService;
import es.deusto.spq.doctorclick.service.MedicoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/medico")
public class ApiMedicoController {

    @Autowired
    private CitaService citaService;
    @Autowired
    private MedicoService medicoService;

    @GetMapping("/citas/{id}")
    public ResponseEntity<?> obtenerCita(@PathVariable("id") Long id, HttpServletRequest request){
        try {
            String dni = Utility.obtenerDni(request);
            Optional<Cita> cita = citaService.getCita(id);
            if (cita.isEmpty()) {
                return ResponseEntity.status(404).body("Cita no encontrada");
            } else if (!cita.get().getMedico().getDni().equals(dni)) {
                return ResponseEntity.status(403).body("No puedes acceder a esta cita");
            } else {
                return ResponseEntity.status(200).body(cita.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al obtener la cita");
        }
    }

    @DeleteMapping("/citas/{id}")
    public ResponseEntity<?> cancelarCita(@PathVariable("id") Long id, HttpServletRequest request) {
        try {
            String dni = Utility.obtenerDni(request);
            CitaService.CitaEliminadaResultado resultado = citaService.cancelarCitaDeMedico(dni,id);

            return switch (resultado) {
                case CITA_ELIMINADA -> ResponseEntity.status(200).body("Cita eliminada");
                case ERROR_MEDICO -> ResponseEntity.status(404).body("Medico no eocntrado");
                case ERROR_CITA_ID -> ResponseEntity.status(404).body("Cita no encotrada");
                case ERROR_ELIMINACION -> ResponseEntity.status(404).body("Error en la eliminar del cita");
                default -> ResponseEntity.status(404).body("Error desconocido.");
            };
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error en el servidor");
        }
    }

   @DeleteMapping("/perfil")
    public ResponseEntity<String> bajaPerfil( HttpServletRequest request) {
        try {
            String dniUsuario = Utility.obtenerDni(request);
            Optional<Medico> medico = medicoService.getMedico(dniUsuario);

            if (medico.isPresent()) {
                medicoService.bajaMedico(medico.get());
                return ResponseEntity.ok("Baja confirmada");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Médico no encontrado");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar la baja: " + e.getMessage());
        }
    }

    @GetMapping("/citasPasadas")
    public ResponseEntity<?> citasPasadas(HttpServletRequest request) {
        try {
            String dni = Utility.obtenerDni(request);
            List<Cita> citas = citaService.obtenerCitaMedicoPasado(dni);
            return ResponseEntity.status(HttpStatus.OK).body(citas);
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}