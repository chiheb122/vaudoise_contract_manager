package ch.vaudoise.apifactory.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "client")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "cli_type", discriminatorType = DiscriminatorType.STRING)
public class Client {

    @Id
    @Column(name = "cli_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cli_name",nullable = false)
    private String name;
    @Column(name = "cli_email",nullable = false)
    private String email;
    @Column(name = "cli_phone")
    private String phone;
    @Enumerated(EnumType.STRING)
    @Column(name = "cli_type")
    private TypeClient typeClient;

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

    public void setTypeClient(TypeClient typeClient) {
        this.typeClient = typeClient;
    }
}
