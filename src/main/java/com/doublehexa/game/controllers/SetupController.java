package com.doublehexa.game.controllers;

import com.doublehexa.game.services.FighterService;
import com.doublehexa.game.services.PowerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/setup")
@RequiredArgsConstructor
public class SetupController {
    private final FighterService fighterService;
    private final PowerService powerService;

    @GetMapping("/game/{gameId}")
    public String showSetup(@PathVariable Long gameId, Model model) {
        // Adicionar lógica para setup inicial
        return "setup/index";
    }

    @PostMapping("/fighters")
    public String setupFighters(@RequestParam Long gameId, @ModelAttribute FighterSetupForm form) {
        // Processar distribuição de pontos dos fighters
        return "redirect:/game/" + gameId;
    }
}