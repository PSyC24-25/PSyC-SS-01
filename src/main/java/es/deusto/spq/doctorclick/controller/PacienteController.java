package es.deusto.spq.doctorclick.controller;

import es.deusto.spq.doctorclick.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("paciente/")
public class PacienteController {

    @Autowired
    PacienteService pacienteService;

    @GetMapping("")
    public String indice(){
        return "pacienteIndice";
    }

    @GetMapping("/pedirCita")
    public String pedirCita() {
        return "pedirCita";
    }

    @PostMapping("/pedirCita")
    public ResponseEntity<Map<String,String>> pedirCita(@RequestBody Map<String,String> requestData) {

        Map<String, String> responseMap = new HashMap<>();
        if (pacienteService.crearCita(requestData)){
            responseMap.put("mensaje", "Cita creada correctamente");
            return ResponseEntity.ok(responseMap);
        }
        responseMap.put("mensaje", "No se pudo crear cita");
        return ResponseEntity.badRequest().body(responseMap);
    }
}
