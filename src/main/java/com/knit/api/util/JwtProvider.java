package com.knit.api.util;

import com.knit.api.domain.user.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;

@Component
public class JwtProvider {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.time}")
    private long validityInMs;

    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMs);

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("nickname", user.getNickname())
                .claim("role", user.getRole().name())
                .claim("provider", user.getProvider().name())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), HS256)
                .compact();
    }
}