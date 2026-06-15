package com.example.tp.controller;

import com.example.tp.model.Cart;
import com.example.tp.model.CartItem;
import com.example.tp.model.Payment;
import com.example.tp.model.User;
import com.example.tp.service.PaymentService;
import com.example.tp.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final UserService userService;

    @GetMapping("/payments")
    public String showPaymentPage(Model model, Authentication authentication, HttpSession session) {
        User user = userService.findByEmail(authentication.getName());
        Cart cart = getCart(session);
        Payment payment = new Payment();
        payment.setAmount(cart.getTotal());

        fillPaymentModel(model, user, payment, cart);
        return "payment";
    }

    @PostMapping("/payments")
    public String processPayment(@Valid @ModelAttribute("payment") Payment payment,
            BindingResult result,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model,
            HttpSession session) {
        User user = userService.findByEmail(authentication.getName());
        Cart cart = getCart(session);

        if (cart.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Votre panier est vide. Ajoutez des produits avant de payer.");
            return "redirect:/cart";
        }

        if (result.hasErrors()) {
            fillPaymentModel(model, user, payment, cart);
            return "payment";
        }

        String recipientSummary = buildRecipientSummary(cart, payment.getMethod());
        if (recipientSummary.isBlank()) {
            fillPaymentModel(model, user, payment, cart);
            model.addAttribute("error", "Aucun commercant du panier n'a renseigne ce mode de paiement.");
            return "payment";
        }

        paymentService.createPayment(user, payment, cart.getTotal(), buildItemsSummary(cart), recipientSummary);
        session.removeAttribute("cart");
        redirectAttributes.addFlashAttribute("success", "Paiement enregistre avec succes. Votre panier a ete vide.");
        return "redirect:/payments";
    }

    private void fillPaymentModel(Model model, User user, Payment payment, Cart cart) {
        model.addAttribute("payment", payment);
        model.addAttribute("payments", paymentService.getPaymentsForUser(user));
        model.addAttribute("userName", user.getFullName());
        model.addAttribute("cart", cart);
        model.addAttribute("cartTotal", cart.getTotal());
        model.addAttribute("recipientAccounts", buildRecipientAccounts(cart));
    }

    private Cart getCart(HttpSession session) {
        Object cartObj = session.getAttribute("cart");
        if (cartObj instanceof Cart) {
            return (Cart) cartObj;
        }
        Cart cart = new Cart();
        session.setAttribute("cart", cart);
        return cart;
    }

    private String buildItemsSummary(Cart cart) {
        return cart.getItems().stream()
                .map(item -> item.getName() + " x" + item.getQuantity() + " = " + item.getSubtotal() + " MRU")
                .collect(Collectors.joining("\n"));
    }

    private Map<String, String> buildRecipientAccounts(Cart cart) {
        Map<String, String> accounts = new LinkedHashMap<>();
        for (String method : new String[] { "BANKILY", "MASRIVI", "SEDAD" }) {
            String summary = buildRecipientSummary(cart, method);
            if (!summary.isBlank()) {
                accounts.put(method, summary);
            }
        }
        return accounts;
    }

    private String buildRecipientSummary(Cart cart, String method) {
        return cart.getItems().stream()
                .map(CartItem::getOwnerEmail)
                .filter(email -> email != null && !email.isBlank())
                .distinct()
                .map(userService::findByEmail)
                .map(merchant -> merchant.getFullName() + " : " + getMerchantNumber(merchant, method))
                .filter(line -> !line.endsWith(" : "))
                .collect(Collectors.joining("\n"));
    }

    private String getMerchantNumber(User merchant, String method) {
        if ("BANKILY".equals(method)) {
            return merchant.getBankilyNumber() == null ? "" : merchant.getBankilyNumber();
        }
        if ("MASRIVI".equals(method)) {
            return merchant.getMasriviNumber() == null ? "" : merchant.getMasriviNumber();
        }
        if ("SEDAD".equals(method)) {
            return merchant.getSedadNumber() == null ? "" : merchant.getSedadNumber();
        }
        return "";
    }
}
