package es.deusto.spq.doctorclick;

import es.deusto.spq.doctorclick.filters.*;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<FiltroSlash> filtroSlash() {
        FilterRegistrationBean<FiltroSlash> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new FiltroSlash());

        registrationBean.setOrder(0);

        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<FiltroJWT> filtroJwt() {
        FilterRegistrationBean<FiltroJWT> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new FiltroJWT());

        // Endpoints que requieren autenticacion obligatoriamente
        registrationBean.addUrlPatterns("/medico/*");
        registrationBean.addUrlPatterns("/paciente/*");

        registrationBean.setOrder(1);

        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<FiltroRequerirNoAutenticado> filtroRequerirNoAutenticado() {
        FilterRegistrationBean<FiltroRequerirNoAutenticado> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new FiltroRequerirNoAutenticado());

        // Endpoints que requieren explicitamente no estar autenticado
        registrationBean.addUrlPatterns("/"); // Indice redirigira al login o a la pagina por defecto segun el rol
        registrationBean.addUrlPatterns("/login");
        registrationBean.addUrlPatterns("/registro");

        registrationBean.setOrder(1);

        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<FiltroMedico> filtroMedico() {
        FilterRegistrationBean<FiltroMedico> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new FiltroMedico());

        registrationBean.addUrlPatterns("/medico/*");

        registrationBean.setOrder(2);

        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<FiltroPaciente> filtroPaciente() {
        FilterRegistrationBean<FiltroPaciente> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new FiltroPaciente());

        registrationBean.addUrlPatterns("/paciente/*");

        registrationBean.setOrder(3);

        return registrationBean;
    }
}

