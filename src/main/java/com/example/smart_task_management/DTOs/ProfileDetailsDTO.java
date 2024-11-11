package com.example.smart_task_management.DTOs;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProfileDetailsDTO {

    @NotBlank(message = "required")
    String fullName;

    @NotBlank(message = "required")
    String email;

    @NotBlank(message = "required")
    String userName;

    @NotBlank(message = "required")
    String password;

    @NotBlank(message = "required")
    String passwordConfirm;

    public boolean emailEmpty()
    {
        return email == null || email.isEmpty();
    }

}
