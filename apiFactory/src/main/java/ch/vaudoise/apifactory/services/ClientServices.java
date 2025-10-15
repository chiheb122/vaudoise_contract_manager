package ch.vaudoise.apifactory.services;

import ch.vaudoise.apifactory.dto.*;
import ch.vaudoise.apifactory.entities.Client;
import ch.vaudoise.apifactory.entities.Company;
import ch.vaudoise.apifactory.entities.Contract;
import ch.vaudoise.apifactory.entities.Person;
import ch.vaudoise.apifactory.exceptions.ClientNotFoundException;
import ch.vaudoise.apifactory.factory.ClientFactory;
import ch.vaudoise.apifactory.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;


@Service
public class ClientServices {


    private final ClientRepository clientRepository;

    public ClientServices(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }



    /**
     * Create a new client (Person or Company) based on the provided request data.
     */
    @Transactional
    public Client createClient(ClientCreateRequest req) {
        Client instance = ClientFactory.instantiate(req);
        // Check if a client with the same unique email already exists
        clientRepository.findByEmailIgnoreCase(req.email())
                    .ifPresent(c -> {
                        throw new IllegalArgumentException("A client with email " + req.email() + " already exists.");
                    });
        // check if the client is a company and if the companyIdentifier is unique
        if (instance instanceof Company company) {
            clientRepository.findByCompanyIdentifierIgnoreCase(company.getCompanyIdentifier())
                    .ifPresent(c -> {
                        throw new IllegalArgumentException("A company with identifier " + company.getCompanyIdentifier() + " already exists.");
                    });
        }

        // check if the phone number is unique
        if (req.phone() != null && !req.phone().isEmpty()) {
            clientRepository.findByPhoneIgnoreCase(req.phone())
                    .ifPresent(c -> {
                        throw new IllegalArgumentException("A client with phone number " + req.phone() + " already exists.");
                    });
        }
        // Save the new client to the repository
        return clientRepository.save(instance);
    }


    /**
     * Get a client by her ID
     * @param id The ID of the client to retrieve
     * @return ClientResponse dto
     */
    public ClientResponse getClientByID(int id) {
        Client clientEntity = clientRepository.findById((long) id)
                .orElseThrow(() -> new ClientNotFoundException("Client with ID " + id + " not found."));
        return mapClient(clientEntity);
    }



    /**
     * Map a Client entity to the appropriate ClientResponse subclass based on its type.
     * @param clientEntity The client entity to map
     * @return ClientResponse
     */
    private ClientResponse mapClient(Client clientEntity){
        // Helper function to convert Contract to ContractRequest
        Function<Contract, ContractRequest> toContractResponse = contract -> new ContractRequest(
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getCostAmount()
        );

        if (clientEntity instanceof Person p) {
            return new ClientPersonResponse(
                    p.getId(),
                    p.getName(),
                    p.getEmail(),
                    p.getTypeClient().toString(),
                    p.getBirthdate(),
                    p.getListOfContracts().stream().map(toContractResponse).toList()
            );
        } else if (clientEntity instanceof Company c) {
            return new ClientCompanyResponse(
                    c.getId(),
                    c.getName(),
                    c.getEmail(),
                    c.getTypeClient().toString(),
                    c.getCompanyIdentifier(),
                    c.getListOfContracts().stream().map(toContractResponse).toList()
            );
        } else {
            throw new IllegalStateException("Unexpected client type: " + clientEntity.getTypeClient());
        }
    }


    /**
     * Get a client by her email
     * @param email The email of the client to retrieve
     * @return ClientResponse
     */
    public ClientResponse getClientByEmail(String email) {
        Client clientEntity = clientRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ClientNotFoundException(
                        "Client with email " + email + " not found."
                ));
        return mapClient(clientEntity);
    }

    /**
     * Update a client's details based on the provided unique email and request data.
     * @param email The unique email of the client to update
     * @param request The request containing updated client details
     */
    @Transactional
    public void updateClient(String email, ClientModifyRequest request){
       Client client = clientRepository.findByEmailIgnoreCase(email)
               .orElseThrow(() -> new ClientNotFoundException(
                       "Client with email " + email + " not found."
               ));
       // update the client details without changing birthdate or companyIdentifier
        client.setName(request.name());
        client.setEmail(request.email());
        client.setPhone(request.phone());
        // save the updated client
        clientRepository.save(client);
    }

    /**
     * Delete a client by her email
     * @param email The email of the client to delete
     */
    @Transactional
    public void deleteClientByEmail(String email){
        Client client = clientRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ClientNotFoundException(
                        "Client with email " + email + " not found."
                ));
        clientRepository.delete(client);
    }







}
