package ch.vaudoise.apifactory.repository;

import ch.vaudoise.apifactory.entities.Client;
import ch.vaudoise.apifactory.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findByName(String name);
    Optional<Client> findByEmailIgnoreCase(String email);
    Optional<Company> findByCompanyIdentifierIgnoreCase(String companyIdentifier);
    // find by phone number
    Optional<Client> findByPhoneIgnoreCase(String phone);
}
