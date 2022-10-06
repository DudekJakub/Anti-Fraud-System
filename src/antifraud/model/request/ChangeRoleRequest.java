package antifraud.model.request;

import antifraud.model.Role;
import antifraud.validation.ValidEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.type.EnumType;

import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class ChangeRoleRequest {
    @NotBlank
    private String username;

    @ValidEnum(enumClass = Role.class)
    private String role;
}
