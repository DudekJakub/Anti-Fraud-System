package antifraud.model.response;

import lombok.Value;

@Value
public class StolenCardDeleteResponse {

    String status;

    public StolenCardDeleteResponse(String number) {
        this.status = "Card " + number + " successfully removed!";
    }
}
