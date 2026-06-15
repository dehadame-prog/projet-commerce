package com.example.tp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le texte du commentaire est obligatoire")
    @Size(min = 2, max = 1000, message = "Le commentaire doit avoir entre 2 et 1000 caractères")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @NotBlank(message = "L'auteur est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom de l'auteur doit avoir entre 2 et 100 caractères")
    @Column(nullable = false)
    private String author;

    @Column(name = "owner_email")
    private String ownerEmail;

    // Relation ManyToOne : plusieurs commentaires appartiennent à un article
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    @JsonIgnore  // Évite la récursion infinie lors de la sérialisation JSON
    private Article article;
}
