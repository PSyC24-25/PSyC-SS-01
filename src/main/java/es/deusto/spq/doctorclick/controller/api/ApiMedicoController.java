package es.deusto.spq.doctorclick.controller.api;

import es.deusto.spq.doctorclick.Utility;
import es.deusto.spq.doctorclick.model.Cita;
import es.deusto.spq.doctorclick.service.MedicoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/medico")
public class ApiMedicoController {

    @Autowired
    MedicoService medicoService;

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
}