package es.deusto.spq.doctorclick.IntegrationTest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nimbusds.jose.JOSEException;
import es.deusto.spq.doctorclick.model.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("Paciente consulta horas disponibles y crea una cita correctamente")
    void pacienteConsultaYAgendaCita() {

        Map<String, String> request = new HashMap<>();
        request.put("dni", "PAC123");
        request.put("contrasena", "1234");
        request.put("tipoUsuario", "paciente");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> respuesta = restTemplate.exchange("/api/auth/login", HttpMethod.POST, entity, Map.class);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals("Usuario login con éxito", respuesta.getBody().get("message"));

        HttpHeaders responseHeaders = respuesta.getHeaders();
        List<String> cookies = responseHeaders.get(HttpHeaders.SET_COOKIE);
        System.out.println("Cookies en la respuesta: " + cookies);
        assertNotNull(cookies, "No se recibieron cookies en la respuesta");

        boolean hasJWTCookie = cookies.stream().anyMatch(cookie -> cookie.startsWith("JWT="));
        assertTrue(hasJWTCookie, "La respuesta debería incluir una cookie llamada 'JWT'");

        // Paso 1: Obtener una hora disponible
        LocalDate fechaConsulta = LocalDate.now().plusDays(1);
        List<LocalDateTime> horasDisponibles = citaService.obtenerHorasDisponibles(idMedico, fechaConsulta);
        assertFalse(horasDisponibles.isEmpty(), "No hay horas disponibles para el médico");
        LocalDateTime horaDisponible = horasDisponibles.get(0);

        // Paso 2: Comprobar que la API devuelve esa hora disponible
        String url = String.format("/api/paciente/citas/disponibles?anyo=%d&mes=%d&dia=%d&medico=%d",
                fechaConsulta.getYear(),
                fechaConsulta.getMonthValue(),
                fechaConsulta.getDayOfMonth(),
                idMedico
        );
        ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        List<Integer> horaEsperada = List.of(
                horaDisponible.getYear(),
                horaDisponible.getMonthValue(),
                horaDisponible.getDayOfMonth(),
                horaDisponible.getHour(),
                horaDisponible.getMinute()
        );
        assertTrue(response.getBody().contains(horaEsperada), "La hora esperada no está en la lista de horas disponibles");

        // Paso 3: Crear una cita para esa hora
        Map<String,String> req = Map.of(
                "idMedico", idMedico.toString(),
                "razon", "Dolor en el pecho desde ayer",
                "anyo", String.valueOf(horaDisponible.getYear()),
                "mes", String.valueOf(horaDisponible.getMonthValue()),
                "dia", String.valueOf(horaDisponible.getDayOfMonth()),
                "hora", String.valueOf(horaDisponible.getHour()),
                "minutos", String.valueOf(horaDisponible.getMinute())
        );

        String jwt;
        try {
            jwt = AuthService.CrearTokenJWT("PAC123","paciente");
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }


        headers.add(HttpHeaders.COOKIE, "JWT=" + jwt);

        ResponseEntity<Map> res = restTemplate.exchange(
                "/api/paciente/citas",
                HttpMethod.POST,
                new HttpEntity<>(req, headers),
                Map.class
        );

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertNotNull(res.getBody());

        // Paso 4: Verificar que la cita se ha creado correctamente
        List<Cita> citas = citaService.obtenerCitaPacienteFuturo("PAC123");
        assertEquals(1, citas.size(), "La cita no fue registrada correctamente");



        ResponseEntity<String> response2 = restTemplate.getForEntity(
                "/api/auth/logout",
                String.class
        );

        assertEquals(HttpStatus.OK, response2.getStatusCode());
        // Contiene algo de la página de login
        assertTrue(response2.getBody().contains("form"));
    }
}

