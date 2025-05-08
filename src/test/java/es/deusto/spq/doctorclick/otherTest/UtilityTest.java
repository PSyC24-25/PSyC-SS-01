package es.deusto.spq.doctorclick.otherTest;

import com.nimbusds.jwt.JWTClaimsSet;
import es.deusto.spq.doctorclick.Utility;
import es.deusto.spq.doctorclick.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UtilityTest {

    @Test
    void testObtenerDniConTokenValido() throws Exception {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie[] cookies = { new Cookie("JWT", "fake-jwt-token") };
        when(request.getCookies()).thenReturn(cookies);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .claim("dni", "12345678A")
                .build();

        try (MockedStatic<AuthService> mockedStatic = mockStatic(AuthService.class)) {
            mockedStatic.when(() -> AuthService.ObtenerClaimsJWT("fake-jwt-token"))
                    .thenReturn(claimsSet);

            // Act
            String dni = Utility.obtenerDni(request);

            // Assert
            assertEquals("12345678A", dni);
        }
    }

    @Test
    void testObtenerDniSinCookie() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(null);

        Exception exception = assertThrows(Exception.class, () -> {
            Utility.obtenerDni(request);
        });

        assertEquals("Token no encontrado en las cookies", exception.getMessage());
    }

    @Test
    void testObtenerDniSinDniEnToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie[] cookies = { new Cookie("JWT", "token-sin-dni") };
        when(request.getCookies()).thenReturn(cookies);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().build(); // no tiene "dni"

        try (MockedStatic<AuthService> mockedStatic = mockStatic(AuthService.class)) {
            mockedStatic.when(() -> AuthService.ObtenerClaimsJWT("token-sin-dni"))
                    .thenReturn(claimsSet);

            Exception exception = assertThrows(Exception.class, () -> {
                Utility.obtenerDni(request);
            });

            assertEquals("DNI no encontrado en el token", exception.getMessage());
        }
    }
}
