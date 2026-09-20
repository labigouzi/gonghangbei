package com.yilu.yinling.security;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Collection;
import java.util.List;
@Component
public class JwtTokenProvider {
    private final SecretKey key; private final long expiration;
    public JwtTokenProvider(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration-ms}") long expiration) { key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); this.expiration = expiration; }
    public String create(Long userId, String username) { return create(userId, username, List.of("USER")); }
    public String create(Long userId, String username, Collection<String> roles) { Date now = new Date(); return Jwts.builder().subject(username).claim("userId", userId).claim("roles", roles).issuedAt(now).expiration(new Date(now.getTime()+expiration)).signWith(key).compact(); }
    public Claims parse(String token) { return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload(); }
}
