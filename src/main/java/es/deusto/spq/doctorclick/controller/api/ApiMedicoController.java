package es.deusto.spq.doctorclick.controller.api;

import es.deusto.spq.doctorclick.Utility;
import es.deusto.spq.doctorclick.model.Cita;
import es.deusto.spq.doctorclick.service.CitaService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/medico")
public class ApiMedicoController {

    @Autowired
    private CitaService citaService;

    @DeleteMapping("/citas/{id}")
    public ResponseEntity<?> cancelarCita(@PathVariable("id") Long id, HttpServletRequest request) {
        try {
            String dni = Utility.obtenerDni(request);
            CitaService.CitaEliminadaResultado resultado = citaService.cancelarCitaMedico(dni,id);

            return switch (resultado) {
                case CITA_ELIMINADA -> ResponseEntity.status(200).body("Cita eliminada");
                case ERROR_MEDICO -> ResponseEntity.status(404).body("Medico no eocntrado");
                case ERROR_CITA_ID -> ResponseEntity.status(404).body("Cita no encotrada");
                case ERROR_ELIMINACION -> ResponseEntity.status(404).body("Error en la eliminar del cita");
            };
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error en el servidor");
        }

    }
}