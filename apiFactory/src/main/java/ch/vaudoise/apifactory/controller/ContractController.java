package ch.vaudoise.apifactory.controller;

import ch.vaudoise.apifactory.dto.ContractRequest;
import ch.vaudoise.apifactory.dto.ContractSumResponse;
import ch.vaudoise.apifactory.exceptions.ClientNotFoundException;
import ch.vaudoise.apifactory.services.ContractServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/contracts")
public class ContractController {

    public final ContractServices contractService;

    public ContractController(ContractServices contractService) {
        this.contractService = contractService;
    }


    /**
     * Add a new contract to a client identified by her email.
     * @param email The email of the client to whom the contract will be added
     * @param contract The contract details to add
     * @return ResponseEntity with appropriate status and message
     */
    @PostMapping("/client/add")
    public ResponseEntity<String> addContractToClient(@RequestParam String email, @RequestBody ContractRequest contract){
        try {
            contractService.addContractToClient(email, contract);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Contract successfully added to client with email: " + email);
        } catch (ClientNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while adding contract to client", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred. Please try again later.");
        }

    }


    /**
     * Retrieve all contracts of a client by her email.
     * If updatedAfter is provided, only contracts updated after that date are returned.
     * @param email The email of the client
     * @param updatedAfter (Optional) Date to filter contracts updated after this date
     * @return A list of ContractRequest dto objects representing the client's contracts
     */
    @GetMapping("/client")
    public ResponseEntity<List<ContractRequest>> getContractsByEmail(
            @RequestParam String email,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date updatedAfter) {
        try {
            List<ContractRequest> contracts = contractService.getAllContractsOfClientByEmail(email, updatedAfter);
            return ResponseEntity.ok(contracts);
        }catch (ClientNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Unexpected error while retrieving contracts for client with email: " + email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GetMapping("/client/sum")
    public ResponseEntity<ContractSumResponse> getSumOfContracts(@RequestParam String email)
    {
        try {
            return ResponseEntity.ok(contractService.getTotalCostOfActiveContracts(email));
        }catch (ClientNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Unexpected error while calculating sum of contracts for client with email: " + email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
