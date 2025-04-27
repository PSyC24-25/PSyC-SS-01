package es.deusto.spq.doctorclick.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    @Test
    void testCrearYValidarToken() throws JOSEException, ParseException {
        String dni = "12345678A";
        String tipo = "medico";

        // Crear un token
        String token = AuthService.CrearTokenJWT(dni, tipo);

        assertNotNull(token);
        assertTrue(AuthService.EsJWTValido(token), "El token recién creado debería ser válido");

        // Obtener claims
        JWTClaimsSet claims = AuthService.ObtenerClaimsJWT(token);
        assertEquals(dni, claims.getStringClaim("dni"));
        assertEquals(tipo, claims.getStringClaim("tipo"));
    }

    @Test
    void testTokenInvalido() throws JOSEException, ParseException {
        // Token inventado, mal firmado
        String fakeToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmYWtlIn0.dummyfirma";

        assertFalse(AuthService.EsJWTValido(fakeToken), "Un token mal firmado debe ser inválido");
    }

    @Test
    void testTokenNulo() throws JOSEException, ParseException {
        assertFalse(AuthService.EsJWTValido(null), "Un token null debe ser inválido");
    }

    @Test
    void testTokenExpirado() throws JOSEException, InterruptedException, ParseException {
        // Crear token que expira casi inmediatamente
        String dni = "87654321B";
        String tipo = "paciente";

        // Hack: Crear token que dura 1 segundo
        JWSSigner signer = new MACSigner("f9d7776651394fdae1b70e819f8149c51c52d49e19962ee6cb6b193ce7f6f18a".getBytes());
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .expirationTime(new Date(new Date().getTime() + 1000)) // 1 segundo
                .claim("dni", dni)
                .claim("tipo", tipo)
                .build();
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
        signedJWT.sign(signer);
        String token = signedJWT.serialize();

        // Esperar a que expire
        Thread.sleep(1500);

        assertFalse(AuthService.EsJWTValido(token), "El token expirado debe ser inválido");
    }
}
