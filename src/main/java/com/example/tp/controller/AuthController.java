package com.example.tp.controller;

import com.example.tp.model.User;
import com.example.tp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "register";
        }

        try {
            userService.register(user);
        } catch (IllegalStateException ex) {
            result.reject("error.user", ex.getMessage());
            return "register";
        }

        redirectAttributes.addFlashAttribute("success", "Inscription réussie. Vous pouvez maintenant vous connecter.");
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            @RequestParam(required = false) String reason,
            Model model) {
        if (error != null) {
            if ("notfound".equals(reason)) {
                model.addAttribute("error", "Ce compte n'existe pas.");
            } else if ("disabled".equals(reason)) {
                model.addAttribute("error", "Compte désactivé ou en attente de paiement administrateur.");
            } else {
                model.addAttribute("error", "Email ou mot de passe incorrect.");
            }
        }
        if (logout != null) {
            model.addAttribute("message", "Vous êtes bien déconnecté.");
        }
        return "login";
    }
}
