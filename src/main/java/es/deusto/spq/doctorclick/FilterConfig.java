package es.deusto.spq.doctorclick;

import es.deusto.spq.doctorclick.filters.FiltroJWT;
import es.deusto.spq.doctorclick.filters.FiltroMedico;
import es.deusto.spq.doctorclick.filters.FiltroPaciente;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<FiltroJWT> filtroJwt() {
        FilterRegistrationBean<FiltroJWT> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new FiltroJWT());

        // Colocar aqui endpoints que requieren autenticacion
        registrationBean.addUrlPatterns("/medico/*");
        registrationBean.addUrlPatterns("/paciente/*");

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

