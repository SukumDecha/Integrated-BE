package sit.int202.ecommerce.common.utils;

import org.springframework.stereotype.Component;

@Component
public class EmailValidator {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    /**
     * Validate the given email address.
     * @param email
     * @return true if the email is valid, false otherwise.
     */
    public boolean isInvalid(String email) {
        return email != null && !email.isBlank() && email.length() <= 50 && email.matches(EMAIL_REGEX);
    }

}
