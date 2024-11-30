package com.doublehexa.game.controllers;

import com.doublehexa.game.models.Player;
import com.doublehexa.game.services.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/players")
@RequiredArgsConstructor
public class PlayerController {
    private final PlayerService playerService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("player", new Player());
        return "players/register";
    }

    @PostMapping("/register")
    public String registerPlayer(@ModelAttribute Player player) {
        playerService.createPlayer(player);
        return "redirect:/game/lobby";
    }

    @GetMapping("/profile")
    public String showProfile(Model model) {
        // Adicionar lógica para pegar jogador atual
        return "players/profile";
    }
}