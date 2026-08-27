package com.arthur.ecommerce_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDTO {

    @NotBlank(message = "O nome é obrigatorio")
    private String name;

    @NotBlank(message = "O email é obrigatorio")
    @Email(message = "Email invalido")
    private String email;

    @NotBlank(message = "A senha e obrigatorio")
    @Size(min = 6, message = "A senha deve conter no minimo 6 caracteres")
    private String password;

}
