package ch.vaudoise.apifactory.dto;

import java.util.Date;

public record ClientPersonResponse(String name,
                                   String email,
                                   String type,
                                   Date birthdate                     // For PERSON
) implements ClientResponse {
}
