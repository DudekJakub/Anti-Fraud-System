package antifraud.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class StolenCardCreateResponse {

    Long id;
    String number;
}
