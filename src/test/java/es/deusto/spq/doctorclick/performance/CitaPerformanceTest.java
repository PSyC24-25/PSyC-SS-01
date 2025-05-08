package es.deusto.spq.doctorclick.performance;

import com.github.noconnor.junitperf.JUnitPerfInterceptor;
import com.github.noconnor.junitperf.JUnitPerfTest;
import es.deusto.spq.doctorclick.service.CitaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith(JUnitPerfInterceptor.class)
class CitaPerformanceTest {

    @Autowired
    CitaService citaService;

    @Test
    @JUnitPerfTest(threads = 10, durationMs = 10000, warmUpMs = 2000)
    void getCitasDniYPaciente(){
        citaService.obtenerCitaPorIdYPaciente(0L,"73275435B");
    }

    @Test
    @JUnitPerfTest(threads = 10, durationMs = 10000, warmUpMs = 2000)
    void getCitasDni(){
        citaService.getCitas("73275435B");

    }

    @Test
    @JUnitPerfTest(threads = 10, durationMs = 10000, warmUpMs = 2000)
    void getCitaId(){
        citaService.getCita(0L);

    }
}
