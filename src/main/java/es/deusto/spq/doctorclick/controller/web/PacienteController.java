package es.deusto.spq.doctorclick.controller.web;

import es.deusto.spq.doctorclick.Utility;
import es.deusto.spq.doctorclick.model.Medico;
import es.deusto.spq.doctorclick.service.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("paciente")
public class PacienteController {
    @Autowired
    MedicoService medicoService;

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
}

