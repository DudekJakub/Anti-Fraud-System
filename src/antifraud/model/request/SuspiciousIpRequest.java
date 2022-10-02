package antifraud.model.request;

import antifraud.validation.ValidIp;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SuspiciousIpRequest {

    @ValidIp
    private String ip;
}
