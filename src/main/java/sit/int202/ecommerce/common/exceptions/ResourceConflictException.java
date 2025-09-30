package sit.int202.ecommerce.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ResourceConflictException extends ResponseStatusException {
    public ResourceConflictException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
