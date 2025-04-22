package es.deusto.spq.doctorclick.controller.web;

import es.deusto.spq.doctorclick.Utility;
import es.deusto.spq.doctorclick.model.Medico;
import es.deusto.spq.doctorclick.model.Cita;
import es.deusto.spq.doctorclick.service.MedicoService;
import es.deusto.spq.doctorclick.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PathVariable;



import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



@Controller
@RequestMapping("paciente")
public class PacienteController {
    @Autowired
    MedicoService medicoService;

    @Autowired
    CitaService citaService;

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

        List<Utility.ParMedicoEspecialidad> medicosEspecialidades = new ArrayList<>();
        for(Medico medico : medicos) {
            Utility.ParMedicoEspecialidad par = new Utility.ParMedicoEspecialidad();
            par.nombreVisual = medico.getEspecialidad() + " (" + medico.getNombre() + " " + medico.getApellidos() + ")";
            par.idMedico = medico.getId();

            medicosEspecialidades.add(par);
        }

        model.addAttribute("especialidades", medicosEspecialidades);
        return "pedirCita";
    }

    @GetMapping("/citas/{id}")
    public String verCitaDetalle(@PathVariable Long id, Model model, HttpServletRequest request) throws Exception {
        String dni = Utility.obtenerDni(request);
        System.out.println("DNI del paciente: " + dni);
        Optional<Cita> optCita = citaService.obtenerCitaPorIdYPaciente(id, dni);
        if (optCita.isEmpty()) {
            return "redirect:/paciente/citas?error=notfound";
        }
        model.addAttribute("cita", optCita.get());
        return "verCitasDetalle";
    }
}

