package es.deusto.spq.doctorclick.IntegrationTest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import com.nimbusds.jose.JOSEException;
import es.deusto.spq.doctorclick.model.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import es.deusto.spq.doctorclick.service.*;
import org.springframework.test.context.jdbc.Sql;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(statements = "DELETE FROM cita; DELETE FROM usuario;")
class CrearCitaIntegrationTest {

    @Autowired TestRestTemplate restTemplate;
    @Autowired CitaService citaService;
    @Autowired MedicoService medicoService;
    @Autowired PacienteService pacienteService;

    Long idMedico;

    @Transactional
    @BeforeEach
    void datos() {
        // Crear médico y paciente de prueba
        Medico m = new Medico(Especialidad.CARDIOLOGIA);
        m.setDni("MED123");
        m.setNombre("Cardio");
        medicoService.registrarMedico(m);
        idMedico = m.getId();

        Paciente p = new Paciente();
        p.setDni("PAC123");
        p.setContrasenia("1234");
        pacienteService.registrarPaciente(p);
    }

    @Test
    void pacienteCreaCitaCorrecta() {
        LocalDateTime fechaValida = citaService.obtenerHorasDisponibles(idMedico, LocalDate.now().plusDays(1))
                .get(0);


        Map<String,String> req = Map.of(
                "idMedico", idMedico.toString(),
                "razon", "Dolor en el pecho desde ayer",     // ≥5 palabras
                "anyo", String.valueOf(fechaValida.getYear()),
                "mes", String.valueOf(fechaValida.getMonthValue()),
                "dia", String.valueOf(fechaValida.getDayOfMonth()),
                "hora", String.valueOf(fechaValida.getHour()),
                "minutos", String.valueOf(fechaValida.getMinute())
        );

        // Simulamos sesión con cookie JWT válida
        String jwt = null;
        try {
            jwt = AuthService.CrearTokenJWT("PAC123","paciente");
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(HttpHeaders.COOKIE, "JWT=" + jwt);

        ResponseEntity<Map> res = restTemplate.exchange("/api/paciente/citas",
                HttpMethod.POST,
                new HttpEntity<>(req, headers),
                Map.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertNotNull(res.getBody());
        // Verifica que la cita realmente existe
        assertEquals(1, citaService.obtenerCitaPacienteFuturo("PAC123").size());
    }
}

