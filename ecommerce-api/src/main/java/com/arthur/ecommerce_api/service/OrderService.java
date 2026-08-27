package com.arthur.ecommerce_api.service;

import com.arthur.ecommerce_api.dto.request.OrderItemRequestDTO;
import com.arthur.ecommerce_api.dto.request.OrderRequestDTO;
import com.arthur.ecommerce_api.dto.response.OrderItemResponseDTO;
import com.arthur.ecommerce_api.dto.response.OrderResponseDTO;
import com.arthur.ecommerce_api.exception.ResourceNotFoundException;
import com.arthur.ecommerce_api.model.Order;
import com.arthur.ecommerce_api.model.OrderItem;
import com.arthur.ecommerce_api.model.Product;
import com.arthur.ecommerce_api.model.User;
import com.arthur.ecommerce_api.repository.OrderItemRepository;
import com.arthur.ecommerce_api.repository.OrderRepository;
import com.arthur.ecommerce_api.repository.ProductRepository;
import com.arthur.ecommerce_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public OrderResponseDTO create(OrderRequestDTO dto){
        String email = getAuthenticatedEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException("Usuario nao encontrado: " + email));

        Order order = new Order();
        order.setUser(user);
        Order savedOrder = orderRepository.save(order);

        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequestDTO itemDto : dto.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto nao encontrado: " + itemDto.getProductId()));
            if (product.getStock() < itemDto.getQuantity()) {
                throw new IllegalArgumentException("Estoque insuficiente para: " + product.getName());
            }

            product.setStock(product.getStock() - itemDto.getQuantity());
            productRepository.save(product);

            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setProduct(product);
            item.setQuantity(itemDto.getQuantity());
            item.setUnitPrice(product.getPrice());
            orderItemRepository.save(item);
            items.add(item);
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
        }

        return toResponseDTO(savedOrder, items, total);
    }

    public List<OrderResponseDTO> findMyOrders() {
        String email = getAuthenticatedEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado: " + email));

        return orderRepository.findByUserId(user.getId())
                .stream()
                .map(order -> {
                    List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
                    BigDecimal total = items.stream()
                            .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return toResponseDTO(order, items, total);
                })
                .toList();
    }

    private OrderResponseDTO toResponseDTO(Order order, List<OrderItem> items, BigDecimal total){
        List<OrderItemResponseDTO> itemDTOs = items.stream()
                .map(item -> new OrderItemResponseDTO(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                ))
                .toList();

        return new OrderResponseDTO(
                order.getId(),
                order.getUser().getEmail(),
                order.getStatus().name(),
                itemDTOs,
                total,
                order.getCreatedAt()
        );
    }

    private String getAuthenticatedEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
