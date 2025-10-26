package sit.int202.ecommerce.modules.auth.validation;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class PasswordValidator {

    private static final String PASSWORD_REGEX =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_.])[A-Za-z\\d@$!%*?&_.]{8,}$";

    /**
     * Validate the given password against the defined complexity rules.
     *
     * @param password
     * @throws ResponseStatusException if the password does not meet the complexity requirements.
     */
    public boolean isInvalid(String password) {
        return password != null && !password.isBlank() && password.length() >= 8 &&  password.length() <= 14 && password.matches(PASSWORD_REGEX);
    }
}
