package sit.int202.ecommerce.modules.user.service;

import org.springframework.web.multipart.MultipartFile;
import sit.int202.ecommerce.modules.auth.dto.RegisterRequest;
import sit.int202.ecommerce.modules.user.model.UserAccount;

import java.util.Optional;

public interface UserService {

    Optional<UserAccount> findByEmail(String email);

    boolean verifyPassword(UserAccount user, String rawPassword);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    UserAccount registerUser(RegisterRequest request, MultipartFile frontImage, MultipartFile backImage);

    UserAccount activateUser(String email);
}