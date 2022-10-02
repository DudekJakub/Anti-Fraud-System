package antifraud.model.request;

import antifraud.validation.ValidStolenCardNumber;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StolenCardRequest {

    @ValidStolenCardNumber
    String number;
}
