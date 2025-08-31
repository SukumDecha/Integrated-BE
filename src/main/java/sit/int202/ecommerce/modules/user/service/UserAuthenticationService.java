package sit.int202.ecommerce.modules.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sit.int202.ecommerce.modules.user.dto.request.UserLoginRequest;
import sit.int202.ecommerce.modules.user.model.UserAccount;
import sit.int202.ecommerce.modules.user.repository.UserAccountRepository;

import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserAuthenticationService {

    private final UserAccountRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public ResponseEntity<?> authenticate(UserLoginRequest req) {
        String email = req.getEmail();
        String password = req.getPassword();

        // Validate ตาม PBI22
        if (!isValidEmail(email) || password == null || password.isBlank() || password.length() > 14) {
            return ResponseEntity.badRequest().body("Email or password is invalid.");
        }

        Optional<UserAccount> optionalUser = repo.findByEmail(email.trim());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email or Password is incorrect");
        }

        UserAccount user = optionalUser.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email or Password is incorrect");
        }

        // PBI23: ต้อง active ก่อน
        if (!user.isActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You need to activate your account before signing in.");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return ResponseEntity.ok(Map.of(
                "access_token", accessToken,
                "refresh_token", refreshToken
        ));
    }

    private boolean isValidEmail(String email) {
        return email != null && email.length() <= 50 &&
                email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}
