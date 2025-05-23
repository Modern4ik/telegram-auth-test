package com.example.telegramauth.controller;

import com.example.telegramauth.model.TelegramUser;
import com.example.telegramauth.service.TelegramAuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class TelegramAuthController {

    private final TelegramAuthService telegramAuthService;

    @PostMapping("/auth/telegram")
    public String authTelegram(@RequestParam("id") Long id,
                               @RequestParam("first_name") String firstName,
                               @RequestParam(value = "last_name", required = false) String lastName,
                               @RequestParam(value = "username", required = false) String userName,
                               @RequestParam(value = "photo_url", required = false) String photoUrl,
                               @RequestParam("auth_date") Long authDate,
                               @RequestParam("hash") String hash,
                               HttpSession session,
                               Model model) {

        TelegramUser user = TelegramUser.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .username(userName)
                .photoUrl(photoUrl)
                .authDate(authDate)
                .hash(hash)
                .build();

        if (telegramAuthService.checkTelegramAuthorization(user)) {
            session.setAttribute("user", user);
            return "redirect:/profile";
        } else {
            model.addAttribute("error", "Invalid Telegram authentication");
            return "error";
        }
    }

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        TelegramUser user = (TelegramUser) session.getAttribute("user");
        model.addAttribute("user", user);

        return "index";
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        TelegramUser user = (TelegramUser) session.getAttribute("user");

        if (user == null) {
            return "redirect:/";
        }

        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();

        return "redirect:/";
    }
}
