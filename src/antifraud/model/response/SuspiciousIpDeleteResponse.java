package antifraud.model.response;

import lombok.Value;

@Value
public class SuspiciousIpDeleteResponse {

    String status;

    public SuspiciousIpDeleteResponse(String ip) {
        this.status = "IP " + ip + " successfully removed!";
    }
}
