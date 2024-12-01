package com.doublehexa.game.controllers;

import com.doublehexa.game.models.Game;
import com.doublehexa.game.models.Player;
import com.doublehexa.game.services.GameService;
import com.doublehexa.game.services.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;
    private final PlayerService playerService;

    @GetMapping("/lobby")
    public String showLobby(Model model, Principal principal) {
        Player currentPlayer = playerService.findByUsername(principal.getName());

        // Busca TODOS os jogadores exceto o atual
        List<Player> otherPlayers = playerService.findAllPlayersExcept(currentPlayer);

        // Busca jogos em SETUP ou PLAYING
        List<Game> activeGames = gameService.findActiveGames();

        model.addAttribute("games", activeGames);
        model.addAttribute("onlinePlayers", otherPlayers);
        model.addAttribute("currentPlayer", currentPlayer);

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