package com.project.sikbbang.app.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secretKey}")
    private String secretKeyString;

    @Value("${jwt.access.expiration}")
    private long accessTokenExpiration;

    private volatile SecretKey cachedKey;

    private SecretKey signingKey() {
        SecretKey local = cachedKey;
        if (local != null) return local;

        byte[] keyBytes = decodeSecret(secretKeyString);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException(
                    "JWT secret must be >= 256 bits. Provided: " + (keyBytes.length * 8) + " bits"
            );
        }
        local = Keys.hmacShaKeyFor(keyBytes);
        cachedKey = local;
        return local;
    }

    private static byte[] decodeSecret(String s) {
        String v = s == null ? "" : s.trim();
        try {
            return Decoders.BASE64.decode(v);
        } catch (Exception ignore) { }
        if (v.matches("^[0-9a-fA-F]+$") && (v.length() % 2 == 0)) {
            int len = v.length();
            byte[] data = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(v.charAt(i), 16) << 4)
                        + Character.digit(v.charAt(i + 1), 16));
            }
            return data;
        }
        return v.getBytes(StandardCharsets.UTF_8);
    }

    private static String stripBearer(String token) {
        if (token == null) return null;
        String t = token.trim();
        if (t.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return t.substring(7).trim();
        }
        return t;
    }

    public String createToken(String adminId) {
        return createToken(adminId, null);
    }

    public String createToken(String adminId, String role) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + accessTokenExpiration);

        io.jsonwebtoken.ClaimsBuilder cb = Jwts.claims().subject(adminId);
        if (role != null) cb.add("role", role);
        Claims claims = cb.build();

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(exp)
                .signWith(signingKey(), Jwts.SIG.HS256)
                .compact();
    }

    private Claims parseClaims(String token) {
        String raw = stripBearer(token);
        var jws = Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(raw);
        return jws.getPayload();
    }

    public String getAdminIdFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public String getRoleFromToken(String token) {
        Object role = parseClaims(token).get("role");
        return role == null ? null : role.toString();
    }

    public Date getExpiration(String token) {
        return parseClaims(token).getExpiration();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (SecurityException e) {
            return false;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return false;
        } catch (RuntimeException e) {
            return false;
        }
    }
}
