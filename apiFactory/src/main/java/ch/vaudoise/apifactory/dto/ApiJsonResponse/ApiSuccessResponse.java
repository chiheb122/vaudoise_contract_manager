package ch.vaudoise.apifactory.dto.ApiJsonResponse;

public record ApiSuccessResponse(
        int status, // HTTP status code (e.g., 200, 201)
        String message // Success message
) {
}
