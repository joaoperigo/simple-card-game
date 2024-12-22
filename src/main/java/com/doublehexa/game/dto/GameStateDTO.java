package com.doublehexa.game.dto;

import com.doublehexa.game.models.Game;
import com.doublehexa.game.models.GameFighter;
import com.doublehexa.game.models.GameMove;
import com.doublehexa.game.models.GameStatus;
import com.doublehexa.game.models.Power;
import lombok.Data;
import java.util.List;
import java.util.stream.Collectors;

import com.doublehexa.game.models.MoveStatus;

@Data
public class GameStateDTO {
    private Long gameId;
    private GameStatus status;
    private String currentTurnUsername;
    private List<FighterDTO> player1Fighters;
    private List<FighterDTO> player2Fighters;
    private List<PowerDTO> availablePowers;
    private String message;
    private GameMoveDTO pendingMove;
    private List<PowerDTO> player1Powers;
    private List<PowerDTO> player2Powers;
    private String player1Username;
    private String player2Username;

    public GameStateDTO(Game game, String message) { // Removemos o gameMoveRepository
        this.gameId = game.getId();
        this.status = game.getStatus();
        this.currentTurnUsername = game.getCurrentTurn().getUsername();
        this.player1Username = game.getPlayer1().getUsername();
        this.player2Username = game.getPlayer2().getUsername();
        this.message = message;

        this.player1Fighters = game.getPlayer1Fighters().stream()
                .map(fighter -> new FighterDTO(fighter))
                .collect(Collectors.toList());

        this.player2Fighters = game.getPlayer2Fighters().stream()
                .map(fighter -> new FighterDTO(fighter))
                .collect(Collectors.toList());

        this.availablePowers = game.getCurrentTurn().getPowers().stream()
                .filter(p -> !p.isUsed())
                .map(power -> new PowerDTO(power))
                .collect(Collectors.toList());

//        GameMove pendingMove = gameMoveRepository
//                .findFirstByGameAndStatusOrderByCreatedAtDesc(game, MoveStatus.PENDING_DEFENSE);
//
//        if (pendingMove != null) {
//            this.pendingMove = new GameMoveDTO(pendingMove);
//        }
//        GameMove pendingMove = gameMoveRepository.findFirstByGameAndStatusOrderByCreatedAtDesc(
//                game, MoveStatus.PENDING_DEFENSE);
//
//        if (pendingMove != null) {
//            this.pendingMove = new GameMoveDTO(pendingMove);
//        }

        // Adicionando powers dos jogadores
        this.player1Powers = game.getPlayer1().getPowers().stream()
                .filter(p -> !p.isUsed())
                .map(power -> new PowerDTO(power))
                .collect(Collectors.toList());

        this.player2Powers = game.getPlayer2().getPowers().stream()
                .filter(p -> !p.isUsed())
                .map(power -> new PowerDTO(power))
                .collect(Collectors.toList());


        // Pegamos o movimento pendente através dos moves do game
        this.pendingMove = game.getMoves().stream()
                .filter(move -> move.getStatus() == MoveStatus.PENDING_DEFENSE)
                .findFirst()
                .map(move -> new GameMoveDTO(move))
                .orElse(null);
    }

    @Data
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
    public static class PowerDTO {
        private Long id;
        private int value;
        private String ownerUsername;  // Adicionado

        public PowerDTO(Power power) {
            this.id = power.getId();
            this.value = power.getValue();
            this.ownerUsername = power.getOwner().getUsername();  // Adicionado
        }
    }

    @Data
    public static class GameMoveDTO {
        private Long id;
        private FighterDTO attackingFighter;
        private PowerDTO attackPower;
        private FighterDTO targetFighter;

        public GameMoveDTO(GameMove move) {
            this.id = move.getId();
            this.attackingFighter = new FighterDTO(move.getAttackingFighter());
            this.attackPower = new PowerDTO(move.getAttackPower());
            this.targetFighter = new FighterDTO(move.getTargetFighter());
        }
    }
}