package ch.vaudoise.apifactory.controller;

import ch.vaudoise.apifactory.dto.*;
import ch.vaudoise.apifactory.dto.ApiJsonResponse.ApiSuccessResponse;
import ch.vaudoise.apifactory.exceptions.ClientNotFoundException;
import ch.vaudoise.apifactory.services.ClientServices;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController()
@RequestMapping("/api/v1/clients")
public class ClientController {


    private final ClientServices clientServices;

    public ClientController(ClientServices clientServices) {
        this.clientServices = clientServices;
    }

    @PostMapping("/add")
    public ResponseEntity<ApiSuccessResponse> addClient(@Valid @RequestBody ClientCreateRequest client){
        log.info("Add Client {}", client.name());
        clientServices.createClient(client);
        ApiSuccessResponse responseBody = new ApiSuccessResponse(
                HttpStatus.CREATED.value(),
                "Client successfully added: " + client.name()
        );
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(responseBody);

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
    public ResponseEntity<ApiSuccessResponse>  modifyClient(@PathVariable String email,@RequestBody ClientModifyRequest client) {
        log.info("Update Client {}", email);
        clientServices.updateClient(email, client);
        ApiSuccessResponse responseBody = new ApiSuccessResponse(
                HttpStatus.OK.value(),
                "Client successfully updated: " + email
        );
        return ResponseEntity.ok().body(responseBody);
    }


    /** Delete a client by email
     * @param email The email of the client to delete
     * @return A ResponseEntity with a success message or an error message
     */
    @DeleteMapping("/delete/{email}")
    public ResponseEntity<ApiSuccessResponse> deleteClient(@PathVariable String email) {
        log.info("Delete Client {}", email);
        clientServices.deleteClientByEmail(email);
        ApiSuccessResponse responseBody = new ApiSuccessResponse(
                HttpStatus.OK.value(),
                "Client successfully deleted: " + email
        );
        return ResponseEntity.ok().body(responseBody);

    }













//    /**
//     * Source : https://stackoverflow.com/questions/66371164/spring-boot-exceptionhandler-for-methodargumentnotvalidexception-in-restcontroll
//     */
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public Map<String,String> handleValidationException(MethodArgumentNotValidException ex){
//        Map<String,String> errors = new HashMap<>();
//
//        ex.getBindingResult().getAllErrors().forEach((error)->{
//            String fieldName = ((FieldError) error).getField();
//            String errorMessage = error.getDefaultMessage();
//            errors.put(fieldName,errorMessage);
//        });
//        return errors;
//    }



}
