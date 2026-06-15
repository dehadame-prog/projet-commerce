package com.example.tp.controller;

import com.example.tp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/admin/merchants")
    public String listMerchants(Model model) {
        model.addAttribute("merchants", userService.getAllMerchants());
        return "admin-merchants";
    }

    @PostMapping("/admin/merchants/{id}/approve")
    public String approveMerchant(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.approveMerchant(id);
        redirectAttributes.addFlashAttribute("success", "Commerçant approuvé.");
        return "redirect:/admin/merchants";
    }
}
