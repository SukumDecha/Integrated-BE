package sit.int202.ecommerce.modules.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import sit.int202.ecommerce.modules.file.model.FileEntity;
import sit.int202.ecommerce.modules.file.service.FileService;
import sit.int202.ecommerce.modules.user.dto.request.UserRegisterRequest;
import sit.int202.ecommerce.modules.user.dto.response.UserRegisterResponse;
import sit.int202.ecommerce.modules.user.mapper.UserMapper;
import sit.int202.ecommerce.modules.user.model.UserAccountType;
import sit.int202.ecommerce.modules.user.model.UserAccount;
import sit.int202.ecommerce.modules.user.repository.UserAccountRepository;

import java.io.IOException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserAccountRepository repo;
    private final UserMapper userMapper;

    private final FileService fileService;

    public UserRegisterResponse register(UserRegisterRequest req, MultipartFile front, MultipartFile back) {
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

        if (req.getUserType() == UserAccountType.SELLER) {
            user.setMobileNumber(req.getMobileNumber());
            user.setBankAccountNumber(req.getBankAccountNumber());
            user.setBankName(req.getBankName());
            user.setNationalId(req.getNationalIdNumber());
        }

        if (user.getType() == UserAccountType.SELLER) {
            try {
                FileEntity frontFile = fileService.saveFile(front, "nid", user.getId(), 0);
                FileEntity backFile = fileService.saveFile(back, "nid", user.getId(), 1);

                user.setNationalIdFrontImage(frontFile);
                user.setNationalIdBackImage(backFile);
            } catch (IOException e) {
                log.error("Error saving national ID images", e);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error saving national ID images");
            }
        }


        repo.save(user);
        return userMapper.toRegisterResponse(user);
    }

}
