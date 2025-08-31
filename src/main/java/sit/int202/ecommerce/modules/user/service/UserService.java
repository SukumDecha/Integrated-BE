package sit.int202.ecommerce.modules.user.service;

import org.springframework.web.multipart.MultipartFile;
import sit.int202.ecommerce.modules.user.dto.request.UserRegisterRequest;
import sit.int202.ecommerce.modules.user.model.UserAccount;

import java.util.Optional;

public interface UserService {

    Optional<UserAccount> findByEmail(String email);

    boolean verifyPassword(UserAccount user, String rawPassword);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    UserAccount registerUser(UserRegisterRequest request, MultipartFile frontImage, MultipartFile backImage);

    UserAccount activateUser(String email);
}