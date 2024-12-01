package com.doublehexa.game.services;

import com.doublehexa.game.models.*;
import com.doublehexa.game.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final GameFighterRepository gameFighterRepository;
    private final PowerRepository powerRepository;
    private final GameMoveRepository gameMoveRepository;

    public Game findById(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found"));
    }

    @Transactional
    public Game createGame(Long player1Id, Long player2Id) {
        Player player1 = playerRepository.findById(player1Id)
                .orElseThrow(() -> new RuntimeException("Player 1 not found"));
        Player player2 = playerRepository.findById(player2Id)
                .orElseThrow(() -> new RuntimeException("Player 2 not found"));

        Game game = new Game();
        game.setPlayer1(player1);
        game.setPlayer2(player2);
        game.setStatus(GameStatus.SETUP);

        return gameRepository.save(game);
    }

    @Transactional
    public void setupFighters(Game game, Player player, List<GameFighter> fighters) {
        // Validar se é o momento de setup
        if (game.getStatus() != GameStatus.SETUP) {
            throw new IllegalStateException("Game is not in setup phase");
        }

        // Validar número de fighters
        if (fighters.size() != 4) {
            throw new IllegalArgumentException("Must have exactly 4 fighters");
        }

        // Validar total de pontos
        int totalPoints = fighters.stream()
                .mapToInt(GameFighter::getPoints)
                .sum();

        if (totalPoints != 16) {
            throw new IllegalArgumentException("Total points must be 16");
        }

        // Validar pontos individuais
        fighters.forEach(fighter -> {
            if (fighter.getPoints() < 1 || fighter.getPoints() > 8) {
                throw new IllegalArgumentException("Each fighter must have between 1 and 8 points");
            }
            fighter.setGame(game);
            fighter.setPlayer(player);
        });

        gameFighterRepository.saveAll(fighters);

        // Verificar se ambos os jogadores já configuraram seus fighters
        if (gameFighterRepository.countByGame(game) == 8) {  // 4 fighters por jogador
            game.setStatus(GameStatus.PLAYING);
            gameRepository.save(game);
        }
    }

    @Transactional
    public Game makeMove(Long gameId, Long attackingFighterId, Long attackPowerId, Long targetFighterId) {
        Game game = findById(gameId);

        // Validar se o jogo está em andamento
        if (game.getStatus() != GameStatus.PLAYING) {
            throw new IllegalStateException("Game is not in playing state");
        }

        GameFighter attacker = gameFighterRepository.findById(attackingFighterId)
                .orElseThrow(() -> new RuntimeException("Attacker not found"));

        Power attackPower = powerRepository.findById(attackPowerId)
                .orElseThrow(() -> new RuntimeException("Attack power not found"));

        GameFighter target = gameFighterRepository.findById(targetFighterId)
                .orElseThrow(() -> new RuntimeException("Target not found"));

        // Validar o poder usado
        if (attackPower.getValue() > attacker.getPoints()) {
            throw new IllegalStateException("Power value cannot be greater than fighter points");
        }

        // Registrar o movimento
        GameMove move = new GameMove();
        move.setGame(game);
        move.setAttackingFighter(attacker);
        move.setAttackPower(attackPower);
        move.setTargetFighter(target);

        gameMoveRepository.save(move);

        // Marcar o poder como usado
        attackPower.setUsed(true);
        powerRepository.save(attackPower);

        return game;
    }

    public boolean isGameOver(Game game) {
        // Verificar se algum jogador perdeu todos os fighters
        long player1ActiveFighters = gameFighterRepository
                .countByGameAndPlayerAndActiveTrue(game, game.getPlayer1());
        long player2ActiveFighters = gameFighterRepository
                .countByGameAndPlayerAndActiveTrue(game, game.getPlayer2());

        return player1ActiveFighters == 0 || player2ActiveFighters == 0;
    }

    @Transactional
    public void finishGame(Game game) {
        game.setStatus(GameStatus.FINISHED);
        gameRepository.save(game);
    }
}