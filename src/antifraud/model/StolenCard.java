package antifraud.model;

import antifraud.validation.ValidCardNumber;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "StolenCard", indexes = {
        @Index(name = "idx_stolencard_card_number", columnList = "card_number")
})
public class StolenCard {

    @Id
    @GeneratedValue
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @ValidCardNumber
    @Column(name = "card_number")
    private String number;
}
