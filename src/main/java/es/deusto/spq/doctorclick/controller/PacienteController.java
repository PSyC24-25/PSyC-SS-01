package es.deusto.spq.doctorclick.controller;

import com.nimbusds.jwt.JWTClaimsSet;
import es.deusto.spq.doctorclick.model.Cita;
import es.deusto.spq.doctorclick.service.AuthService;
import es.deusto.spq.doctorclick.service.PacienteService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("paciente/")
public class PacienteController {

    @Autowired
    PacienteService pacienteService;

    @GetMapping("")
    public String indice() {
        return "pacienteIndice";
    }

    @GetMapping("/pedirCita")
    public String pedirCita() {
        return "pedirCita";
    }

    @PostMapping("/pedirCita")
    public ResponseEntity<Map<String, String>> pedirCita(@RequestBody Map<String, String> requestData, HttpServletRequest request) {
        String token = null;
        String dni = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("JWT")) {
                    token = cookie.getValue();
                }
            }
        }
        try {
            JWTClaimsSet claims = AuthService.ObtenerClaimsJWT(token);
            dni = (String) claims.getClaim("dni");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, String> responseMap = new HashMap<>();
        if (pacienteService.crearCita(requestData, dni)) {
            responseMap.put("mensaje", "Cita creada correctamente");
            return ResponseEntity.ok(responseMap);
        }
        responseMap.put("mensaje", "No se pudo crear cita");
        return ResponseEntity.badRequest().body(responseMap);
    }

    @GetMapping("/verCitasJson")
    public ResponseEntity<?> verCitasJson(HttpServletRequest request) {
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JWT".equals(cookie.getName())) {
                    token = cookie.getValue();
                }
            }
        }
        try {
            JWTClaimsSet claims = AuthService.ObtenerClaimsJWT(token);
            String dni = (String) claims.getClaim("dni");
            List<Cita> citas = pacienteService.obtenerCitasPorDni(dni);
            return ResponseEntity.ok(citas);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("No se pudieron obtener las citas");
        }
    }

    @GetMapping("/verCitas")
    public String verCitas() {
        return "verCitas";
    }
}

