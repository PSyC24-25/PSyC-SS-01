package es.deusto.spq.doctorclick.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jwt.*;

import java.text.ParseException;
import java.util.Date;

public class AuthService {
    // La llave es de prueba, esto deberia de ir en un .env
    private static final byte[] PRIVATE_KEY = "f9d7776651394fdae1b70e819f8149c51c52d49e19962ee6cb6b193ce7f6f18a".getBytes();

    public static String CrearTokenJWT(String dni, String tipo) throws JOSEException {
        JWSSigner signer = new MACSigner(PRIVATE_KEY);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .expirationTime(new Date(new Date().getTime() + 60 * 1000 * 60)) // 1 hora
                .claim("dni", dni)
                .claim("tipo", tipo)
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    public static boolean EsJWTValido(String tokenJwt) throws JOSEException, ParseException {
        if(tokenJwt == null) return false;

        SignedJWT signedJWT = SignedJWT.parse(tokenJwt);
        JWSVerifier verifier = new MACVerifier(PRIVATE_KEY);

        if(!signedJWT.verify(verifier)) {
            System.out.println("La firma del token no es valida.");
            return false;
        }

        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
        Date expirationTime = claims.getExpirationTime();
        if (expirationTime != null && expirationTime.before(new Date())) {
            System.out.println("Token expirado");
            return false;
        }

        return true;
    }

    public static JWTClaimsSet ObtenerClaimsJWT(String tokenJwt) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(tokenJwt);
        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

        return claims;
    }
}
