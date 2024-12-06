package com.rentalmovie.movie.testutils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

public class JwtTokenUtil {

    private static final Key SECRET_KEY = Keys.hmacShaKeyFor("FhCH5aXYk0VGbgxQCHY9lP1cNjhO7MZH".getBytes());

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
