package com.example.tp.service;

import com.example.tp.model.User;
import com.example.tp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

import com.example.tp.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalStateException("Cet email est déjà utilisé.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("USER");
        }
        if ("MERCHANT".equals(user.getRole())) {
            if (isBlank(user.getBankilyNumber()) && isBlank(user.getMasriviNumber()) && isBlank(user.getSedadNumber())) {
                throw new IllegalStateException("Un commercant doit renseigner au moins un numero Bankily, Masrivi ou Sedad.");
            }
            user.setMerchantPaid(true);
            user.setActive(true);
        } else {
            user.setActive(true);
        }
        return userRepository.save(user);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public void approveMerchant(Long merchantId) {
        User merchant = userRepository.findById(merchantId)
                .orElseThrow(() -> new IllegalStateException("Commerçant introuvable."));
        if (!"MERCHANT".equals(merchant.getRole())) {
            throw new IllegalStateException("L'utilisateur n'est pas un commerçant.");
        }
        merchant.setMerchantPaid(true);
        merchant.setActive(true);
        userRepository.save(merchant);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Utilisateur introuvable."));
    }

    public java.util.List<User> getAllMerchants() {
        return userRepository.findByRole("MERCHANT");
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email introuvable : " + email));

        if (!user.isActive() || ("MERCHANT".equals(user.getRole()) && !user.isMerchantPaid())) {
            throw new org.springframework.security.authentication.DisabledException("Compte inactif ou non payé.");
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole())))
                .build();
    }
}
