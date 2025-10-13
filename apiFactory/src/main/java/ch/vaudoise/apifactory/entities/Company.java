package ch.vaudoise.apifactory.entities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("COMPANY")
public class Company extends Client{

    @Column(name = "cli_company_identifier", unique = true)
    private String companyIdentifier; // ex: aaa-123


    // Getter and Setter
    public String getCompanyIdentifier() {
        return companyIdentifier;
    }

    public void setCompanyIdentifier(String companyIdentifier) {
        this.companyIdentifier = companyIdentifier;
    }
}
