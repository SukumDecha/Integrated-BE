package sit.int202.ecommerce.modules.user.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SellerFieldsRequiredValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface SellerFieldsRequired {
    String message() default "Seller fields are required for SELLER account type";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

