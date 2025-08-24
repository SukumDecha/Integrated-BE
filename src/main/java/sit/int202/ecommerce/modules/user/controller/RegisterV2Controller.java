package sit.int202.ecommerce.modules.user.controller;

import jakarta.validation.*;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import sit.int202.ecommerce.modules.user.dto.request.UserRegisterRequest;
import sit.int202.ecommerce.modules.user.dto.response.UserRegisterResponse;
import sit.int202.ecommerce.modules.user.model.UserAccountType;
import sit.int202.ecommerce.modules.user.service.UserService;
import sit.int202.ecommerce.modules.user.validation.SellerGroup;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

@RestController
@RequestMapping("/v2")
public class RegisterV2Controller {
    private final UserService service;
    private final Validator validator;

    public RegisterV2Controller(UserService service) {
        this.service = service;
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserRegisterResponse> register(
            @RequestPart("data") @Validated UserRegisterRequest data,
            @RequestPart(value = "nationalIdFront", required = false) MultipartFile nationalIdFront,
            @RequestPart(value = "nationalIdBack", required = false) MultipartFile nationalIdBack
    ) {
        // ตรวจสอบข้อมูลเฉพาะกรณีเป็น SELLER
        if (data.getUserAccountType() == UserAccountType.SELLER) {
            Set<ConstraintViolation<UserRegisterRequest>> errs = validator.validate(data, SellerGroup.class);
            if (!errs.isEmpty()) {
                throw new ConstraintViolationException(errs);
            }

            if (nationalIdFront == null || nationalIdBack == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seller must upload nationalIdFront and nationalIdBack");
            }
        }

        try {
            UserRegisterResponse resp = service.register(data, nationalIdFront, nationalIdBack);
            return ResponseEntity.created(URI.create("/v2/users/" + resp.getId())).body(resp);
        } catch (IOException e) {
            // หากมีปัญหาในการบันทึกไฟล์หรืออัปโหลด
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File upload failed", e);
        }
    }

}
