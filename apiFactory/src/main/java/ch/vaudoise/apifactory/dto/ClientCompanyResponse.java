package ch.vaudoise.apifactory.dto;

import ch.vaudoise.apifactory.entities.Contract;

import java.util.Date;
import java.util.List;

public record ClientCompanyResponse (
        Long id,
        String name,
        String email,
        String type,
        String companyIdentifier,            // For COMPANY
        List<ContractRequest> listOfContracts

) implements ClientResponse {
}
