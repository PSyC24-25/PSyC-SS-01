package es.deusto.spq.doctorclick.controller.api;

import es.deusto.spq.doctorclick.model.Especialidad;
import es.deusto.spq.doctorclick.model.Medico;
import es.deusto.spq.doctorclick.model.Paciente;
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
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.Array;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {
    @Autowired
    private PacienteRepository pacienteRepository;
    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private RegistroService registroService;

    @GetMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Cookie cookie = new Cookie("JWT", null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        response.sendRedirect(request.getContextPath() + "/");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUsuario(@RequestBody Map<String, String> requestData, HttpServletResponse response) {
        Map<String, Object> responseMap = new HashMap<>();

        String dni = requestData.get("dni");
        String contrasena = requestData.get("contrasena");
        String tipoUsuario = requestData.get("tipoUsuario");

        if(!tipoUsuario.equals("paciente") && !tipoUsuario.equals("medico")) {
            System.out.println("Tipo de usuario invalido.");
            responseMap.put("error", "Tipo de usuario invalido.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMap);
        }


        Usuario usuario = null;
        if(tipoUsuario.equals("paciente")) {
            Optional<Paciente> paciente = pacienteRepository.findByDni(dni);
            if(paciente.isPresent()) { usuario = paciente.get(); }
        } else {
            Optional<Medico> medico = medicoRepository.findByDni(dni);
            if(medico.isPresent()) { usuario = medico.get(); }
        }

        if(usuario == null) {
            responseMap.put("error", "No existe ningún " + tipoUsuario + " con ese DNI.");
            responseMap.put("campos", List.of("dni"));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMap);
        }

        // TODO: Esto obviamente no se hara asi pues ni si quiera se esta encriptando en BD, es temporal.
        if(!usuario.getContrasenia().equals(contrasena)) {
            responseMap.put("error", "Contraseña incorrecta.");
            responseMap.put("campos", List.of("contrasena"));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMap);
        }

        try {
            response.addCookie(crearCookieSession(AuthService.CrearTokenJWT(dni, tipoUsuario)));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        responseMap.put("message", "Usuario login con éxito");
        return ResponseEntity.ok(responseMap);
    }

    @PostMapping("/registro")
    public ResponseEntity<Map<String, Object>> registrarUsuario(@RequestBody Map<String, String> requestData, HttpServletResponse response) {
        Map<String, Object> responseMap = new HashMap<>();

        String dni = requestData.get("dni");
        if(!isDniValido(dni)) {
            responseMap.put("error", "El DNI no es valido.");
            responseMap.put("campos", List.of("dni"));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap);
        }

        String nombre = requestData.get("nombre");
        if(nombre.isEmpty()) {
            responseMap.put("error", "El nombre no puede estar vacío.");
            responseMap.put("campos", List.of("nombre"));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMap);
        }

        String apellidos = requestData.get("apellidos");
        if(apellidos.isEmpty() || apellidos.trim().split(" ").length < 2) {
            responseMap.put("error", "Introduce al menos dos apellidos.");
            responseMap.put("campos", List.of("apellidos"));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMap);
        }

        String contrasena = requestData.get("contrasena");
        if(contrasena.length() < 4) {
            responseMap.put("error", "Las contraseña debe de ser al menos de 4 caracteres de largo.");
            responseMap.put("campos", List.of("contrasena"));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMap);
        }

        String contrasena2 = requestData.get("contrasena2");
        if(!contrasena.equals(contrasena2)) {
            responseMap.put("error", "Las contraseñas no coinciden.");
            responseMap.put("campos", List.of("contrasena", "contrasena2"));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMap);
        }

        String tipoUsuario = requestData.get("tipo");
        if(!tipoUsuario.equals("paciente") && !tipoUsuario.equals("medico")) {
            responseMap.put("error", "Tipo de usuario invalido.");
            responseMap.put("campos", List.of("tipoUsuario"));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMap);
        }

        String especialidad = requestData.get("especialidad");
        if(tipoUsuario.equals("medico")) {
            try {
                Especialidad.valueOf(especialidad);
            } catch(IllegalArgumentException e) {
                responseMap.put("error", "La especialidad médica no es válida.");
                responseMap.put("campos", List.of("especialidadParent"));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap);
            }
        }

        boolean registroExitoso = registroService.registrarUsuario(nombre, apellidos, contrasena, dni, tipoUsuario, especialidad);
        if(!registroExitoso) {
            responseMap.put("error", "Error al registrar el usuario en la base de datos. Probablemente el DNI ya esta en uso.");
            responseMap.put("campos", List.of("dni"));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap);
        }

        try {
            response.addCookie(crearCookieSession(AuthService.CrearTokenJWT(dni, tipoUsuario)));
        } catch (Exception e) {
            responseMap.put("error", "Error al crear la sesion. Intentalo mas tarde.");
            return ResponseEntity.ok(responseMap);
        }

        responseMap.put("message", "Usuario registrado con éxito");
        return ResponseEntity.ok(responseMap);
    }

    private static byte[] letrasDni = "TRWAGMYFPDXBNJZSQVHLCKE".getBytes();
    private boolean isDniValido(String dni) {
        if(dni == null) return false;
        if(dni.length() != 9) return false;

        byte letraIntroducida = dni.getBytes()[8];

        int numero = Integer.valueOf(dni.substring(0, 8));
        byte letraEsperada = letrasDni[numero % letrasDni.length];

        return letraIntroducida == letraEsperada;
    }

    private Cookie crearCookieSession(String tokenJwt) {
        Cookie cookie = new Cookie("JWT", tokenJwt);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60 * 60); // 1 hora
        cookie.setPath("/");

        return cookie;
    }

    public static class PacienteController {
    }
}
