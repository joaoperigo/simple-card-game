package com.doublehexa.game.controllers;

import com.doublehexa.game.models.Game;
import com.doublehexa.game.models.GameStatus;
import com.doublehexa.game.services.GameService;
import com.doublehexa.game.dto.GameMoveRequest;
import com.doublehexa.game.dto.GameStateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

@Controller
@RequiredArgsConstructor
public class GameWebSocketController {
    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/game.move")
    @Transactional(readOnly = true)  // Adiciona transação readonly
    public void handleMove(GameMoveRequest moveRequest) {
        try {
            Game game = gameService.makeMove(
                    moveRequest.getGameId(),
                    moveRequest.getAttackingFighterId(),
                    moveRequest.getAttackPowerId(),
                    moveRequest.getTargetFighterId()
            );

            // Não precisa mais forçar carregamento aqui pois já foi feito no service
            GameStateDTO gameState = new GameStateDTO(game, "Move completed");
            messagingTemplate.convertAndSend("/topic/game", gameState);

        } catch (Exception e) {
            // Usa o método que carrega todos os detalhes
            Game game = gameService.findByIdWithDetails(moveRequest.getGameId());
            messagingTemplate.convertAndSend("/topic/game",
                    new GameStateDTO(game, "Error: " + e.getMessage())
            );
        }
    }
}