package sit.int202.ecommerce.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MyErrorResponse {

    private final Instant timestamp = Instant.now();
    private final int status;
    private final String errorMessage;
    private final String path;
    private final List<ValidationError> errors = new ArrayList<>();

    @Getter
    @RequiredArgsConstructor
    private static class ValidationError {
        private final String field;
        private final String message;
    }

    public void addValidationError(String field, String message) {
        errors.add(new ValidationError(field, message));
    }

    public List<ValidationError> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}