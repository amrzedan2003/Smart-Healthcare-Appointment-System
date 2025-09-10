package ps.exalt.healthcare_appointment_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(NotFoundException.class)
        public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex, WebRequest request) {
                return buildErrorResponse(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage(), request);
        }

        @ExceptionHandler(DuplicateException.class)
        public ResponseEntity<ErrorResponse> handleDuplicateException(DuplicateException ex, WebRequest request) {
                return buildErrorResponse(HttpStatus.CONFLICT, "Duplicate Resource", ex.getMessage(), request);
        }

        @ExceptionHandler(InvalidException.class)
        public ResponseEntity<ErrorResponse> handleInvalidException(InvalidException ex, WebRequest request) {
                return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Authentication Failed", ex.getMessage(), request);
        }

        @ExceptionHandler({ IllegalStateException.class })
        public ResponseEntity<ErrorResponse> handleBadRequestExceptions(RuntimeException ex, WebRequest request) {
                return buildErrorResponse(HttpStatus.BAD_REQUEST, "Illegal State", ex.getMessage(), request);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex,
                        WebRequest request) {

                List<String> details = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(FieldError::getDefaultMessage)
                                .collect(Collectors.toList());

                ErrorResponse errorResponse = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.BAD_REQUEST.value(),
                                "Validation Failed",
                                "Invalid input data",
                                details);

                return ResponseEntity.badRequest().body(errorResponse);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
                return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                                "An unexpected error occurred" + ex, request);
        }

        private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String error, String message,
                        WebRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(
                                LocalDateTime.now(),
                                status.value(),
                                error,
                                message,
                                null);
                return ResponseEntity.status(status).body(errorResponse);
        }
}
