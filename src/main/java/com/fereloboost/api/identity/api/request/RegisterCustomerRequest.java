package com.fereloboost.api.identity.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterCustomerRequest(
        @NotBlank @Email @Size(max = 320) String email,
        @NotBlank @Size(min = 2, max = 100) String displayName,
        @Size(max = 30) String phone,
        @NotBlank @Size(min = 12, max = 128) String password
) {
}
