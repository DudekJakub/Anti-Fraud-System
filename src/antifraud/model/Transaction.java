package antifraud.model;

import antifraud.validation.ValidIp;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transactions", indexes = {
        @Index(name = "idx_transaction_number_date", columnList = "credit_card_id, date")
})
public class Transaction {

    @Id
    @GeneratedValue
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotNull
    @Positive
    private Long amount;

    @ValidIp
    private String ip;

    @ManyToOne(targetEntity = CreditCard.class)
    @JoinColumn(name = "credit_card_id")
    private CreditCard creditCard;

    @NotBlank
    private String region;

    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    private Result result;

    @Enumerated(EnumType.STRING)
    private Result feedback;
}
