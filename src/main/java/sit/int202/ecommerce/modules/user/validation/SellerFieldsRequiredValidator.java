package sit.int202.ecommerce.modules.user.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import sit.int202.ecommerce.modules.user.dto.request.UserRegisterRequest;
import sit.int202.ecommerce.modules.user.model.UserAccountType;

public class SellerFieldsRequiredValidator implements ConstraintValidator<SellerFieldsRequired, UserRegisterRequest> {

    @Override
    public boolean isValid(UserRegisterRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true;
        }

        if (request.getUserType() == UserAccountType.SELLER) {
            boolean valid = true;

            context.disableDefaultConstraintViolation();
            if (isBlank(request.getMobileNumber())) {
                addViolation(context, "mobileNumber");
                valid = false;
            }
            if (isBlank(request.getBankAccountNumber())) {
                addViolation(context, "bankAccountNumber");
                valid = false;
            }
            if (isBlank(request.getBankName())) {
                addViolation(context, "bankName");
                valid = false;
            }
            if (isBlank(request.getIdCardNumber())) {
                addViolation(context, "idCardNumber");
                valid = false;
            }

            return valid;
        }

        return true;
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    private void addViolation(ConstraintValidatorContext context, String field) {
        context.buildConstraintViolationWithTemplate(field + " is required for SELLER")
                .addPropertyNode(field)
                .addConstraintViolation();
    }
}
