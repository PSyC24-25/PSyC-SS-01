package es.deusto.spq.doctorclick.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class FiltroSlash extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        if(uri.endsWith("/") && uri.length() > 1) {
            response.sendRedirect(uri.substring(0, uri.length()-1));
            return;
        }

        chain.doFilter(request, response);
    }
}