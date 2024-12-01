package com.doublehexa.game.controllers;

import com.doublehexa.game.models.Game;
import com.doublehexa.game.models.GameFighter;
import com.doublehexa.game.models.Player;
import com.doublehexa.game.services.GameService;
import com.doublehexa.game.services.PlayerService;
import com.doublehexa.game.dto.FighterSetupForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/setup")
@RequiredArgsConstructor
public class SetupController {
    private final GameService gameService;
    private final PlayerService playerService;

    @GetMapping("/game/{gameId}")
    public String showSetup(@PathVariable Long gameId, Model model, Principal principal) {
        Game game = gameService.findById(gameId);
        Player player = playerService.findByUsername(principal.getName());

        model.addAttribute("game", game);
        model.addAttribute("setupForm", new FighterSetupForm());
        return "setup/index";
    }

    @PostMapping("/fighters")
    public String setupFighters(@RequestParam Long gameId,
                                @ModelAttribute FighterSetupForm form,
                                Principal principal) {
        Game game = gameService.findById(gameId);
        Player player = playerService.findByUsername(principal.getName());

        List<GameFighter> fighters = form.toGameFighters();
        gameService.setupFighters(game, player, fighters);

        return "redirect:/game/" + gameId;
    }
}