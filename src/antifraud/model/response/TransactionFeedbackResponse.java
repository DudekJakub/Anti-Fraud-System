package antifraud.model.response;

import lombok.Data;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
public class TransactionFeedbackResponse  {

    Long transactionId;
    Long amount;
    String ip;
    String number;
    String region;
    LocalDateTime date;
    String result;
    String feedback = " ";
}
