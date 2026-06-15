package com.example.tp.repository;

import com.example.tp.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    // Recherche par titre
    List<Article> findByTitleContainingIgnoreCase(String title);

    // Charger l'article avec ses commentaires (évite le problème N+1)
    @Query("SELECT a FROM Article a LEFT JOIN FETCH a.comments WHERE a.id = :id")
    Optional<Article> findByIdWithComments(Long id);
}
