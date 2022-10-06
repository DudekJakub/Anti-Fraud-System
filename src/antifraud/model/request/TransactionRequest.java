package antifraud.model.request;

import antifraud.validation.ValidCardNumber;
import antifraud.validation.ValidRegion;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TransactionRequest {

    @Positive
    int amount;

    @NotBlank
    String ip;

    @ValidCardNumber
    String number;

    @NotBlank
    @ValidRegion
    String region;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS][.SS][.S]")
    LocalDateTime date;
}
