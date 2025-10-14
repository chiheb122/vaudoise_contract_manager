package ch.vaudoise.apifactory.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "contract")
public class Contract {

    // Attributs
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contract_id", nullable = false)
    private Long id;

    // Dates follow ISO 8601 format (yyyy-MM-dd)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @Column(name = "start_date")
    private Date startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @Column(name = "end_date",nullable = true)
    private Date endDate;

    @Column(name = "cost_amount",nullable = false)
    private BigDecimal costAmount;  // precise monetary value, safer than double

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonIgnore // To respect this constraint Keep the update date (last modified date) internally
    @Column(name = "last_modified_date")
    private Date lastModifiedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cli_id")
    private Client client;


    // Make startDate with current date if is it null;
    @PrePersist
    public void setDefaultDate(){
        Date now = new Date();
        this.startDate = (this.startDate == null) ? now : this.startDate;
        this.lastModifiedDate = now; // initialize at creation
    }

    //Make the current date for every modification
    @PreUpdate
    public void setUpdateDate(){
        this.lastModifiedDate = new Date();
    }

    // Getter and Setter
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getCostAmount() {
        return costAmount;
    }

    public void setCostAmount(BigDecimal costAmount) {
        this.lastModifiedDate = new Date();
        this.costAmount = costAmount;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
