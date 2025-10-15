package ch.vaudoise.apifactory.services;

import ch.vaudoise.apifactory.dto.ContractRequest;
import ch.vaudoise.apifactory.dto.ContractSumResponse;
import ch.vaudoise.apifactory.entities.Client;
import ch.vaudoise.apifactory.entities.Contract;
import ch.vaudoise.apifactory.exceptions.ClientNotFoundException;
import ch.vaudoise.apifactory.repository.ClientRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ContractServices {

    @PersistenceContext
    EntityManager entityManager;


    private final ClientRepository clientRepository;

    public ContractServices(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
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
        for (Contract contract : getActiveContracts(client)) {
            // Check if the contract is valid (end date > current date)
            // and if it was updated after the specified date (if provided)
            boolean isValidContract = contract.getEndDate().after(new Date());
            boolean isUpdatedAfter = updatedAfter == null || contract.getLastModifiedDate().after(updatedAfter);
            // If both conditions are met, add the contract to the result list
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

    /**
     * Calculate the total cost of all active contracts for a client identified by her email.*
     * @param email The email of the client whose active contracts' total cost is to be calculated
     * @return A ContractSumResponse containing the client's ID, email, number of active contracts, and total cost
     */
    public ContractSumResponse getTotalCostOfActiveContracts(String email) {
        // 1. Find the client by email
        Client client = clientRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ClientNotFoundException(
                        "Client with email " + email + " not found."
                ));
        // 2. Get active contracts
        List<Contract> activeContracts = getActiveContracts(client);
        // 3. Sum the cost amounts of active contracts
        BigDecimal totalCost = BigDecimal.ZERO;
        for (Contract contract : activeContracts) {
            if (contract.getCostAmount() != null) {
                totalCost = totalCost.add(contract.getCostAmount());
            }
        }
        return new ContractSumResponse(
                client.getId(),
                client.getEmail(),
                activeContracts.size(),
                totalCost
        );

    }


    // private method to get active contracts of a client (end date > current date)
    // using JPQL query for better performance
    private List<Contract> getActiveContracts(Client client) {
        String jpql = "SELECT c FROM Contract c WHERE c.client = :client AND c.endDate > :currentDate";
        return entityManager.createQuery(jpql, Contract.class)
                .setParameter("client", client)
                .setParameter("currentDate", new Date())
                .getResultList();
    }





}
