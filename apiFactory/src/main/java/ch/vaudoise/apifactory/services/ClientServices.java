package ch.vaudoise.apifactory.services;

import ch.vaudoise.apifactory.dto.*;
import ch.vaudoise.apifactory.entities.Client;
import ch.vaudoise.apifactory.entities.Company;
import ch.vaudoise.apifactory.entities.Person;
import ch.vaudoise.apifactory.exceptions.ClientNotFoundException;
import ch.vaudoise.apifactory.factory.ClientFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static ch.vaudoise.apifactory.entities.TypeClient.COMPANY;
import static ch.vaudoise.apifactory.entities.TypeClient.PERSON;

@Service
public class ClientServices {

    @PersistenceContext
    EntityManager em;

    @Transactional
    public Client createClient(ClientCreateRequest req){
        Client instance = ClientFactory.instantiate(req);
        em.persist(instance);
        return instance;
    }



    // Get a client by id
    public ClientResponse getClientByID(int id) {
        Client clientEntity = em.find(Client.class, (long) id);

        if (clientEntity == null) {
            throw new ClientNotFoundException("Client with ID " + id + " not found.");
        }
        // return the correct fields correspond to his type
        return mapClient(clientEntity);
    }


    private ClientResponse mapClient(Client clientEntity){
        switch (clientEntity.getTypeClient()) {
            case PERSON:
                Person person = (Person) clientEntity;
                return new ClientPersonResponse(
                        person.getName(), person.getEmail(),person.getTypeClient().toString(),
                        person.getBirthdate()

                );
            case COMPANY:
                Company company = (Company) clientEntity;
                return new ClientCompanyResponse(
                        company.getName(), company.getEmail(), company.getTypeClient().toString(), company.getCompanyIdentifier()
                );
            default:
                throw new IllegalStateException("Unexpected client type: " + clientEntity.getTypeClient());
        }
    }

    // Get a client by her email
    public ClientResponse getClientByEmail(String email) {
        try {
            String request = "SELECT c FROM Client c where c.email = :email";
            TypedQuery<Client> query = em.createQuery(request, Client.class);
            query.setParameter("email", email);
            return mapClient(query.getSingleResult());
        }catch (NoResultException e) {
            throw new ClientNotFoundException("Client with email " + email + " not found.");
        }

    }

    // Update a client
    @Transactional
    public void updateClient(String email, ClientModifyRequest request){
        try {
            Client existing = em.createQuery(
                            "SELECT x FROM Client x WHERE LOWER(x.email) = LOWER(:email)", Client.class)
                    .setParameter("email", email)
                    .getSingleResult();
            existing.setEmail(request.email());
            existing.setPhone(request.phone());
            existing.setName(request.name());

            em.merge(existing);
        }catch (NoResultException e) {
            throw new ClientNotFoundException("Client with email " + email + " not found.");
        }

    }





}
