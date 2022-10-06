package antifraud.model;

import antifraud.validation.ValidCardNumber;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "credit_cards",
        indexes = { @Index(name = "idx_creditcard_cardnumber", columnList = "cardNumber") },
        uniqueConstraints = {@UniqueConstraint(columnNames = {"cardNumber"})})
public class CreditCard {

    @Id
    @GeneratedValue
    private Long id;

    @ValidCardNumber
    @Column(unique = true)
    private String cardNumber;

    @ColumnDefault("200")
    private double allowedLimit = 200;

    @ColumnDefault("1500")
    private double manualProcessingLimit = 1500;

    @ToString.Exclude
    @OneToMany(mappedBy = "creditCard")
    private Set<Transaction> transactions = new HashSet<>();

    public CreditCard(String cardNumber) {
        this.cardNumber = cardNumber;
    }
}
