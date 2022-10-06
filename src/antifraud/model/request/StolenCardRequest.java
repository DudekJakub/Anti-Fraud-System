package antifraud.model.request;

import antifraud.validation.ValidCardNumber;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StolenCardRequest {

    @ValidCardNumber
    String number;
}
