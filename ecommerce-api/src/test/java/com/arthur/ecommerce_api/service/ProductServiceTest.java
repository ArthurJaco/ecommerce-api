package com.arthur.ecommerce_api.service;

import com.arthur.ecommerce_api.dto.request.ProductRequestDTO;
import com.arthur.ecommerce_api.exception.ResourceNotFoundException;
import com.arthur.ecommerce_api.model.Product;
import com.arthur.ecommerce_api.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void findById_deveRetornarProduto_quandoIdExiste(){
        Product product = new Product();
        product.setId(1L);
        product.setName("Mouse Gamer");
        product.setPrice(new BigDecimal("150.00"));
        product.setStock(10);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        var result = productService.findById(1L);

        assertThat(result.getName()).isEqualTo("Mouse Gamer");
    }

    @Test
    void findById_deveLancarExcecao_quandoIdNaoExiste() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_deveSalvarProduto_comDadosDoDto(){
        ProductRequestDTO dto = new ProductRequestDTO();
        dto.setName("Teclado mecanico");
        dto.setDescription("Teclado gamer 60%");
        dto.setPrice(new BigDecimal("250.00"));
        dto.setStock(15);

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName("Teclado Mecanico");
        savedProduct.setDescription("Teclado RGB");
        savedProduct.setPrice(new BigDecimal("250.00"));
        savedProduct.setStock(15);

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        var result = productService.create(dto);

        assertThat(result.getName()).isEqualTo("Teclado Mecanico");
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("250.00"));
        assertThat(result.getStock()).isEqualTo(15);
    }


}
