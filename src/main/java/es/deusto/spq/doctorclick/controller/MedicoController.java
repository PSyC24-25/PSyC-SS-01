package es.deusto.spq.doctorclick.controller;

import es.deusto.spq.doctorclick.Utility;
import es.deusto.spq.doctorclick.model.Cita;
import es.deusto.spq.doctorclick.service.MedicoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("medico")
public class MedicoController {

    @Autowired
    MedicoService medicoService;

    /* ### RUTAS PARA VISTAS ### */
    @GetMapping("")
    public String indice(){
        return "medicoIndice";
    }
    @GetMapping("/citas")
    public String citas(HttpServletRequest request, Model model){
        try {
            String dni = Utility.obtenerDni(request);
            List<Cita> citas = medicoService.getCitas(dni);
            model.addAttribute("citas", citas);
            return "verCitasMedico";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("verCitasMedico", "No se han encontrado las citas");
            return "verCitasMedico";
        }
    }

    /* ### RUTAS API REST ### */
    @GetMapping("/api/citas/{id}")
    public String citasId(@PathVariable("id") Long id, HttpServletRequest request, Model model){
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
