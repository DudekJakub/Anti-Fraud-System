package antifraud.model.request;

import antifraud.validation.ValidRegion;
import antifraud.validation.ValidStolenCardNumber;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TransactionRequest {

    @Positive
    int amount;

    @NotBlank
    String ip;

    @ValidStolenCardNumber
    String number;

    @NotBlank
    @ValidRegion
    String region;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS][.SS][.S]")
    LocalDateTime date;
}
