package ch.vaudoise.apifactory.services;

import ch.vaudoise.apifactory.dto.*;
import ch.vaudoise.apifactory.entities.Client;
import ch.vaudoise.apifactory.entities.Company;
import ch.vaudoise.apifactory.entities.Contract;
import ch.vaudoise.apifactory.entities.Person;
import ch.vaudoise.apifactory.exceptions.ClientNotFoundException;
import ch.vaudoise.apifactory.factory.ClientFactory;
import ch.vaudoise.apifactory.repository.ClientRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
     * @param clientEntity
     * @return ClientResponse
     */
    private ClientResponse mapClient(Client clientEntity){
        if (clientEntity instanceof Person p) {
            return new ClientPersonResponse(
                    p.getName(),
                    p.getEmail(),
                    p.getTypeClient().toString(),
                    p.getBirthdate()
            );
        } else if (clientEntity instanceof Company c) {
            return new ClientCompanyResponse(
                    c.getName(),
                    c.getEmail(),
                    c.getTypeClient().toString(),
                    c.getCompanyIdentifier()
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

    /**
     * Method to add a contract to a client
     * @param email The email of the client to whom the contract will be added
     * @param contract The contract details to add
     */
    @Transactional
    public void addContractToClient(String email, ContractRequest contract){
        // 1. Find the client by email
        Client client = clientRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ClientNotFoundException(
                        "Client with email " + email + " not found."
                ));
        // 2. Create a new contract from the request
        Contract newContract = new Contract();
        newContract.setStartDate(contract.startDate());
        newContract.setEndDate(contract.endDate());
        newContract.setCostAmount(contract.costAmount());
        newContract.setClient(client);
        // add the contract to the client
        client.getListOfContracts().add(newContract);
        // save the client with the new contract
        clientRepository.save(client);
    }

    /**
     * Retrieve all valid contracts of a client identified by her email.
     * A contract is considered valid if its end date is after the current date.
     * Optionally, filter contracts that were updated after a specified date.
     *
     * @param email The email of the client whose contracts are to be retrieved
     * @param updatedAfter (Optional) If provided, only contracts updated after this date will be included
     * @return A list of ContractRequest representing the client's valid contracts
     */
    public List<ContractRequest> getAllContractsOfClientByEmail(String email, @Nullable Date updatedAfter) {
        // 1. Find the client by email
        Client client = clientRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ClientNotFoundException(
                        "Client with email " + email + " not found."
                ));
        // 2. get all contracts and filter by end date > current date
        List<ContractRequest> validContract = new ArrayList<>();
        for (Contract contract : client.getListOfContracts()) {

            // Check if the contract is valid (end date > current date)
            // and if it was updated after the specified date (if provided)
            boolean isValidContract = contract.getEndDate().after(new Date());
            boolean isUpdatedAfter = updatedAfter == null || contract.getLastModifiedDate().after(updatedAfter);

            if (isValidContract && isUpdatedAfter) {
                // Map the contract entity to a ContractRequest DTO
                ContractRequest cr = new ContractRequest(
                        contract.getStartDate(),
                        contract.getEndDate(),
                        contract.getCostAmount()
                );
                // add to the list of valid contracts
                validContract.add(cr);

            }
        }

        return validContract;
    }



}
