package sit.int202.ecommerce.common.exceptions.handler;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.server.ResponseStatusException;
import sit.int202.ecommerce.common.dto.ErrorResponse;
import sit.int202.ecommerce.common.exceptions.BrandHasSaleItemsException;
import sit.int202.ecommerce.common.exceptions.FileUploadException;


@RestControllerAdvice
public class GlobalExceptionHandler {

    // ✅ Handler สำหรับ ResponseStatusException (เช่น 400, 401, 403, 409)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(
            ResponseStatusException ex, HttpServletRequest request) {

        ErrorResponse error = ErrorResponse.builder()
                .status(ex.getStatusCode().value())
                .errorMessage(ex.getReason())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(ex.getStatusCode()).body(error);
    }

    // ✅ Handler สำหรับ EntityNotFoundException (เช่น 404)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(
            EntityNotFoundException ex, HttpServletRequest request) {

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .errorMessage(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler({
            EntityExistsException.class,
            BrandHasSaleItemsException.class
    })
    public ResponseEntity<ErrorResponse> entityExistsExceptions(
            RuntimeException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .errorMessage(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handlePresentExceptions(ValidationException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .errorMessage(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .status(ex.getStatusCode().value())
                .errorMessage("Validation errors")
                .path(request.getRequestURI())
                .build();

        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            error.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleControllerValidation(
            HandlerMethodValidationException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorMessage(ex.getAllErrors().get(0).getDefaultMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ErrorResponse> handleFileUploadException(
            FileUploadException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .errorMessage(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .errorMessage(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(error.getStatus()).body(error);
    }
}
