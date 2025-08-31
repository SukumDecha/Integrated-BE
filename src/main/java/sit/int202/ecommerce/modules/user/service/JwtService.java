package sit.int202.ecommerce.modules.user.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sit.int202.ecommerce.modules.user.model.UserAccount;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.issuer:http://intproj24.sit.kmutt.ac.th/ssal/}")
    private String issuer; // เปลี่ยนค่าจริงใน application-*.yml ได้

    @Value("${jwt.secret:CHANGE_ME_TO_A_256BIT_RANDOM_STRING________________________________}")
    private String secret; // ควรเป็นค่า 32 bytes+ (256-bit)

    private SecretKey key;

    @PostConstruct
    void init() {
        // แปลง secret เป็น HMAC-SHA key
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(UserAccount user) {
        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(user.getEmail())
                .claim("id", user.getId())
                .claim("email", user.getEmail())
                .claim("nickname", user.getNickname())
                .claim("role", user.getType().name()) // ENUM -> STRING
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(30, ChronoUnit.MINUTES))) // อายุ 30 นาที
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(UserAccount user) {
        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject("refresh:" + user.getId())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(24, ChronoUnit.HOURS))) // อายุ 24 ชั่วโมง
                .signWith(key)
                .compact();
    }
}
