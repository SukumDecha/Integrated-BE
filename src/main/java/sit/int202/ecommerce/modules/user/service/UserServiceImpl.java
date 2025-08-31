package sit.int202.ecommerce.modules.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import sit.int202.ecommerce.modules.user.dto.request.UserRegisterRequest;
import sit.int202.ecommerce.modules.file.model.FileEntity;
import sit.int202.ecommerce.modules.file.service.FileServiceImpl;
import sit.int202.ecommerce.modules.user.mapper.UserMapper;
import sit.int202.ecommerce.modules.user.model.UserAccount;
import sit.int202.ecommerce.modules.user.model.UserAccountType;
import sit.int202.ecommerce.modules.user.repository.UserAccountRepository;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserAccountRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final FileServiceImpl fileService;


    public Optional<UserAccount> findByEmail(String email) {
        return repo.findByEmail(email);
    }

    public boolean verifyPassword(UserAccount user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public boolean existsByEmail(String email) {
        return repo.existsByEmail(email);
    }

    public boolean existsByNickname(String nickname) {
        return repo.existsByNickname(nickname);
    }

    public UserAccount registerUser(UserRegisterRequest request, MultipartFile frontImage, MultipartFile backImage) {
        UserAccount user = userMapper.toEntity(request);
        user.setActive(false);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        if (request.getUserType() == UserAccountType.SELLER) {
            user.setMobileNumber(request.getMobileNumber());
            user.setBankAccountNumber(request.getBankAccountNumber());
            user.setBankName(request.getBankName());
            user.setIdCardNumber(request.getIdCardNumber());
        }

        repo.save(user);

        if (user.getType() == UserAccountType.SELLER && frontImage != null && backImage != null) {
            FileEntity frontFile = fileService.uploadSingleFile(frontImage, "USER_ACCOUNT", user.getId(), 0, "ID_CARD_FRONT");
            FileEntity backFile = fileService.uploadSingleFile(backImage, "USER_ACCOUNT", user.getId(), 0, "ID_CARD_BACK");

            user.getIdCardImageFront().add(frontFile);
            user.getIdCardImageBack().add(backFile);
            repo.save(user);
        }

        return user;
    }

    public UserAccount activateUser(String email) {
        UserAccount user = repo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already active");
        }

        user.setActive(true);
        return repo.save(user);
    }

}
