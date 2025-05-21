package es.deusto.spq.doctorclick.IntegrationTest;

import com.nimbusds.jose.JOSEException;
import es.deusto.spq.doctorclick.model.Especialidad;
import es.deusto.spq.doctorclick.model.Medico;
import es.deusto.spq.doctorclick.model.Paciente;
import es.deusto.spq.doctorclick.service.AuthService;
import es.deusto.spq.doctorclick.service.CitaService;
import es.deusto.spq.doctorclick.service.MedicoService;
import es.deusto.spq.doctorclick.service.PacienteService;
import jakarta.transaction.Transactional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(statements = "DELETE FROM usuario;")
class PerfilIntegrationTest {

    @Autowired TestRestTemplate restTemplate;
    @Autowired MedicoService medicoService;
    @Autowired PacienteService pacienteService;

    Medico medico;
    String tokenMedico;

    Paciente paciente;
    String tokenPaciente;

    @Transactional
    @BeforeEach
    void datos() throws JOSEException {
        // Registrar un paciente y crear token de sesion
        paciente = new Paciente();
        paciente.setDni("11111111H");
        paciente.setNombre("Juan");
        paciente.setApellidos("Hernandez Isidro");
        paciente.setContrasenia("1234");
        pacienteService.registrarPaciente(paciente);
        tokenPaciente = AuthService.CrearTokenJWT(paciente.getDni(), "paciente");

        // Registrar un medico y crear token de sesion
        medico = new Medico();
        medico.setEspecialidad(Especialidad.NEUROLOGIA);
        medico.setDni("22222222J");
        medico.setNombre("Ignacio");
        medico.setApellidos("Rodriguez Perez");
        medico.setContrasenia("1234");
        medicoService.registrarMedico(medico);
        tokenMedico = AuthService.CrearTokenJWT(medico.getDni(), "medico");
    }

    @Test
    @DisplayName("Paciente visita apartado \"perfil\"")
    void obtenerPerfilPaciente() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, "JWT=" + tokenPaciente);

        ResponseEntity<String> response = restTemplate.exchange("/paciente/perfil",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Document doc = Jsoup.parse(response.getBody());

        String titulo = doc.title();
        assertEquals("Tu perfil | Doctorclick", titulo);


        Elements tds = doc.select("main > div > table > tbody > tr > td");
        assertEquals(3, tds.size());
        assertEquals(paciente.getDni(), tds.get(0).text());
        assertEquals(paciente.getNombre(), tds.get(1).text());
        assertEquals(paciente.getApellidos(), tds.get(2).text());
    }

    @Test
    @DisplayName("Medico visita apartado \"perfil\"")
    void obtenerPerfilMedico() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, "JWT=" + tokenMedico);

        ResponseEntity<String> response = restTemplate.exchange("/medico/perfil",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Document doc = Jsoup.parse(response.getBody());

        String titulo = doc.title();
        assertEquals("Tu perfil | Doctorclick", titulo);


        Elements tds = doc.select("main > div > table > tbody > tr > td");
        assertEquals(4, tds.size());
        assertEquals(medico.getDni(), tds.get(0).text());
        assertEquals(medico.getNombre(), tds.get(1).text());
        assertEquals(medico.getApellidos(), tds.get(2).text());
        assertEquals(medico.getEspecialidad().toString(), tds.get(3).text());
    }
}