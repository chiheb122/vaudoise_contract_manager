package ch.vaudoise.apifactory.controller;

import ch.vaudoise.apifactory.dto.ClientCreateRequest;
import ch.vaudoise.apifactory.dto.ClientCompanyResponse;
import ch.vaudoise.apifactory.dto.ClientModifyRequest;
import ch.vaudoise.apifactory.dto.ClientResponse;
import ch.vaudoise.apifactory.entities.Client;
import ch.vaudoise.apifactory.exceptions.ClientNotFoundException;
import ch.vaudoise.apifactory.services.ClientServices;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
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
        try {
            clientServices.updateClient(email, client);
            return ResponseEntity.ok("Client updated successfully.");
        } catch (ClientNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
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
