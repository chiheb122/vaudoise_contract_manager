package ch.vaudoise.apifactory;

import ch.vaudoise.apifactory.dto.ClientCompanyResponse;
import ch.vaudoise.apifactory.dto.ClientCreateRequest;
import ch.vaudoise.apifactory.dto.ClientModifyRequest;
import ch.vaudoise.apifactory.dto.ClientResponse;
import ch.vaudoise.apifactory.entities.Client;
import ch.vaudoise.apifactory.entities.Company;
import ch.vaudoise.apifactory.entities.Person;
import ch.vaudoise.apifactory.entities.TypeClient;
import ch.vaudoise.apifactory.exceptions.ClientNotFoundException;
import ch.vaudoise.apifactory.services.ClientServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@SpringBootTest
@Transactional
public class ClientServiceTest {

    private ClientCreateRequest personReq;
    private ClientCreateRequest companyReq;

    @Autowired
    ClientServices clientService;

    @BeforeEach
    public void setup() {
        //  Initialize test data for Person and Company clients
        LocalDate localDate = LocalDate.of(1990, 1, 1);
        Date birthdate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        personReq = new ClientCreateRequest(
                TypeClient.PERSON,
                "chiheb",
                "chiheb@gmail.com",
                "+41234567890",
                birthdate,
                null);

        companyReq = new ClientCreateRequest(
                TypeClient.COMPANY,
                "Vaudoise",
                "vd@company.ch",
                "+41234567890",
                null,
                "aaa-123");
    }

    // Test createClient for Person
    @Test
    public void testCreateClientPerson() {
        Client client = clientService.createClient(personReq);
        // Validate the created client
        Assert.assertNotNull(client.getId());
        Assert.assertEquals("chiheb", client.getName());
        Assert.assertEquals("chiheb@gmail.com", client.getEmail());
        Assert.assertTrue(client instanceof Person);
    }

    // Test createClient for Company
    @Test
    public void testCreateClientCompany() {
        Client client =  clientService.createClient(companyReq);
        Assert.assertNotNull(client.getId());
        Assert.assertEquals("Vaudoise", client.getName());
        Assert.assertEquals("vd@company.ch", client.getEmail());
        Assert.assertTrue(client instanceof Company);
    }

    // Test getClientByID
    @Test
    public void testGetClientByID() {
        Client createdClient = clientService.createClient(companyReq);
        // Retrieve the ID of the created client
        int id = createdClient.getId().intValue();
        Assert.assertNotNull(id);
        ClientCompanyResponse response = (ClientCompanyResponse) clientService.getClientByID(id);
        Assert.assertEquals("Vaudoise", response.name());
        Assert.assertEquals("aaa-123",response.companyIdentifier());
    }


    // Test getClientByEmail
    @Test
    public void testGetClientByEmail() {
        Client createdClient = clientService.createClient(companyReq);
        String email = createdClient.getEmail();
        Assert.assertNotNull(email);
        // Retrieve the client by email
        ClientCompanyResponse response = (ClientCompanyResponse) clientService.getClientByEmail(email);
        Assert.assertEquals(email, response.email());
        Assert.assertEquals("Vaudoise", response.name());
    }

    // Test updateClient
    @Test
    public void testUpdateClient() {
        Company createdClient = (Company) clientService.createClient(companyReq);
        ClientModifyRequest updateReq = new ClientModifyRequest(
                TypeClient.COMPANY,
                "vaudoise updated",
                "vd2@gmail.com",
                "+41234567891"
        );
        clientService.updateClient(createdClient.getEmail(), updateReq);
        ClientResponse response = clientService.getClientByEmail("vd2@gmail.com");
        Assert.assertNotNull(response);
    }


    // Test deleteClient
    @Test
    public void testDeleteClient() {
        Company createdClient = (Company) clientService.createClient(companyReq);
        String email = createdClient.getEmail();
        clientService.deleteClientByEmail(email);
        Assert.assertThrows(ClientNotFoundException.class, () -> {
            clientService.getClientByEmail(email);
        });
    }

}
