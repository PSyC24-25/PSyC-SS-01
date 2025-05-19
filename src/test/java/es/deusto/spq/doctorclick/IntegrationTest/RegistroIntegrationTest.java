package es.deusto.spq.doctorclick.IntegrationTest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import es.deusto.spq.doctorclick.model.Paciente;
import es.deusto.spq.doctorclick.repository.PacienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(statements = {
        "DELETE FROM cita",
        "DELETE FROM usuario"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class RegistroIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PacienteRepository pacienteRepository;

    @BeforeEach
    void cleanUp() {
        pacienteRepository.findByDni("PAC123").ifPresent(pacienteRepository::delete);
    }

    @Test
    void registroPaciente_Exitoso() {
        Map<String,String> body = Map.of(
                "dni",        "12345678Z",
                "nombre",     "Pepe",
                "apellidos",  "Gomez Ruiz",
                "contrasena", "abcd",
                "contrasena2","abcd",
                "tipo",       "paciente",
                "especialidad","");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/auth/registro",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                Map.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Optional<Paciente> creado = pacienteRepository.findByDni("12345678Z");
        assertTrue(creado.isPresent(), "El paciente debería haberse guardado en BD");

        List<String> setCookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertNotNull(setCookies);
        assertTrue(setCookies.stream().anyMatch(c -> c.startsWith("JWT=")));
    }
}

