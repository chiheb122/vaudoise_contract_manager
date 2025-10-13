package ch.vaudoise.apifactory.services;

import ch.vaudoise.apifactory.dto.ClientCreateRequest;
import ch.vaudoise.apifactory.entities.Client;
import ch.vaudoise.apifactory.factory.ClientFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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



}
