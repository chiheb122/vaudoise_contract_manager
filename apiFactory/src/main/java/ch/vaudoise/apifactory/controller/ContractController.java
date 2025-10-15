package ch.vaudoise.apifactory.controller;

import ch.vaudoise.apifactory.dto.ContractRequest;
import ch.vaudoise.apifactory.dto.ContractSumResponse;
import ch.vaudoise.apifactory.dto.ApiJsonResponse.ApiSuccessResponse;
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
    public ResponseEntity<ApiSuccessResponse> addContractToClient(@RequestParam String email, @RequestBody ContractRequest contract){
        log.info("Add Contract to Client {}", email);
            contractService.addContractToClient(email, contract);
        ApiSuccessResponse responseBody = new ApiSuccessResponse(
                HttpStatus.CREATED.value(),
                "Contract successfully added to client with email: " + email

        );
        // Return json response Created status 201
            return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
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

            List<ContractRequest> contracts = contractService.getAllContractsOfClientByEmail(email, updatedAfter);
            return ResponseEntity.ok(contracts);
    }

    @GetMapping("/client/sum")
    public ResponseEntity<ContractSumResponse> getSumOfContracts(@RequestParam String email)
    {
            return ResponseEntity.ok(contractService.getTotalCostOfActiveContracts(email));
    }

}
