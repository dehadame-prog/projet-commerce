package com.example.tp.config;

import com.example.tp.model.User;
import com.example.tp.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initUsers() {
        if (!userRepository.existsByEmail("admin@gmail.com")) {
            User admin = User.builder()
                    .fullName("Administrateur")
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("Admin123"))
                    .contact("221771234567")
                    .nationality("Sénégalaise")
                    .role("ADMIN")
                    .merchantPaid(true)
                    .active(true)
                    .build();
            userRepository.save(admin);
        }

        if (!userRepository.existsByEmail("merchant@gmail.com")) {
            User merchant = User.builder()
                    .fullName("Commerçant")
                    .email("merchant@gmail.com")
                    .password(passwordEncoder.encode("Merchant123"))
                    .contact("221770000000")
                    .nationality("Sénégalaise")
                    .role("MERCHANT")
                    .merchantPaid(true)
                    .bankilyNumber("41000001")
                    .masriviNumber("42000002")
                    .sedadNumber("43000003")
                    .active(true)
                    .build();
            userRepository.save(merchant);
        }
    }
}
