package es.deusto.spq.doctorclick.controller;

import es.deusto.spq.doctorclick.service.RegistroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/")
public class AuthController {

    @Autowired
    private RegistroService registroService;

    @GetMapping("login")
    public String login(){
        return "login";
    }

    @GetMapping("registro")
    public String registro(){
        return "registro";
    }

    @PostMapping("registro")
    public ResponseEntity<Map<String, String>> registrarUsuario(@RequestBody Map<String, String> requestData) {
        System.out.println(requestData);

        String nombre = requestData.get("nombre");
        String dni = requestData.get("dni");
        String tipo = requestData.get("tipo");
        String apellido = requestData.get("apellido");
        String contrasena = requestData.get("contrasena");
        String especialidad = requestData.get("especialidad");

        registroService.registrar(nombre,apellido,contrasena,dni,tipo,especialidad);

        Map<String, String> response = new HashMap<>();

        response.put("message", "Usuario registrado con éxito");
        return ResponseEntity.ok(response);

    }
}
