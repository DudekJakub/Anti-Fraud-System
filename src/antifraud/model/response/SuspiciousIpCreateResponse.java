package antifraud.model.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SuspiciousIpCreateResponse {

    Long id;
    String ip;
}
