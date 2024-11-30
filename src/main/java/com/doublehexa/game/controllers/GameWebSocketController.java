package com.doublehexa.game.controllers;

import com.doublehexa.game.models.Game;
import com.doublehexa.game.services.GameService;
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
    public Game handleMove(GameMoveRequest moveRequest) {
        return gameService.makeMove(
                moveRequest.getGameId(),
                moveRequest.getAttackingFighterId(),
                moveRequest.getAttackPowerId(),
                moveRequest.getTargetFighterId()
        );
    }
}