package es.deusto.spq.doctorclick.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("medico/")
public class MedicoController {
    @GetMapping("")
    public String indice(){
        return "medicoIndice";
    }
}
