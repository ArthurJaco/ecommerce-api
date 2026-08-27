package com.arthur.ecommerce_api.controller;

import com.arthur.ecommerce_api.dto.request.OrderRequestDTO;
import com.arthur.ecommerce_api.dto.response.OrderResponseDTO;
import com.arthur.ecommerce_api.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> create (@Valid @RequestBody OrderRequestDTO dto){
        OrderResponseDTO created = orderService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> findMyOrders(){
        return ResponseEntity.ok(orderService.findMyOrders());
    }
}
