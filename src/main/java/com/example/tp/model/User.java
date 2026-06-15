package com.example.tp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit avoir entre 2 et 100 caractères")
    @Column(nullable = false)
    private String fullName;

    @Email(message = "L'email est invalide")
    @Pattern(regexp = "(?i)^[A-Za-z0-9._%+-]+@gmail\\.com$", message = "L'email doit être une adresse Gmail.")
    @NotBlank(message = "L'email est obligatoire")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "Le contact est obligatoire")
    @Size(min = 8, max = 30, message = "Le numéro de téléphone est invalide")
    @Column(nullable = false)
    private String contact;

    @NotBlank(message = "La nationalité est obligatoire")
    @Size(max = 100, message = "La nationalité est trop longue")
    @Column(nullable = false)
    private String nationality;

    @Column(nullable = false)
    private String role = "USER";

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    @Builder.Default
    @Column(nullable = false)
    private boolean merchantPaid = false;

    @Column(name = "bankily_number")
    private String bankilyNumber;

    @Column(name = "masrivi_number")
    private String masriviNumber;

    @Column(name = "sedad_number")
    private String sedadNumber;
}
