package ch.vaudoise.apifactory.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;


import java.util.Date;

@Entity
@DiscriminatorValue("PERSON")
public class Person extends Client{

    // Dates follow ISO 8601 format (yyyy-MM-dd)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @Column(name = "cli_birthdate")
    private Date birthdate;


    // Getter and Setter
    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }
}
