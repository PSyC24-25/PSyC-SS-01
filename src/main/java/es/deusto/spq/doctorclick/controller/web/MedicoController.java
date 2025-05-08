package es.deusto.spq.doctorclick.controller.web;

import es.deusto.spq.doctorclick.Utility;
import es.deusto.spq.doctorclick.model.Cita;
import es.deusto.spq.doctorclick.service.CitaService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

import static es.deusto.spq.doctorclick.service.CitaService.CITAS_POR_HORA;

@Controller
@RequestMapping("/medico")
public class MedicoController {

    private static final String VISTA_CITA_DETALLADA_MEDICO = "citaDetalladaMedico";
    private static final String VISTA_VER_CITAS_MEDICO = "verCitasMedico";


    @Autowired
    private CitaService citaService;

    @GetMapping("")
    public String indice(){
        return "medicoIndice";
    }

    @GetMapping("/citas")
    public String citas(HttpServletRequest request, Model model){
        try {
            String dni = Utility.obtenerDni(request);
            List<Cita> citas = citaService.getCitas(dni);
            model.addAttribute("citas", citas);
            model.addAttribute("citasDuracionMinutos", 60 / CITAS_POR_HORA);
            return VISTA_VER_CITAS_MEDICO;
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute(VISTA_VER_CITAS_MEDICO, "No se han encontrado las citas");
            return VISTA_VER_CITAS_MEDICO;
        }
    }

    @GetMapping("/citas/{id}")
    public String citasId(@PathVariable("id") Long id, HttpServletRequest request, Model model) {
        try {
            String dni = Utility.obtenerDni(request);
            Optional<Cita> cita = citaService.getCita(id);
            if (cita.isEmpty()) {
                model.addAttribute("mensaje", "No se ha encontrado la cita");
                return VISTA_VER_CITAS_MEDICO;
            } else if (!cita.get().getMedico().getDni().equals(dni)) {
                model.addAttribute("mensaje", "No tiene permiso para esta cita");
                return VISTA_CITA_DETALLADA_MEDICO;
            } else {
                model.addAttribute("cita", cita.get());
                return VISTA_CITA_DETALLADA_MEDICO;
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("mensaje", "error");
            return VISTA_CITA_DETALLADA_MEDICO;
        }
    }
}
