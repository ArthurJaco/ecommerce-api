package com.arthur.ecommerce_api.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

// ProductRequestDTO
@Data
public class ProductRequestDTO {

    @NotBlank(message = "O nome e obrigatorio")
    private String name;

    private String description;

    @NotNull(message = "O preco e obrigatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "O preco deve ser maior que zero")
    private BigDecimal price;

    @NotNull(message = "O estoque e obrigatorio")
    @Min(value = 0, message = "O estoque nao pode ser negativo")
    private Integer stock;
}