package es.deusto.spq.doctorclick.controller.api;

import es.deusto.spq.doctorclick.Utility;
import es.deusto.spq.doctorclick.model.Cita;
import es.deusto.spq.doctorclick.model.Medico;
import es.deusto.spq.doctorclick.repository.CitaRepository;
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

    private final CitaRepository citaRepository;

    @Autowired
    private CitaService citaService;
    @Autowired
    private MedicoService medicoService;

    ApiMedicoController(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

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
   @DeleteMapping("/miperfil")
    public ResponseEntity<String> bajaPerfil( HttpServletRequest request) {
        try {
            String dniUsuario = Utility.obtenerDni(request);
            Optional<Medico> medico = medicoService.getMedico(dniUsuario);


            if (medico.isPresent()) {
               boolean resultado =  citaService.eliminarCitasMedicos(dniUsuario);
                medicoService.bajaMedico(medico.get());
               if(resultado == true){
                return ResponseEntity.ok("Baja confirmada");
               }
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