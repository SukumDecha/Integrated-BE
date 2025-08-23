package sit.int202.ecommerce.modules.user.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordPolicyValidator.class)
public @interface PasswordPolicy {
    String message() default "Password must be >= 8 and include lower, upper, number, and special char";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
