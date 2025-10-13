package ch.vaudoise.apifactory.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "client")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "cli_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Client {

    @Id
    @Column(name = "cli_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cli_name",nullable = false)
    private String name;
    @Column(name = "cli_email",nullable = false,unique = true)
    private String email;
    @Column(name = "cli_phone",unique = true)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "cli_type",insertable=false, updatable=false)
    private TypeClient typeClient;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Contract> ListOfContracts = new ArrayList<>();

    // Getter and Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public TypeClient getTypeClient() {
        return typeClient;
    }

    public List<Contract> getListOfContracts() {
        return ListOfContracts;
    }

    public void setListOfContracts(List<Contract> listOfContracts) {
        ListOfContracts = listOfContracts;
    }
}
