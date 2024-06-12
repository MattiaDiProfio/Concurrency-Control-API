package com.mdp.next.entity;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter
@Setter
@RequiredArgsConstructor
public class UserUpdateRequest {

    @NonNull
    @Email(message = "User's email must follow a valid email format")
    private String email;

    @NonNull
    @NotBlank(message = "User's address cannot be blank")
    private String address;

}
