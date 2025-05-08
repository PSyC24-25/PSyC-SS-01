package es.deusto.spq.doctorclick.performance;

import com.github.noconnor.junitperf.JUnitPerfInterceptor;
import com.github.noconnor.junitperf.JUnitPerfTest;
import es.deusto.spq.doctorclick.service.PacienteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith(JUnitPerfInterceptor.class)
class PacientePerformanceTest {

    @Autowired
    PacienteService pacienteService;

    @Test
    @JUnitPerfTest(threads = 10, durationMs = 10000, warmUpMs = 2000)
    void testGetPacienteByDniPerformance() {
        pacienteService.getPaciente("73275435B");
    }

    @Test
    @JUnitPerfTest(threads = 10, durationMs = 10000, warmUpMs = 2000)
    void testGetPacienteByIdPerformance() {
        pacienteService.getPaciente(1L);
    }
}
