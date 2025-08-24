package sit.int202.ecommerce.modules.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import sit.int202.ecommerce.modules.file.dto.FileResponse;
import sit.int202.ecommerce.modules.file.model.File;
import sit.int202.ecommerce.modules.file.service.FileService;
import sit.int202.ecommerce.modules.saleitem.dto.request.SaleItemImageRequest;
import sit.int202.ecommerce.modules.user.dto.request.UserRegisterRequest;
import sit.int202.ecommerce.modules.user.dto.response.UserRegisterResponse;
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
    private final PasswordEncoder encoder;
    private final FileService fileService;
    private SaleItemImageRequest nationalIdFrontImage;
    private SaleItemImageRequest nationalIdBackImage;

    public UserRegisterResponse register(UserRegisterRequest req, MultipartFile front, MultipartFile back) throws IOException {


        if (repo.existsByEmail(req.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already used");
        }
        if (repo.existsByNickname(req.getNickname())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nickname already used");
        }

        UserAccount u = new UserAccount();
        u.setType(req.getUserAccountType());
        u.setFullname(req.getFullName().trim());
        u.setNickname(req.getNickname().trim());
        u.setEmail(req.getEmail().trim().toLowerCase());
        u.setPassword(encoder.encode(req.getPassword()));
        u.setActive(false);

        if (req.getUserAccountType() == UserAccountType.SELLER) {
            u.setMobileNumber(req.getMobileNumber());
            u.setBankAccountNumber(req.getBankAccountNumber());
            u.setBankName(req.getBankName());
            u.setNationalId(req.getNationalIdNumber());
        }

        // Save user ก่อน เพื่อให้มี u.getId()
        repo.save(u);

        if (u.getType() == UserAccountType.SELLER) {
            File frontFile = fileService.saveFile(front, "nid", u.getId(), 0);
            File backFile = fileService.saveFile(back, "nid", u.getId(), 1);

            u.setNationalIdFrontImage(frontFile.getFilePath());
            u.setNationalIdBackImage(backFile.getFilePath());
        }


        repo.save(u);

        UserRegisterResponse resp = new UserRegisterResponse();
        resp.setId(u.getId());
        resp.setNickname(u.getNickname());
        resp.setEmail(u.getEmail());
        resp.setFullName(u.getFullname());
        resp.setAccountType(u.getType());
        resp.setCreatedOn(u.getCreatedOn());
        resp.setUpdatedOn(u.getUpdatedOn());
        resp.setBankName(u.getBankName());
        resp.setBankAccountNumber(u.getBankAccountNumber());
        resp.setMobileNumber(u.getMobileNumber());
        resp.setNationalIdNumber(u.getNationalId());

        File frontFile = fileService.getFileByStoredFilename(u.getNationalIdFrontImage());
        File backFile = fileService.getFileByStoredFilename(u.getNationalIdBackImage());

        resp.setNationalIdFront(buildFileResponse(frontFile));
        resp.setNationalIdBack(buildFileResponse(backFile));


        return resp;
    }

    private FileResponse buildFileResponse(File file) {
        if (file == null) return null;
        return FileResponse.builder()
                .fileName(file.getStoredFilename())
                .url("/uploads/" + file.getStoredFilename())
                .base64(fileService.convertToBase64(file.getStoredFilename()))
                .build();
    }


}
