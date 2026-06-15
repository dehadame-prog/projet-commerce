package com.example.tp.controller;

import com.example.tp.model.Cart;
import com.example.tp.model.CartItem;
import com.example.tp.model.Product;
import com.example.tp.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final ProductService productService;

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        Cart cart = getCart(session);
        model.addAttribute("cart", cart);
        return "cart";
    }

    @PostMapping("/cart/add/{productId}")
    public String addToCart(@PathVariable Long productId,
            @RequestParam(defaultValue = "1") int quantity,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Product product = productService.getProductById(productId);
        Cart cart = getCart(session);
        String ownerEmail = product.getOwnerEmail() == null || product.getOwnerEmail().isBlank()
                ? "merchant@gmail.com"
                : product.getOwnerEmail();
        cart.addItem(new CartItem(product.getId(), product.getName(), product.getPrice(), quantity, ownerEmail));
        session.setAttribute("cart", cart);
        redirectAttributes.addFlashAttribute("success", "Produit ajouté au panier.");
        return "redirect:/products";
    }

    @PostMapping("/cart/remove/{productId}")
    public String removeFromCart(@PathVariable Long productId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Cart cart = getCart(session);
        cart.removeItem(productId);
        session.setAttribute("cart", cart);
        redirectAttributes.addFlashAttribute("success", "Produit retiré du panier.");
        return "redirect:/cart";
    }

    @PostMapping("/cart/update/{productId}")
    public String updateCartQuantity(@PathVariable Long productId,
            @RequestParam int quantity,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Cart cart = getCart(session);
        cart.updateQuantity(productId, quantity);
        session.setAttribute("cart", cart);
        redirectAttributes.addFlashAttribute("success", "Quantité mise à jour.");
        return "redirect:/cart";
    }

    private Cart getCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }
        return cart;
    }
}
