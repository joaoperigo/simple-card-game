package com.doublehexa.game.controllers;

import com.doublehexa.game.dto.GameMoveResponse;
import com.doublehexa.game.models.Game;
import com.doublehexa.game.services.GameService;
import com.doublehexa.game.dto.GameMoveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameApiController {
    private final GameService gameService;

    @PostMapping("/{gameId}/move")
    public ResponseEntity<GameMoveResponse> makeMove(
            @PathVariable Long gameId,
            @RequestBody GameMoveRequest moveRequest) {

        Game game = gameService.makeMove(
                gameId,
                moveRequest.getAttackingFighterId(),
                moveRequest.getAttackPowerId(),
                moveRequest.getTargetFighterId()
        );

        return ResponseEntity.ok(new GameMoveResponse(game, "Ataque realizado com sucesso"));
    }

    @GetMapping("/{gameId}/status")
    public ResponseEntity<Game> getGameStatus(@PathVariable Long gameId) {
        Game game = gameService.findById(gameId);
        return ResponseEntity.ok(game);
    }

    @PostMapping("/{gameId}/move/{moveId}/defend")
    public ResponseEntity<GameMoveResponse> defendMove(
            @PathVariable Long gameId,
            @PathVariable Long moveId,
            @RequestBody(required = false) Long defensePowerId) {

        Game game = gameService.defendMove(gameId, moveId, defensePowerId);
        return ResponseEntity.ok(new GameMoveResponse(game, "Defesa processada com sucesso"));
    }
}