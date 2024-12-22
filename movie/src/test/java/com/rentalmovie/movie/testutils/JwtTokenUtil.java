package com.rentalmovie.movie.testutils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

public class JwtTokenUtil {

    private static final Key SECRET_KEY = Keys.hmacShaKeyFor("88c0dfeab9520b2e50cfa2d407ed0914764ac6e8fd524aa3e82b9f37268bbcc3".getBytes());

    public static String generateToken(String subject, String roles, long expirationMillis) {
        return Jwts.builder()
                .subject(subject)
                .claim("roles", roles)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(expirationMillis)))
                .signWith(SECRET_KEY)
                .compact();
    }
}
