package antifraud.model.request;

import antifraud.model.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class ChangeRoleRequest {
    @NotBlank
    private String username;

    private Role role;
}
