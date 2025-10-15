package ch.vaudoise.apifactory.dto;

import ch.vaudoise.apifactory.entities.Contract;

import java.util.Date;
import java.util.List;

public record ClientPersonResponse(
                                   Long id,
                                   String name,
                                   String email,
                                   String type,
                                   Date birthdate,                     // For PERSON
                                   List<ContractRequest> listOfContracts
) implements ClientResponse {
}
