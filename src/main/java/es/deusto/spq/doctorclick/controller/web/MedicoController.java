package es.deusto.spq.doctorclick.controller.web;

import es.deusto.spq.doctorclick.Utility;
import es.deusto.spq.doctorclick.model.Cita;
import es.deusto.spq.doctorclick.service.CitaService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static es.deusto.spq.doctorclick.service.CitaService.CITAS_POR_HORA;

@Controller
@RequestMapping("/medico")
public class MedicoController {
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
            return "verCitasMedico";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("verCitasMedico", "No se han encontrado las citas");
            return "verCitasMedico";
        }
    }
}
