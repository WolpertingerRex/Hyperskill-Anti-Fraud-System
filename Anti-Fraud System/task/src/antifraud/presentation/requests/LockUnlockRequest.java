package antifraud.presentation.requests;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LockUnlockRequest {
    @NotBlank
    private String username;

    @NotBlank
    @ValueOfEnum(enumClass = Operation.class)
    private String operation;
}
