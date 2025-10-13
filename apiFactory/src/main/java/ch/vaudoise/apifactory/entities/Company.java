package ch.vaudoise.apifactory.entities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.Date;

@Entity
@DiscriminatorValue("COMPANY")
public class Company extends Client{

    @Column(name = "cli_company_identifier", unique = true)
    private String companyIdentifier; // ex: aaa-123

    @Column(name = "cli_birthdate",insertable = false,updatable = false)
    private Date birthdate;


    // Getter and Setter
    public String getCompanyIdentifier() {
        return companyIdentifier;
    }

    public void setCompanyIdentifier(String companyIdentifier) {
        this.companyIdentifier = companyIdentifier;
    }
}
