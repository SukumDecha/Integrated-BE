package sit.int202.ecommerce.modules.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import sit.int202.ecommerce.modules.security.services.UserDetailsServiceImpl;
import sit.int202.ecommerce.modules.user.dto.response.UserResponse;
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

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostConstruct
    void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(UserResponse user) {
        return Jwts.builder()
                .setIssuer(issuer)
                .claim("id", user.getId())
                .claim("email", user.getEmail())
                .claim("nickname", user.getNickname())
                .claim("role", user.getUserType().name())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(30, ChronoUnit.MINUTES)))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(UserResponse user) {
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
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(24, ChronoUnit.HOURS)))
                .signWith(key)
                .claim("email", user.getEmail())
                .compact();
    }

    public boolean validateAccessToken(String token) {
        try {
            var claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Extra validation for Access Tokens
            if (claims.get("email", String.class) == null || claims.get("role", String.class) == null) {
                System.out.println("[JWT] ❌ Invalid access token: missing claims");
                return false;
            }

            System.out.println("[JWT] ✅ Access token is valid");
            return true;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired JWT token");
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            var claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Refresh tokens are issued with "refresh:<userId>" as subject
            String subject = claims.getSubject();
            if (subject == null || !subject.startsWith("refresh:")) {
                System.out.println("[JWT] ❌ Invalid refresh token: bad subject");
                return false;
            }

            System.out.println("[JWT] ✅ Refresh token is valid");
            return true;
        } catch (Exception e) {
            System.out.println("[JWT] ❌ Invalid refresh token: " + e.getMessage());
            return false;
        }
    }

    public int getUserIdFromRefreshToken(String refreshToken) {
        var claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();
        String subject = claims.getSubject();
        return Integer.parseInt(subject.replace("refresh:", ""));
    }

    public String getEmailFromToken(String token) {
        try {
            String email = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("email", String.class);
            System.out.println("[JWT] ✅ Email from token = " + email);
            return email;
        } catch (Exception e) {
            System.out.println("[JWT] ❌ Failed to extract email: " + e.getMessage());
            return null;
        }
    }

    public Authentication getAuthentication(String token) {
        String email = getEmailFromToken(token);
        if (email == null) {
            System.out.println("[JWT] ❌ Email is null from token");
            return null;
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        System.out.println("[JWT] ✅ Loaded user: " + userDetails.getUsername());

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}