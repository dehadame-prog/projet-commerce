package com.example.tp.service;

import com.example.tp.exception.ResourceNotFoundException;
import com.example.tp.model.Article;
import com.example.tp.model.Comment;
import com.example.tp.repository.ArticleRepository;
import com.example.tp.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    // Lister tous les articles
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    // Récupérer un article avec ses commentaires
    @Transactional(readOnly = true)
    public Article getArticleById(Long id) {
        return articleRepository.findByIdWithComments(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article", id));
    }

    // Créer un article
    public Article createArticle(Article article) {
        return articleRepository.save(article);
    }

    // Modifier un article
    public Article updateArticle(Long id, Article updated) {
        Article existing = getArticleById(id);
        existing.setTitle(updated.getTitle());
        existing.setContent(updated.getContent());
        return articleRepository.save(existing);
    }

    // Supprimer un article (et ses commentaires grâce à cascade)
    public void deleteArticle(Long id) {
        Article article = getArticleById(id);
        articleRepository.delete(article);
    }

    // Ajouter un commentaire à un article
    @Transactional
    public Comment addComment(Long articleId, Comment comment) {
        Article article = getArticleById(articleId);
        comment.setArticle(article);
        return commentRepository.save(comment);
    }

    // Supprimer un commentaire
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Commentaire", commentId));
        commentRepository.delete(comment);
    }

    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Commentaire", commentId));
    }

    public Comment updateComment(Long commentId, Comment updated) {
        Comment existing = getCommentById(commentId);
        existing.setAuthor(updated.getAuthor());
        existing.setText(updated.getText());
        return commentRepository.save(existing);
    }

    // Lister les commentaires d'un article
    public List<Comment> getCommentsByArticle(Long articleId) {
        // Vérifier que l'article existe
        getArticleById(articleId);
        return commentRepository.findByArticleId(articleId);
    }
}
