package com.arthur.ecommerce_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {

    @NotBlank(message = "O email é obrigatorio")
    @Email(message = "Email invalido")
    private String email;

    @NotBlank(message = "A senha é obrigatoria")
    private String password;
}
