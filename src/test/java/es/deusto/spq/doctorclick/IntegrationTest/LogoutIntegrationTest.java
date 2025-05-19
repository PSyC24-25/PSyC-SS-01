package es.deusto.spq.doctorclick.IntegrationTest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LogoutIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void logout_FinalPageIsLogin() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/auth/logout",
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Contiene algo de la página de login
        assertTrue(response.getBody().contains("form"));
    }
}