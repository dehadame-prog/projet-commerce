package com.example.tp.controller;

import com.example.tp.model.Article;
import com.example.tp.model.Comment;
import com.example.tp.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    // GET /api/articles → Lister tous les articles
    @GetMapping
    public ResponseEntity<List<Article>> getAllArticles() {
        return ResponseEntity.ok(articleService.getAllArticles());
    }

    // GET /api/articles/{id} → Récupérer un article avec ses commentaires
    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.getArticleById(id));
    }

    // POST /api/articles → Créer un article
    @PostMapping
    public ResponseEntity<Article> createArticle(@Valid @RequestBody Article article) {
        Article created = articleService.createArticle(article);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/articles/{id} → Modifier un article
    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(
            @PathVariable Long id,
            @Valid @RequestBody Article article) {
        return ResponseEntity.ok(articleService.updateArticle(id, article));
    }

    // DELETE /api/articles/{id} → Supprimer un article
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }

    // ── Gestion des commentaires ──────────────────────────────────────

    // GET /api/articles/{id}/comments → Lister les commentaires d'un article
    @GetMapping("/{id}/comments")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.getCommentsByArticle(id));
    }

    // POST /api/articles/{id}/comments → Ajouter un commentaire
    @PostMapping("/{id}/comments")
    public ResponseEntity<Comment> addComment(
            @PathVariable Long id,
            @Valid @RequestBody Comment comment) {
        Comment created = articleService.addComment(id, comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // DELETE /api/articles/{articleId}/comments/{commentId} → Supprimer un commentaire
    @DeleteMapping("/{articleId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long articleId,
            @PathVariable Long commentId) {
        articleService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
