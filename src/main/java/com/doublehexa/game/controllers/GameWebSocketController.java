package com.doublehexa.game.controllers;

import com.doublehexa.game.models.Game;
import com.doublehexa.game.services.GameService;
import com.doublehexa.game.dto.GameMoveRequest;
import com.doublehexa.game.dto.GameStateDTO;
import com.doublehexa.game.dto.DefenseRequest;
import com.doublehexa.game.dto.PassTurnRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class GameWebSocketController {
    private final GameService gameService;

    @MessageMapping("/game.move")
    @SendTo("/topic/game")
    @Transactional
    public GameStateDTO handleMove(GameMoveRequest moveRequest) {
        System.out.println("Recebido movimento para jogo: " + moveRequest.getGameId());
        try {
            Game game = gameService.registerAttack(
                    moveRequest.getGameId(),
                    moveRequest.getAttackingFighterId(),
                    moveRequest.getAttackPowerId(),
                    moveRequest.getTargetFighterId()
            );
            return new GameStateDTO(game, "Ataque realizado!");
        } catch (Exception e) {
            return new GameStateDTO(
                    gameService.findById(moveRequest.getGameId()),
                    "Erro no ataque: " + e.getMessage()
            );
        }
    }

    @MessageMapping("/game.defend")
    @SendTo("/topic/game")
    @Transactional
    public GameStateDTO handleDefense(DefenseRequest defenseRequest) {
        System.out.println("Recebida defesa para jogo: " + defenseRequest.getGameId());
        try {
            Game game = gameService.defendMove(
                    defenseRequest.getGameId(),
                    defenseRequest.getMoveId(),
                    defenseRequest.getDefensePowerId()
            );
            return new GameStateDTO(game, "Defesa processada!");
        } catch (Exception e) {
            return new GameStateDTO(
                    gameService.findById(defenseRequest.getGameId()),
                    "Erro na defesa: " + e.getMessage()
            );
        }
    }

    @MessageMapping("/game.pass-turn")
    @SendTo("/topic/game")
    @Transactional
    public GameStateDTO handlePassTurn(PassTurnRequest passTurnRequest) {
        try {
            Game game = gameService.passTurn(passTurnRequest.getGameId());
            return new GameStateDTO(game, "Turno passado!");
        } catch (Exception e) {
            return new GameStateDTO(
                    gameService.findById(passTurnRequest.getGameId()),
                    "Erro ao passar turno: " + e.getMessage()
            );
        }
    }
}