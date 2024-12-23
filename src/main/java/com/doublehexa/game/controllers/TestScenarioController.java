package com.doublehexa.game.controllers;

import com.doublehexa.game.dto.FighterSetup;
import com.doublehexa.game.models.Game;
import com.doublehexa.game.models.GameFighter;
import com.doublehexa.game.models.Player;
import com.doublehexa.game.models.Power;
import com.doublehexa.game.repositories.GameFighterRepository;
import com.doublehexa.game.repositories.PowerRepository;
import com.doublehexa.game.services.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test-scenarios")
//@Profile("dev")
@RequiredArgsConstructor
public class TestScenarioController {
    private final GameService gameService;
    private final GameFighterRepository gameFighterRepository;
    private final PowerRepository powerRepository;

    @PostMapping("/setup-near-tie")
    public Game setupNearTie(@RequestParam Long gameId) {
        Game game = gameService.findById(gameId);

        // Configura fighters do player1
        updateFighters(game, game.getPlayer1(), List.of(
                new FighterSetup(1, "Fighter1", 3, true),
                new FighterSetup(2, "Fighter2", 2, true),
                new FighterSetup(3, "Fighter3", 0, false),
                new FighterSetup(4, "Fighter4", 0, false)
        ));

        // Configura fighters do player2
        updateFighters(game, game.getPlayer2(), List.of(
                new FighterSetup(1, "Fighter1", 3, true),
                new FighterSetup(2, "Fighter2", 2, true),
                new FighterSetup(3, "Fighter3", 0, false),
                new FighterSetup(4, "Fighter4", 0, false)
        ));

        // Deixa apenas 1 power para cada
        setRemainingPowers(game.getPlayer1(), List.of(3));
        setRemainingPowers(game.getPlayer2(), List.of(3));

        return gameService.save(game);
    }

    @PostMapping("/setup-near-loss")
    public Game setupNearLoss(@RequestParam Long gameId) {
        Game game = gameService.findById(gameId);

        // Configura fighters do player1 (quase perdendo)
        updateFighters(game, game.getPlayer1(), List.of(
                new FighterSetup(1, "Fighter1", 1, true),
                new FighterSetup(2, "Fighter2", 2, true),
                new FighterSetup(3, "Fighter3", 0, false),
                new FighterSetup(4, "Fighter4", 0, false)
        ));

        // Configura fighters do player2 (ganhando)
        updateFighters(game, game.getPlayer2(), List.of(
                new FighterSetup(1, "Fighter1", 4, true),
                new FighterSetup(2, "Fighter2", 3, true),
                new FighterSetup(3, "Fighter3", 0, false),
                new FighterSetup(4, "Fighter4", 0, false)
        ));

        // Deixa poucos powers
        setRemainingPowers(game.getPlayer1(), List.of(2));
        setRemainingPowers(game.getPlayer2(), List.of(4));

        return gameService.save(game);
    }

    @PostMapping("/setup-no-valid-moves")
    public Game setupNoValidMoves(@RequestParam Long gameId) {
        Game game = gameService.findById(gameId);

        // Configura fighters do player1 (quase perdendo)
        updateFighters(game, game.getPlayer1(), List.of(
                new FighterSetup(1, "Fighter1", 1, true),
                new FighterSetup(2, "Fighter2", 1, true),
                new FighterSetup(3, "Fighter3", 6, true),
                new FighterSetup(4, "Fighter4", 8, true)
        ));

        // Configura fighters do player2 (ganhando)
        updateFighters(game, game.getPlayer2(), List.of(
                new FighterSetup(1, "Fighter1", 6, true),
                new FighterSetup(2, "Fighter2", 1, true),
                new FighterSetup(3, "Fighter3", 0, false),
                new FighterSetup(4, "Fighter4", 0, false)
        ));

        // Deixa poucos powers
        setRemainingPowers(game.getPlayer1(), List.of(6, 7, 8));
        setRemainingPowers(game.getPlayer2(), List.of(4,8));

        return gameService.save(game);
    }

    private void updateFighters(Game game, Player player, List<FighterSetup> setups) {
        List<GameFighter> fighters = gameFighterRepository.findByGameAndPlayer(game, player);
        for (int i = 0; i < setups.size(); i++) {
            FighterSetup setup = setups.get(i);
            GameFighter fighter = fighters.get(i);
            fighter.setPoints(setup.getPoints());
            fighter.setActive(setup.isActive());
        }
        gameFighterRepository.saveAll(fighters);
    }

    private void setRemainingPowers(Player player, List<Integer> powerValues) {
        // Marca todos os powers como usados
        for (Power power : player.getPowers()) {
            power.setUsed(true);
        }

        // Desmarca apenas os powers especificados
        for (Integer value : powerValues) {
            player.getPowers().stream()
                    .filter(p -> p.getValue() == value)
                    .findFirst()
                    .ifPresent(p -> p.setUsed(false));
        }
        powerRepository.saveAll(player.getPowers());
    }
}