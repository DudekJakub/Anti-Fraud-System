package antifraud.model.request;

import antifraud.model.Result;
import antifraud.validation.ValidEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
public class TransactionFeedbackRequest {

    @NotNull
    private Long transactionId;

    @ValidEnum(enumClass = Result.class)
    private String feedback;
}
