package com.doublehexa.game.controllers;

import com.doublehexa.game.models.Game;
import com.doublehexa.game.services.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameApiController {
    private final GameService gameService;

    @PostMapping("/{gameId}/move")
    public ResponseEntity<Game> makeMove(
            @PathVariable Long gameId,
            @RequestParam Long attackingFighterId,
            @RequestParam Long attackPowerId,
            @RequestParam Long targetFighterId) {

        Game game = gameService.makeMove(gameId, attackingFighterId, attackPowerId, targetFighterId);
        return ResponseEntity.ok(game);
    }

    @GetMapping("/{gameId}/status")
    public ResponseEntity<Game> getGameStatus(@PathVariable Long gameId) {
        Game game = gameService.findById(gameId);
        return ResponseEntity.ok(game);
    }
}