package com.example.user_service.vo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestUser {

    @NotNull(message = "Email cannot be null")
    @Size(min = 2, message = "Email not be less than two characters")
    private String email;
    @NotNull(message = "name cannot be null")
    @Size(min = 2, message = "name not be less than two characters")
    private String name;
    @NotNull(message = "pwd cannot be null")
    @Size(min = 8, message = "pwd must be less equal grater than two characters")
    private String pwd;
}
