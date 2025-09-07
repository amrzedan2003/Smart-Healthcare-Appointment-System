package ps.exalt.healthcare_appointment_system.exception;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class GlobalExceptionHandler {

        @ExceptionHandler(NotFoundException.class)
        public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex, WebRequest request) {
                log.error("Resource not found: {}", ex.getMessage());
                return buildErrorResponse(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage(), request);
        }

        @ExceptionHandler(DuplicateException.class)
        public ResponseEntity<ErrorResponse> handleDuplicateException(DuplicateException ex, WebRequest request) {
                log.error("Duplicate resource: {}", ex.getMessage());
                return buildErrorResponse(HttpStatus.CONFLICT, "Duplicate Resource", ex.getMessage(), request);
        }

        @ExceptionHandler({ InvalidException.class, IllegalStateException.class })
        public ResponseEntity<ErrorResponse> handleBadRequestExceptions(RuntimeException ex, WebRequest request) {
                log.error("Bad request: {}", ex.getMessage());
                String error = ex instanceof InvalidException ? "Invalid Operation" : "Illegal State";
                return buildErrorResponse(HttpStatus.BAD_REQUEST, error, ex.getMessage(), request);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex,
                        WebRequest request) {
                log.error("Validation error: {}", ex.getMessage());

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
                                request.getDescription(false).replace("uri=", ""),
                                details);

                return ResponseEntity.badRequest().body(errorResponse);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
                log.error("Unexpected error: {}", ex.getMessage(), ex);
                return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                                "An unexpected error occurred", request);
        }

        private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String error, String message,
                        WebRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(
                                LocalDateTime.now(),
                                status.value(),
                                error,
                                message,
                                request.getDescription(false).replace("uri=", ""));
                return ResponseEntity.status(status).body(errorResponse);
        }
}
