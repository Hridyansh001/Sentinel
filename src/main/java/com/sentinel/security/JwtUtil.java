package com.sentinel.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    private Key getKey(){return Keys.hmacShaKeyFor(secret.getBytes());}
    private static final long EXPIRATION = 1000*60*60*24;

    public String generateToken(String email)
    {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+EXPIRATION))
                .signWith(getKey())
                .compact();
    }
    public String extractEmail(String token)
    {
        return Jwts.parser()
                .verifyWith((SecretKey)getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
    public boolean validateToken(String token)
    {
        try{
            Jwts.parser()
                    .verifyWith((SecretKey) getKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

}
