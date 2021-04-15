package main.config.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import main.config.security.SecurityConstants;

import java.util.Date;

public class JwtProvider
{
    private static final Algorithm algorithm = Algorithm.none();

    public static String createToken() {
        return JWT.create()
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME_IN_MILLIS))
                .sign(algorithm);
    }

    public static boolean validateToken(String token)
    {
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            verifier.verify(token.substring(0, token.length() - SecurityConstants.SUFFIX.length()));
            return true;
        } catch (JWTDecodeException | TokenExpiredException | AlgorithmMismatchException e) {
            return false;
        }
    }
}