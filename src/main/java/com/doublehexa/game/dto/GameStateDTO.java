package com.doublehexa.game.dto;

import com.doublehexa.game.models.Game;
import lombok.Data;

@Data
public class GameStateDTO {
    private Game game;
    private String message;

    public GameStateDTO(Game game, String message) {
        this.game = game;
        this.message = message;
    }
}