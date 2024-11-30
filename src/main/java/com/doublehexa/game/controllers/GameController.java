package com.doublehexa.game.controllers;

import com.doublehexa.game.models.Game;
import com.doublehexa.game.services.GameService;
import com.doublehexa.game.services.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;
    private final PlayerService playerService;

    @GetMapping("/lobby")
    public String showLobby(Model model) {
        // Lista jogos disponíveis e jogadores online
        return "game/lobby";
    }

    @GetMapping("/{id}")
    public String showGame(@PathVariable Long id, Model model) {
        Game game = gameService.findById(id);
        model.addAttribute("game", game);
        return "game/show";
    }

    @PostMapping("/create")
    public String createGame(@RequestParam Long player1Id, @RequestParam Long player2Id) {
        Game game = gameService.createGame(player1Id, player2Id);
        return "redirect:/game/" + game.getId();
    }
}