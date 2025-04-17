package es.deusto.spq.doctorclick.controller.api;

import es.deusto.spq.doctorclick.Utility;
import es.deusto.spq.doctorclick.model.Cita;
import es.deusto.spq.doctorclick.service.CitaService;
import es.deusto.spq.doctorclick.service.MedicoService;
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
    MedicoService medicoService;
    @Autowired
    private CitaService citaService;

    @GetMapping("/citas/{id}")
    public String citasId(@PathVariable("id") Long id, HttpServletRequest request, Model model) {
        try {
            String dni = Utility.obtenerDni(request);
            Optional<Cita> cita = medicoService.getCita(id);
            if (cita.isEmpty()) {
                model.addAttribute("mensaje", "No se ha encontrado la cita");
                return "verCitasMedico";
            } else if (!cita.get().getMedico().getDni().equals(dni)) {
                model.addAttribute("mensaje", "No tiene permiso para esta cita");
                return "citaDetalladaMedico";
            } else {
                model.addAttribute("cita", cita.get());
                return "verCitasMedico";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("mensaje", "error");
            return "citaDetalladaMedico";
        }
    }

    @DeleteMapping("/citas/{id}")
    public ResponseEntity<?> cancelarCita(@PathVariable("id") Long id, HttpServletRequest request, Model model) {
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