package antifraud.model.request;

import antifraud.model.Status;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class UserStatusRequest {

    @NotBlank
    private String username;

    private Status operation;
}
