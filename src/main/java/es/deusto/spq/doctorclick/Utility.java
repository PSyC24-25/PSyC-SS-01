package es.deusto.spq.doctorclick;

import com.nimbusds.jwt.JWTClaimsSet;
import es.deusto.spq.doctorclick.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;

public class Utility {
    public static class ParMedicoEspecialidad {
        public String nombreVisual;
        public Long idMedico;
    }


    public static String obtenerDni(HttpServletRequest request) throws Exception {
        String token = obtenerTokenDeCookies(request);
        if (token == null) {
            throw new Exception("Token no encontrado en las cookies");
        }
        JWTClaimsSet claims = AuthService.ObtenerClaimsJWT(token);
        String dni = (String) claims.getClaim("dni");
        if (dni == null) {
            throw new Exception("DNI no encontrado en el token");
        }
        return dni;
    }
    private static String obtenerTokenDeCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JWT".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
