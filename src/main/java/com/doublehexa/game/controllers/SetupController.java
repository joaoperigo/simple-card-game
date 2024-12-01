package com.doublehexa.game.controllers;

import com.doublehexa.game.models.Game;
import com.doublehexa.game.models.GameFighter;
import com.doublehexa.game.models.Player;
import com.doublehexa.game.services.GameService;
import com.doublehexa.game.services.PlayerService;
import com.doublehexa.game.dto.FighterSetupForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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

        // Verificar se o jogador pertence ao jogo
        if (!isPlayerInGame(game, player)) {
            return "redirect:/game/lobby?error=not_your_game";
        }

        // Verificar se já configurou os fighters
        if (gameService.hasPlayerSetupFighters(game, player)) {
            return "redirect:/game/" + gameId + "?error=already_setup";
        }

        model.addAttribute("game", game);
        model.addAttribute("setupForm", new FighterSetupForm());
        return "setup/index";
    }

    @PostMapping("/fighters")
    public String setupFighters(
            @RequestParam Long gameId,
            @Valid @ModelAttribute FighterSetupForm form,  // Adicione @Valid
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Principal principal) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please check the form for errors");
            return "redirect:/setup/game/" + gameId;
        }

        try {
            if (!form.isValid()) {
                redirectAttributes.addFlashAttribute("error", "Invalid fighter setup. Check points distribution.");
                return "redirect:/setup/game/" + gameId;
            }

            Game game = gameService.findById(gameId);
            Player player = playerService.findByUsername(principal.getName());

            List<GameFighter> fighters = form.toGameFighters();
            gameService.setupFighters(game, player, fighters);

            redirectAttributes.addFlashAttribute("success", "Fighters setup completed!");
            return "redirect:/game/" + gameId;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/setup/game/" + gameId;
        }
    }

    private boolean isPlayerInGame(Game game, Player player) {
        return game.getPlayer1().getId().equals(player.getId()) ||
                game.getPlayer2().getId().equals(player.getId());
    }
}