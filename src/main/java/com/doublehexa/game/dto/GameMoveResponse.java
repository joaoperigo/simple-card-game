package com.doublehexa.game.dto;

import com.doublehexa.game.models.Game;
import com.doublehexa.game.models.GameStatus;
import lombok.Data;

@Data
public class GameMoveResponse {
    private String message;
    private GameStatus status;
    private Long gameId;
    private String currentTurnUsername;
    private boolean success;

    public GameMoveResponse(Game game, String message) {
        this.gameId = game.getId();
        this.status = game.getStatus();
        this.currentTurnUsername = game.getCurrentTurn().getUsername();
        this.message = message;
        this.success = true;
    }
}