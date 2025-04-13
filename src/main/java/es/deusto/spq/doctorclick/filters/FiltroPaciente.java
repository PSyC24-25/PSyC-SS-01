package es.deusto.spq.doctorclick.filters;

import es.deusto.spq.doctorclick.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.ParseException;

public class FiltroPaciente extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        System.out.println("FiltroPaciente");
        String token = null;
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies){
            if(cookie.getName().equals("JWT")){
                token = cookie.getValue();
            }
        }

        assert token != null; // Sabemos que no es null

        try {
            if(AuthService.ObtenerClaimsJWT(token).getClaim("tipo").equals("paciente")) {
                chain.doFilter(request, response);
                return;
            }

            response.sendRedirect(request.getContextPath() + "/auth/login");
            // Esto se usara para la api
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            // response.getWriter().write("No autorizado.");
        } catch (ParseException e) {
            System.out.println("Error al obtener claims del JWT.");
        }

    }
}
