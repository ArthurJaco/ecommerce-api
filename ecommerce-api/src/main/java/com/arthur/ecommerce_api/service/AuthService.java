package com.arthur.ecommerce_api.service;

import com.arthur.ecommerce_api.dto.request.LoginRequestDTO;
import com.arthur.ecommerce_api.dto.request.RegisterRequestDTO;
import com.arthur.ecommerce_api.dto.response.AuthResponseDTO;
import com.arthur.ecommerce_api.model.User;
import com.arthur.ecommerce_api.model.enums.Role;
import com.arthur.ecommerce_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponseDTO register(RegisterRequestDTO dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email ja cadastrado: " + dto.getEmail());
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponseDTO(token, user.getEmail(), user.getRole().name());
    }

    public AuthResponseDTO login(LoginRequestDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Email ou senha invalidos"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Email ou senha invalidos");
        }

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponseDTO(token, user.getEmail(), user.getRole().name());
    }
}