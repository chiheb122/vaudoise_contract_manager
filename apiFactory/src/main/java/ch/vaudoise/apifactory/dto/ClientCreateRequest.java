package ch.vaudoise.apifactory.dto;

import ch.vaudoise.apifactory.entities.TypeClient;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.util.Date;

public record ClientCreateRequest(
        @NotNull(message = "Client type is required.")
        TypeClient typeClient,              // PERSON | COMPANY
        @NotBlank(message = "Name cannot be empty.")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters.")
        String name,
        @NotBlank(message = "Email is required.")
        @Email(message = "Email format is invalid.")
        @Pattern(
                regexp = "^[A-Za-z0-9._%+-]+@(?:(?!mailinator\\.com|temp-mail\\.org|guerrillamail\\.com|10minutemail\\.com)[A-Za-z0-9.-]+)\\.[A-Za-z]{2,6}$",
                message = "Email format is invalid or uses a temporary email domain."
        )
        String email,

        // Swiss Phone Number Validation (with +41 prefix)
        @NotBlank(message = "Phone number is required.")
        @Pattern(
                // This Regex supports various Swiss formats (e.g., +41 79 123 45 67, +41791234567, 079 123 45 67)
                regexp = "(\\b(0041|0)|\\B\\+41)(\\s?\\(0\\))?(\\s)?[1-9]{2}(\\s)?[0-9]{3}(\\s)?[0-9]{2}(\\s)?[0-9]{2}\\b",
                message = "Invalid Swiss phone number format. Must include +41 or 0 prefix."
        )
        String phone,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        Date birthdate,                     // For PERSON
        String companyIdentifier            // For COMPANY
                                  ){}
