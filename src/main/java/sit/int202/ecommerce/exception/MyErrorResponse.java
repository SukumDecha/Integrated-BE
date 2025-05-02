package sit.int202.ecommerce.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@RequiredArgsConstructor
public class MyErrorResponse {
    private final Instant timestamp = Instant.now();  // current time
    private final int status;                         // 404
    private final String error;                       // "Not Found"
    private final String message;                     // custom message
    private final String path;                        // URI path
}




