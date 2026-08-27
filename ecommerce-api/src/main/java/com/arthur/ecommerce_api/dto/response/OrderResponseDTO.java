package com.arthur.ecommerce_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDTO {

    private Long id;
    private String userEmail;
    private String status;
    private List<OrderItemResponseDTO> items;
    private BigDecimal total;
    private LocalDateTime createdAt;
}
