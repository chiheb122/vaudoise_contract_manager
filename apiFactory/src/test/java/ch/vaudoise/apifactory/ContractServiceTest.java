package ch.vaudoise.apifactory;

import ch.vaudoise.apifactory.dto.ClientCompanyResponse;
import ch.vaudoise.apifactory.dto.ClientCreateRequest;
import ch.vaudoise.apifactory.dto.ContractRequest;
import ch.vaudoise.apifactory.dto.ContractSumResponse;
import ch.vaudoise.apifactory.entities.Client;
import ch.vaudoise.apifactory.entities.TypeClient;
import ch.vaudoise.apifactory.services.ClientServices;
import ch.vaudoise.apifactory.services.ContractServices;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@SpringBootTest
@Transactional
public class ContractServiceTest {

    @Autowired
    private ContractServices contractServices;
    @Autowired
    private ClientServices clientServices;


    private Client client;

    @BeforeEach
    public void setup() {
        // Create a  company client for each test
        ClientCreateRequest companyReq = new ClientCreateRequest(
                TypeClient.COMPANY,
                "Vaudoise",
                "vd@company.ch",
                "+41234567890",
                null,
                "aaa-123");
        client = clientServices.createClient(companyReq);
    }


    // test addContractToClient
    @Test
    public void testAddContractToClient() {
        // Create a contract request
        ContractRequest contractReq = new ContractRequest(null,
                Date.from(LocalDate.of(2025, 12, 12)
                        .atStartOfDay(ZoneId.systemDefault()).toInstant()),
                new BigDecimal("1530.04")
        );
        // Add the contract to the client
        contractServices.addContractToClient(client.getEmail() ,contractReq);
        Assert.assertNotNull(client.getListOfContracts());
    }


    // test getAllContractsOfClientByEmail
    @Test
    public void testGetAllContractsOfClientByEmail() {
        // Create a contract request
        ContractRequest contractReq = new ContractRequest(null,
                Date.from(LocalDate.of(2025, 12, 12)
                        .atStartOfDay(ZoneId.systemDefault()).toInstant()),
                new BigDecimal("1530.04")
        );
        // Add the contract to the client
        contractServices.addContractToClient(client.getEmail(), contractReq);
        // Retrieve all valid contracts of the client
        List<ContractRequest> contracts = contractServices.getAllContractsOfClientByEmail(client.getEmail(), null);
        Assert.assertNotNull(contracts);
        Assert.assertEquals(1, contracts.size());
    }


    // test getTotalCostOfActiveContracts
    @Test
    public void testGetTotalCostOfActiveContracts() {
        // Create two contract requests one active and one inactive
        ContractRequest contractReq = new ContractRequest(null,
                Date.from(LocalDate.of(2025, 12, 12)
                        .atStartOfDay(ZoneId.systemDefault()).toInstant()),
                new BigDecimal("1530.04")
        );
        ContractRequest contractInactive = new ContractRequest(null,
                Date.from(LocalDate.of(2020, 12, 12)
                        .atStartOfDay(ZoneId.systemDefault()).toInstant()),
                new BigDecimal("100.00")
        );
        // Add the contract to the client
        contractServices.addContractToClient(client.getEmail(), contractReq);
        contractServices.addContractToClient(client.getEmail(), contractInactive);

        // check that the client has 2 contracts
        ClientCompanyResponse freshClient = (ClientCompanyResponse) clientServices.getClientByEmail(client.getEmail());
        Assert.assertEquals(2, freshClient.listOfContracts().size());
        // Retrieve the total cost of active contracts
        ContractSumResponse totalCost = contractServices.getTotalCostOfActiveContracts(client.getEmail());
        Assert.assertNotNull(totalCost);
        Assert.assertEquals(new BigDecimal("1530.04"), totalCost.totalActiveContractAmount());
        Assert.assertEquals(1, totalCost.totalContracts());
    }





}
