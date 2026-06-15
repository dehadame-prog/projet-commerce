package com.example.tp.controller;

import com.example.tp.model.Article;
import com.example.tp.model.Cart;
import com.example.tp.model.Product;
import com.example.tp.service.ArticleService;
import com.example.tp.service.ImageStorageService;
import com.example.tp.service.ProductService;
import com.example.tp.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.tp.model.Comment; // Ou le package exact où se trouve votre entité

@Controller
@RequiredArgsConstructor
public class WebController {

    private final ProductService productService;
    private final ArticleService articleService;
    private final ImageStorageService imageStorageService;
    private final UserService userService;

    // ── Page d'accueil ────────────────────────────────────────────────

    @GetMapping("/")
    public String home(Model model, Authentication authentication, HttpSession session) {
        model.addAttribute("totalProducts", productService.getAllProducts().size());
        model.addAttribute("totalArticles", articleService.getAllArticles().size());
        model.addAttribute("authenticated", authentication != null && authentication.isAuthenticated());
        model.addAttribute("cartSize", getCart(session).getItems().size());
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("userRole",
                    authentication.getAuthorities().stream().findFirst().map(Object::toString).orElse("ROLE_USER"));
            model.addAttribute("canManageProducts", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")
                            || auth.getAuthority().equals("ROLE_MERCHANT")));
        } else {
            model.addAttribute("userRole", "ROLE_ANONYMOUS");
            model.addAttribute("canManageProducts", false);
        }
        return "index";
    }

    // ── Pages Produits ────────────────────────────────────────────────

    @GetMapping("/products")
    public String listProducts(Model model,
            @RequestParam(required = false) String search,
            Authentication authentication,
            HttpSession session) {
        if (search != null && !search.isBlank()) {
            model.addAttribute("products", productService.searchByName(search));
            model.addAttribute("search", search);
        } else {
            model.addAttribute("products", productService.getAllProducts());
        }
        model.addAttribute("newProduct", new Product());
        model.addAttribute("authenticated", authentication != null && authentication.isAuthenticated());
        model.addAttribute("cartSize", getCart(session).getItems().size());
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("userRole",
                    authentication.getAuthorities().stream().findFirst().map(Object::toString).orElse("ROLE_USER"));
            model.addAttribute("canManageProducts", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")
                            || auth.getAuthority().equals("ROLE_MERCHANT")));
        } else {
            model.addAttribute("userRole", "ROLE_ANONYMOUS");
            model.addAttribute("canManageProducts", false);
        }
        return "products";
    }

    @PostMapping("/products")
    public String addProduct(@Valid @ModelAttribute("newProduct") Product product,
            BindingResult result,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("products", productService.getAllProducts());
            model.addAttribute("authenticated", authentication != null && authentication.isAuthenticated());
            model.addAttribute("canManageProducts", authentication != null && authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")
                            || auth.getAuthority().equals("ROLE_MERCHANT")));
            model.addAttribute("userRole", authentication != null && authentication.isAuthenticated()
                    ? authentication.getAuthorities().stream().findFirst().map(Object::toString).orElse("ROLE_USER")
                    : "ROLE_ANONYMOUS");
            return "products";
        }
        if (authentication != null && authentication.isAuthenticated()) {
            product.setOwnerEmail(authentication.getName());
        }
        String imageUrl = imageStorageService.storeProductImage(imageFile);
        product.setImageUrl(imageUrl);
        productService.createProduct(product);
        redirectAttributes.addFlashAttribute("success", "Produit ajouté avec succès !");
        return "redirect:/products";
    }

    @GetMapping("/products/{id}/edit")
    public String editProduct(@PathVariable Long id,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        Product product = productService.getProductById(id);
        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("error", "Vous devez être connecté pour modifier un produit.");
            return "redirect:/login";
        }
        model.addAttribute("product", product);
        model.addAttribute("authenticated", true);
        model.addAttribute("canManageProducts", authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")
                        || auth.getAuthority().equals("ROLE_MERCHANT")));
        return "product-edit";
    }

    @PostMapping("/products/{id}/update")
    public String updateProduct(@PathVariable Long id,
            @Valid @ModelAttribute("product") Product product,
            BindingResult result,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("authenticated", authentication != null && authentication.isAuthenticated());
            model.addAttribute("canManageProducts", authentication != null && authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")
                            || auth.getAuthority().equals("ROLE_MERCHANT")));
            return "product-edit";
        }
        String imageUrl = imageStorageService.storeProductImage(imageFile);
        product.setImageUrl(imageUrl);
        productService.updateProduct(id, product);
        redirectAttributes.addFlashAttribute("success", "Produit mis à jour avec succès.");
        return "redirect:/products";
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productService.deleteProduct(id);
        redirectAttributes.addFlashAttribute("success", "Produit supprimé.");
        return "redirect:/products";
    }

    private Cart getCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    // ── Pages Blog ────────────────────────────────────────────────────

    @GetMapping("/articles")
    public String listArticles(Model model, Authentication authentication) {
        model.addAttribute("articles", articleService.getAllArticles());
        model.addAttribute("newArticle", new Article());
        model.addAttribute("authenticated", authentication != null && authentication.isAuthenticated());
        model.addAttribute("canManageArticles",
                hasRole(authentication, "ADMIN") || hasRole(authentication, "MERCHANT"));
        return "articles";
    }

    @PostMapping("/articles")
    public String addArticle(@Valid @ModelAttribute("newArticle") Article article,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("articles", articleService.getAllArticles());
            model.addAttribute("canManageArticles", true);
            return "articles";
        }
        articleService.createArticle(article);
        redirectAttributes.addFlashAttribute("success", "Article publié !");
        return "redirect:/articles";
    }

    @GetMapping("/articles/{id}")
    public String viewArticle(@PathVariable Long id, Model model, Authentication authentication) {
        model.addAttribute("article", articleService.getArticleById(id));
        model.addAttribute("newComment", new Comment());
        model.addAttribute("authenticated", isAuthenticated(authentication));
        model.addAttribute("currentEmail", isAuthenticated(authentication) ? authentication.getName() : "");
        model.addAttribute("canManageAllComments",
                hasRole(authentication, "ADMIN") || hasRole(authentication, "MERCHANT"));
        return "article-detail";
    }

    @PostMapping("/articles/{id}/comments")
    public String addComment(@PathVariable Long id,
            @Valid @ModelAttribute("newComment") Comment comment,
            BindingResult result,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("article", articleService.getArticleById(id));
            model.addAttribute("authenticated", isAuthenticated(authentication));
            model.addAttribute("currentEmail", isAuthenticated(authentication) ? authentication.getName() : "");
            model.addAttribute("canManageAllComments",
                    hasRole(authentication, "ADMIN") || hasRole(authentication, "MERCHANT"));
            return "article-detail";
        }
        if (isAuthenticated(authentication)) {
            comment.setOwnerEmail(authentication.getName());
        }
        articleService.addComment(id, comment);
        redirectAttributes.addFlashAttribute("success", "Commentaire ajouté !");
        return "redirect:/articles/" + id;
    }

    @PostMapping("/articles/{articleId}/comments/{commentId}/update")
    public String updateComment(@PathVariable Long articleId,
            @PathVariable Long commentId,
            @Valid @ModelAttribute("comment") Comment comment,
            BindingResult result,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        Comment existing = articleService.getCommentById(commentId);
        if (!canManageComment(authentication, existing)) {
            redirectAttributes.addFlashAttribute("error", "Vous ne pouvez modifier que vos propres commentaires.");
            return "redirect:/articles/" + articleId;
        }
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Le commentaire doit contenir un auteur et un texte valides.");
            return "redirect:/articles/" + articleId;
        }
        articleService.updateComment(commentId, comment);
        redirectAttributes.addFlashAttribute("success", "Commentaire modifiÃ©.");
        return "redirect:/articles/" + articleId;
    }

    @PostMapping("/articles/{articleId}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable Long articleId,
            @PathVariable Long commentId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        Comment existing = articleService.getCommentById(commentId);
        if (!canManageComment(authentication, existing)) {
            redirectAttributes.addFlashAttribute("error", "Vous ne pouvez supprimer que vos propres commentaires.");
            return "redirect:/articles/" + articleId;
        }
        articleService.deleteComment(commentId);
        redirectAttributes.addFlashAttribute("success", "Commentaire supprimÃ©.");
        return "redirect:/articles/" + articleId;
    }

    @PostMapping("/articles/{id}/delete")
    public String deleteArticle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        articleService.deleteArticle(id);
        redirectAttributes.addFlashAttribute("success", "Article supprimé.");
        return "redirect:/articles";
    }
    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && authentication.getAuthorities().stream()
                        .noneMatch(auth -> auth.getAuthority().equals("ROLE_ANONYMOUS"));
    }

    private boolean hasRole(Authentication authentication, String role) {
        return isAuthenticated(authentication) && authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role));
    }

    private boolean canManageProducts(Authentication authentication) {
        return hasRole(authentication, "ADMIN") || hasRole(authentication, "MERCHANT");
    }

    private boolean canManageComment(Authentication authentication, Comment comment) {
        return hasRole(authentication, "ADMIN")
                || hasRole(authentication, "MERCHANT")
                || (isAuthenticated(authentication)
                        && comment.getOwnerEmail() != null
                        && comment.getOwnerEmail().equals(authentication.getName()));
    }
}
