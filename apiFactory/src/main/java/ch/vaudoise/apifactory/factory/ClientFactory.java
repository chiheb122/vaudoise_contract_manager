package ch.vaudoise.apifactory.factory;

import ch.vaudoise.apifactory.dto.ClientCreateRequest;
import ch.vaudoise.apifactory.entities.Client;
import ch.vaudoise.apifactory.entities.Company;
import ch.vaudoise.apifactory.entities.Person;



public class ClientFactory {
    /**
     * Instantiates the correct subclass (Person or Company)
     * based on the client's typeClient attribute.
     */
    public static Client instantiate(ClientCreateRequest client) {
        if (client.typeClient() == null) {
            throw new IllegalArgumentException("Client type cannot be null");
        }

        switch (client.typeClient()) {
            case PERSON -> {
                Person person = new Person();
                person.setName(client.name());
                person.setEmail(client.email());
                person.setPhone(client.phone());
                person.setBirthdate(client.birthdate());
                person.setTypeClient(client.typeClient());
                return person;
            }

            case COMPANY -> {
                Company company = new Company();
                company.setName(client.name());
                company.setEmail(client.email());
                company.setPhone(client.phone());
                company.setCompanyIdentifier(client.companyIdentifier());
                company.setTypeClient(client.typeClient());
                return company;
            }
            default -> throw new IllegalArgumentException("Unknown client type: " + client.typeClient());

        }
    }
}
