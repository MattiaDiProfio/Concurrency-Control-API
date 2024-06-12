package com.mdp.next.entity;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter
@Setter
@RequiredArgsConstructor
public class UserUpdateRequest {

    @NonNull
    @Email(message = "email must follow a valid email format")
    private String email;

    @NonNull
    @NotBlank(message = "address cannot be blank")
    private String address;

}
