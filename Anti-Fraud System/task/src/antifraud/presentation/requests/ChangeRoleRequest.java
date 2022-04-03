package antifraud.presentation.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeRoleRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String role;
}
