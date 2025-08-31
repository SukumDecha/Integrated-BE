package sit.int202.ecommerce.modules.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import sit.int202.ecommerce.common.utils.JwtUtils;
import sit.int202.ecommerce.modules.email.service.EmailService;
import sit.int202.ecommerce.modules.file.model.FileEntity;
import sit.int202.ecommerce.modules.file.service.FileServiceImpl;
import sit.int202.ecommerce.modules.user.dto.request.UserRegisterRequest;
import sit.int202.ecommerce.modules.user.dto.response.UserResponse;
import sit.int202.ecommerce.modules.user.mapper.UserMapper;
import sit.int202.ecommerce.modules.user.model.UserAccountType;
import sit.int202.ecommerce.modules.user.model.UserAccount;
import sit.int202.ecommerce.modules.user.repository.UserAccountRepository;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserAccountRepository repo;
    private final UserMapper userMapper;

    private final EmailService emailService;
    private final FileServiceImpl fileService;

    private final JwtUtils jwtUtils;

    private final PasswordEncoder passwordEncoder;

    /**
     * Register a new user
     * @param req
     * @param front
     * @param back
     * @return
     */
    public UserResponse register(UserRegisterRequest req, MultipartFile front, MultipartFile back) {
        if (repo.existsByEmail(req.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already used");
        }
        if (repo.existsByNickname(req.getNickname())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nickname already used");
        }

        if (req.getUserType() == UserAccountType.SELLER && (front == null || back == null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "National ID images are required for sellers");
        }

        UserAccount user = userMapper.toEntity(req);
        user.setActive(false);
        user.setPassword(passwordEncoder.encode(req.getPassword()));

        if (req.getUserType() == UserAccountType.SELLER) {
            user.setMobileNumber(req.getMobileNumber());
            user.setBankAccountNumber(req.getBankAccountNumber());
            user.setBankName(req.getBankName());
            user.setIdCardNumber(req.getIdCardNumber());
        }

        repo.save(user);

        if (user.getType() == UserAccountType.SELLER) {
            FileEntity frontFile = fileService.uploadSingleFile(front, "USER_ACCOUNT", user.getId(), 0, "ID_CARD_FRONT");
            FileEntity backFile = fileService.uploadSingleFile(back, "USER_ACCOUNT", user.getId(), 0, "ID_CARD_BACK");

            user.getIdCardImageFront().add(frontFile);
            user.getIdCardImageBack().add(backFile);
        }

        String token = jwtUtils.generateToken(user.getEmail());
        emailService.sendVerificationEmail(user.getEmail(), user.getNickname(), token);

        repo.save(user);
        return userMapper.toRegisterResponse(user);
    }

    /**
     * Verify email using JWT token
     * @param token
     * @return
     */
    public UserResponse verifyEmail(String token) {
        String email;
        try {
            email = jwtUtils.getUsernameFromToken(token);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired token");
        }

        UserAccount user = repo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already active");
        }

        user.setActive(true);
        repo.save(user);

        return userMapper.toRegisterResponse(user);
    }

    public boolean verifyLogin(String email, String rawPassword) {
        Optional<UserAccount> optionalUser = repo.findByEmail(email);
        if (optionalUser.isEmpty()) return false;

        UserAccount user = optionalUser.get();
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }


}
