package ch.vaudoise.apifactory.controller;

import ch.vaudoise.apifactory.dto.*;
import ch.vaudoise.apifactory.exceptions.ClientNotFoundException;
import ch.vaudoise.apifactory.services.ClientServices;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController()
@RequestMapping("/api/v1/clients")
public class ClientController {


    private final ClientServices clientServices;

    public ClientController(ClientServices clientServices) {
        this.clientServices = clientServices;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addClient(@Valid @RequestBody ClientCreateRequest client){
        log.info("Add Client {}", client.name());
        try {
            clientServices.createClient(client);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Client successfully added: " + client.name());
        }catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input: " + e.getMessage());}
        catch (Exception e) {
            log.error("Unexpected error while adding client", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred. Please try again later.");
        }
   }

   @GetMapping("/id/{id}")
   public ClientResponse getClient(@PathVariable int id){
            return clientServices.getClientByID(id);
   }

    @GetMapping
    public ClientResponse getClientByEmail(@RequestParam("email") String email) throws ClientNotFoundException {
            return clientServices.getClientByEmail(email);

    }

    @PutMapping("/update/{email}")
    public ResponseEntity<String>  modifyClient(@PathVariable String email,@RequestBody ClientModifyRequest client) {
        log.info("Update Client {}", email);
        try {
            clientServices.updateClient(email, client);
            return ResponseEntity.ok("Client updated successfully.");
        } catch (ClientNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }catch (Exception e) {
            log.error("Unexpected error while updating client", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred. Please try again later.");
        }
    }


    /** Delete a client by email
     * @param email The email of the client to delete
     * @return A ResponseEntity with a success message or an error message
     */
    @DeleteMapping("/delete/{email}")
    public ResponseEntity<String> deleteClient(@PathVariable String email) {
        try {
            clientServices.deleteClientByEmail(email);
            return ResponseEntity.ok("Client deleted successfully.");
        } catch (ClientNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while deleting client", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred. Please try again later.");
        }
    }


    /** Method to add a contract to a client
     *
     */
    @PostMapping("/contracts/add")
    public ResponseEntity<String> addContractToClient(@RequestParam String email, @RequestBody ContractRequest contract){
        try {
            clientServices.addContractToClient(email, contract);
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
    @GetMapping("/contracts")
    public ResponseEntity<List<ContractRequest>> getContractsByEmail(
            @RequestParam String email,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date updatedAfter) {
        try {
            List<ContractRequest> contracts = clientServices.getAllContractsOfClientByEmail(email, updatedAfter);
            return ResponseEntity.ok(contracts);
        }catch (ClientNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Unexpected error while retrieving contracts for client with email: " + email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }











    /**
     * Source : https://stackoverflow.com/questions/66371164/spring-boot-exceptionhandler-for-methodargumentnotvalidexception-in-restcontroll
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String,String> handleValidationException(MethodArgumentNotValidException ex){
        Map<String,String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error)->{
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName,errorMessage);
        });
        return errors;
    }



}
