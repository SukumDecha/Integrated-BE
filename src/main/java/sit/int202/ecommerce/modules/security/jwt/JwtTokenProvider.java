package sit.int202.ecommerce.modules.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sit.int202.ecommerce.modules.user.model.UserAccount;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey key;

    @PostConstruct
    void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(UserAccount user) {
        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(user.getEmail())
                .claim("id", user.getId())
                .claim("email", user.getEmail())
                .claim("nickname", user.getNickname())
                .claim("role", user.getType().name())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(30, ChronoUnit.MINUTES)))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(UserAccount user) {
        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject("refresh:" + user.getId())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(24, ChronoUnit.HOURS)))
                .signWith(key)
                .compact();
    }

    public String generateEmailToken(UserAccount user) {
        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(24, ChronoUnit.HOURS)))
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}