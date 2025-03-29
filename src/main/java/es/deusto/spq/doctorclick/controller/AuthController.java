package es.deusto.spq.doctorclick.controller;

import es.deusto.spq.doctorclick.model.Usuario;
import es.deusto.spq.doctorclick.repository.MedicoRepository;
import es.deusto.spq.doctorclick.repository.PacienteRepository;
import es.deusto.spq.doctorclick.service.AuthService;
import es.deusto.spq.doctorclick.service.RegistroService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/")
public class AuthController {
    @Autowired
    private PacienteRepository pacienteRepository;
    @Autowired
    private MedicoRepository medicoRespository;

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

    @GetMapping("logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Cookie cookie = new Cookie("JWT", null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        response.sendRedirect(request.getContextPath() + "/");
    }

    @PostMapping("login")
    public ResponseEntity<Map<String, String>> loginUsuario(@RequestBody Map<String, String> requestData, HttpServletResponse response) {
        Map<String, String> responseMap = new HashMap<>();

        String dni = requestData.get("username");
        String contrasenia = requestData.get("password");
        String tipoUsuario = requestData.get("tipoUsuario");

        if(!tipoUsuario.equals("paciente") && !tipoUsuario.equals("medico")) {
            System.out.println("Tipo de usuario invalido.");
            responseMap.put("error", "Tipo de usuario invalido.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMap);
        }


        Usuario usuario;
        if(tipoUsuario.equals("paciente")) {
            usuario = pacienteRepository.findByDni(dni);
        } else {
            usuario = medicoRespository.findByDni(dni);
        }

        if(usuario == null) {
            System.out.println("Usuario no encontrado.");
            responseMap.put("error", "Usuario no encontrado.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMap);
        }

        // TODO: Esto obviamente no se hara asi pues ni si quiera se esta encriptando en BD, es temporal.
        if(!usuario.getContrasenia().equals(contrasenia)) {
            System.out.println("Contrasena incorrecta.");
            responseMap.put("error", "Contrasena incorrecta.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMap);
        }

        try {
            String tokenJwt = AuthService.CrearTokenJWT(dni, tipoUsuario);
            Cookie cookie = new Cookie("JWT", tokenJwt);
            cookie.setHttpOnly(true);
            cookie.setMaxAge(60 * 60); // 1 hora
            cookie.setPath("/");
            response.addCookie(cookie);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        responseMap.put("message", "Usuario login con éxito");
        return ResponseEntity.ok(responseMap);
    }

    @PostMapping("registro")
    public ResponseEntity<Map<String, String>> registrarUsuario(@RequestBody Map<String, String> requestData) {
        String nombre = requestData.get("nombre");
        String dni = requestData.get("dni");
        String tipo = requestData.get("tipo");
        String apellidos = requestData.get("apellidos");
        String contrasena = requestData.get("contrasena");
        String especialidad = requestData.get("especialidad");

        registroService.registrarUsuario(nombre, apellidos, contrasena, dni, tipo, especialidad);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Usuario registrado con éxito");
        return ResponseEntity.ok(response);
    }
}
