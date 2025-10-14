package ch.vaudoise.apifactory.dto;

import java.util.Date;

public record ClientCompanyResponse (
        Long id,
        String name,
        String email,
        String type,
        String companyIdentifier            // For COMPANY

) implements ClientResponse {
}
