package com.doublehexa.game.controllers;

import com.doublehexa.game.models.Game;
import com.doublehexa.game.services.GameService;
import com.doublehexa.game.dto.GameMoveRequest;
import com.doublehexa.game.dto.GameStateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class GameWebSocketController {
    private final GameService gameService;

    @MessageMapping("/game.move")
    @SendTo("/topic/game.{gameId}")
    public GameStateDTO handleMove(GameMoveRequest moveRequest) {
        Game game = gameService.makeMove(
                moveRequest.getGameId(),
                moveRequest.getAttackingFighterId(),
                moveRequest.getAttackPowerId(),
                moveRequest.getTargetFighterId()
        );

        if (game.getStatus() == GameStatus.FINISHED) {
            return new GameStateDTO(game, "Game Over!");
        }

        return new GameStateDTO(game, "Move completed");
    }
}