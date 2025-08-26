package sit.int202.ecommerce.modules.user.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PasswordPolicyValidator implements ConstraintValidator<PasswordPolicy, String> {
    private static final Pattern P = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$"
    );
    @Override public boolean isValid(String v, ConstraintValidatorContext c) {
        return v != null && P.matcher(v).matches();
    }
}
