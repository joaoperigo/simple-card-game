package com.doublehexa.game.dto;

import com.doublehexa.game.models.Game;
import com.doublehexa.game.models.GameFighter;
import com.doublehexa.game.models.GameStatus;
import com.doublehexa.game.models.Power;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class GameStateDTO {
    private Long gameId;
    private GameStatus status;
    private String currentTurnUsername;
    private List<FighterDTO> player1Fighters;
    private List<FighterDTO> player2Fighters;
    private List<PowerDTO> availablePowers;
    private String message;

    // Construtor principal
    public GameStateDTO(Game game, String message) {
        this.gameId = game.getId();
        this.status = game.getStatus();
        this.currentTurnUsername = game.getCurrentTurn().getUsername();
        this.message = message;

        // Filtra fighters por jogador corretamente
        List<GameFighter> p1Fighters = game.getPlayer1Fighters().stream()
                .filter(f -> f.getPlayer().getId().equals(game.getPlayer1().getId()))
                .collect(Collectors.toList());

        List<GameFighter> p2Fighters = game.getPlayer2Fighters().stream()
                .filter(f -> f.getPlayer().getId().equals(game.getPlayer2().getId()))
                .collect(Collectors.toList());

        this.player1Fighters = p1Fighters.stream()
                .map(FighterDTO::new)
                .collect(Collectors.toList());

        this.player2Fighters = p2Fighters.stream()
                .map(FighterDTO::new)
                .collect(Collectors.toList());

        // Adiciona powers disponíveis
        this.availablePowers = game.getCurrentTurn().getPowers().stream()
                .filter(p -> !p.isUsed())
                .map(PowerDTO::new)
                .collect(Collectors.toList());
    }


    @Data
    @NoArgsConstructor
    public static class FighterDTO {
        private Long id;
        private String name;
        private int points;
        private boolean active;

        public FighterDTO(GameFighter fighter) {
            this.id = fighter.getId();
            this.name = fighter.getName();
            this.points = fighter.getPoints();
            this.active = fighter.isActive();
        }
    }

    @Data
    @NoArgsConstructor
    public static class PowerDTO {
        private Long id;
        private int value;
        private boolean used;

        public PowerDTO(Power power) {
            this.id = power.getId();
            this.value = power.getValue();
            this.used = power.isUsed();
        }
    }
}