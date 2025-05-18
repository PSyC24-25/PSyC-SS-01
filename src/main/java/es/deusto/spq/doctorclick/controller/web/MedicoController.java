package es.deusto.spq.doctorclick.controller.web;

import es.deusto.spq.doctorclick.Utility;
import es.deusto.spq.doctorclick.model.Cita;
import es.deusto.spq.doctorclick.model.Medico;
import es.deusto.spq.doctorclick.service.CitaService;
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

import static es.deusto.spq.doctorclick.service.CitaService.CITAS_POR_HORA;

@Controller
@RequestMapping("/medico")
public class MedicoController {

    private static final String VISTA_VER_CITAS_MEDICO = "medico/calendario";
    private static final String VISTA_PERFIL_MEDICO = "medico/miPerfil";

    @Autowired
    private CitaService citaService;
    @Autowired
    private MedicoService medicoService;

    @GetMapping("")
    public String indice(Model model){
        model.addAttribute("tipoCuenta", "medico");
        model.addAttribute("seccion", "indice");

        return "medico/medicoIndice";
    }

    @GetMapping("/citas")
    public String citas(HttpServletRequest request, Model model){
        model.addAttribute("tipoCuenta", "medico");
        model.addAttribute("seccion", "citas");

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
        model.addAttribute("tipoCuenta", "medico");
        model.addAttribute("seccion", "citas");

        return "medico/citaDetallada";
    }

    @GetMapping("/citasPasadas")
    public String citasPasadas(Model model){
        model.addAttribute("tipoCuenta", "medico");
        model.addAttribute("seccion", "citasPasadas");

        return "medico/verCitasPasadas";
    }

    @GetMapping("/miperfil")
    public String miperfil(HttpServletRequest request, Model model){
        model.addAttribute("tipoCuenta", "medico");
        model.addAttribute("seccion", "perfil");

        try {
            String dni = Utility.obtenerDni(request);
            Optional<Medico> medico = medicoService.getMedico(dni);
          
            if(medico.isEmpty()){
                model.addAttribute("medico", "No se han cargado los datos");
                return VISTA_PERFIL_MEDICO;

            }else if(!medico.get().getDni().equals(dni)){
                model.addAttribute("medico", "No tiene permiso para acceder a los datos de este perfil ");
                return VISTA_PERFIL_MEDICO;

            }else{
                model.addAttribute("medico", medico.get());
                return VISTA_PERFIL_MEDICO;
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("medico", "error");
            return VISTA_PERFIL_MEDICO;
        }
    }
}
