package ch.vaudoise.apifactory.handler;

import ch.vaudoise.apifactory.dto.ApiJsonResponse.ApiErrorResponse;
import ch.vaudoise.apifactory.exceptions.ClientNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler to manage and format error responses consistently across the application.
 */

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handle validation errors for @Valid annotated request bodies.
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        List<Map<String, String>> validationDetails = null;
        if (ex instanceof MethodArgumentNotValidException validationEx) {
            validationDetails = validationEx.getBindingResult().getAllErrors().stream()
                    .filter(error -> error instanceof FieldError)
                    .map(error -> {
                        Map<String, String> errorDetail = new HashMap<>();
                        errorDetail.put("field", ((FieldError) error).getField());
                        errorDetail.put("message", error.getDefaultMessage());
                        return errorDetail;
                    })
                    .collect(Collectors.toList());
        }

        ApiErrorResponse apiError = new ApiErrorResponse(
                LocalDateTime.now().toString(),
                status.value(),
                HttpStatus.valueOf(status.value()).getReasonPhrase(), // "Bad Request", "Not Found"
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""),
                validationDetails
        );

        return new ResponseEntity<>(apiError, headers, status);
    }



    /**
     * Handle validation errors for @Valid annotated request bodies.
     */
    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleClientNotFound(
            ClientNotFoundException ex, WebRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND;
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                LocalDateTime.now().toString(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""),
                null
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Handle IllegalArgumentException, typically thrown when a client with a duplicate email or phone is being created.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, WebRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST; // Statut 400

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                LocalDateTime.now().toString(),
                status.value(),
                status.getReasonPhrase(), // "Bad Request"
                ex.getMessage(), // ex : "A client with email... already exists."
                request.getDescription(false).replace("uri=", ""),
                null
        );
        return new ResponseEntity<>(errorResponse, status);
    }
}