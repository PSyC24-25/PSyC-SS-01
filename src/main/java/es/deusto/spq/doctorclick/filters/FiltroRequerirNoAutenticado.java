package es.deusto.spq.doctorclick.filters;

import com.nimbusds.jwt.JWTClaimsSet;
import es.deusto.spq.doctorclick.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

public class FiltroRequerirNoAutenticado extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        System.out.println("FiltroNoAutenticado");
        String token = null;
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals("JWT")){
                    token = cookie.getValue();
                }
            }
        }

        try {
            if(!AuthService.EsJWTValido(token)) {
                chain.doFilter(request, response);
                return;
            }

            // Esto se usara para la api
            // response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            // response.getWriter().write("Ya tienes una sesion iniciada.");

            // Si es valido, redirigir a la pantalla principal segun el rol
            JWTClaimsSet claims = AuthService.ObtenerClaimsJWT(token);

            String tipo = (String)claims.getClaim("tipo");
            if(tipo.equals("medico")) {
                response.sendRedirect(request.getContextPath() + "/medico/");
            } else if(tipo.equals("paciente")) {
                response.sendRedirect(request.getContextPath() + "/paciente/");
            }

            response.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
        } catch (Exception e) {
            System.out.println("Error al obtener claims del JWT.");
        }
    }
}
