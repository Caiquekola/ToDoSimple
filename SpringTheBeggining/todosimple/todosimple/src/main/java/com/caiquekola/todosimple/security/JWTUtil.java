package com.caiquekola.todosimple.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.Objects;

@Component
public class JWTUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String generateToken(String username) {
        SecretKey key = getKeyBySecret();
        return String.valueOf((Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + (expiration)))));
    }

    private SecretKey getKeyBySecret( ) {
        SecretKey key = Keys.hmacShaKeyFor(this.secret.getBytes());
        return key;
    }

    public boolean isValidToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if(Objects.nonNull(claims)) {
            String username = claims.getSubject();
            Date expiration = claims.getExpiration();
            Date now = new Date(System.currentTimeMillis());
            if(Objects.nonNull(username)&&Objects.nonNull(expiration)&&now.before(expiration)) {
                return true;
            }
        }
        return false;
    }

    private Claims getClaimsFromToken(String token) {
        SecretKey key = getKeyBySecret();
        try{
            return Jwts.parserBuilder().setSigningKey(key).build().
                    parseClaimsJws(token).getBody();
        }catch (Exception e) {
            return null;
        }
    }

}
