package es.deusto.spq.doctorclick.IntegrationTest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.deusto.spq.doctorclick.model.Paciente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LoginIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        Paciente paciente = new Paciente();
        paciente.setDni("12345678A");
        paciente.setContrasenia("1234");
        paciente.setNombre("Juan Test");
    }

    // @Test
    // void testLoginPacienteExitoso() {
    //     Map<String, String> request = new HashMap<>();
    //     request.put("dni", "12345678A");
    //     request.put("contrasena", "1234");
    //     request.put("tipoUsuario", "paciente");

    //     HttpHeaders headers = new HttpHeaders();
    //     headers.setContentType(MediaType.APPLICATION_JSON);
    //     HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

    //     ResponseEntity<Map> response = restTemplate.exchange("/api/auth/login", HttpMethod.POST, entity, Map.class);

    //     assertEquals(HttpStatus.OK, response.getStatusCode());
    //     assertNotNull(response.getBody());
    //     assertEquals("Usuario login con éxito", response.getBody().get("message"));

    //     HttpHeaders responseHeaders = response.getHeaders();
    //     List<String> cookies = responseHeaders.get(HttpHeaders.SET_COOKIE);
    //     System.out.println("Cookies en la respuesta: " + cookies);
    //     assertNotNull(cookies, "No se recibieron cookies en la respuesta");

    //     boolean hasJWTCookie = cookies.stream().anyMatch(cookie -> cookie.startsWith("JWT="));
    //     assertTrue(hasJWTCookie, "La respuesta debería incluir una cookie llamada 'JWT'");
    // }
}
