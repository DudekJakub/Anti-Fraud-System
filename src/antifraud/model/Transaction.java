package antifraud.model;

import antifraud.validation.ValidIp;
import antifraud.validation.ValidStolenCardNumber;
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
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotNull
    @Positive
    private int amount;

    @ValidIp
    private String ip;

    @ValidStolenCardNumber
    private String number;

    @NotBlank
    private String region;

    private LocalDateTime date;
}
