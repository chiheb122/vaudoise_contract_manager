package ch.vaudoise.apifactory.dto.ApiJsonResponse;

import java.util.List;
import java.util.Map;

public record ApiErrorResponse(
        String timestamp, // ISO 8601 format
        int status, // HTTP status code (e.g., 400, 404, 500)
        String error, // Short description of the error (e.g., "Bad Request", "Not Found", "Internal Server Error")
        String message, // Detailed error message
        String path, // The request path that caused the error
        List<Map<String, String>> details // List of field errors
) {
}
