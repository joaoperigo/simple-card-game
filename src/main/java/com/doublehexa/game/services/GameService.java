package com.doublehexa.game.services;

import com.doublehexa.game.models.*;
import com.doublehexa.game.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final GameFighterRepository gameFighterRepository;
    private final PowerRepository powerRepository;
    private final GameMoveRepository gameMoveRepository;
    private final PowerService powerService;

    @PersistenceContext
    private EntityManager entityManager;

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

        gameFighterRepository.saveAll(fighters);

        // Verifica se ambos os jogadores já configuraram seus fighters
        if (gameFighterRepository.countByGame(game) == 8) {  // 4 fighters por jogador
            game.setStatus(GameStatus.PLAYING);

            // Define aleatoriamente quem começa
            Random random = new Random();
            game.setCurrentTurn(random.nextBoolean() ? game.getPlayer1() : game.getPlayer2());

            // Inicializa powers para ambos os jogadores
            powerService.initializePlayerPowers(game.getPlayer1());
            powerService.initializePlayerPowers(game.getPlayer2());

            gameRepository.save(game);
        }
    }

    @Transactional(readOnly = true)
    public Game findByIdWithDetails(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        // Força carregamento de todas as coleções necessárias
        game.getPlayer1Fighters().size();
        game.getPlayer2Fighters().size();
        game.getPlayer1().getPowers().size();
        game.getPlayer2().getPowers().size();

        return game;
    }


    @Transactional
    public Game makeMove(Long gameId, Long attackingFighterId, Long attackPowerId, Long targetFighterId) {
        Game game = findById(gameId);

        GameFighter attacker = gameFighterRepository.findById(attackingFighterId)
                .orElseThrow(() -> new RuntimeException("Attacker not found"));

        Power attackPower = powerRepository.findById(attackPowerId)
                .orElseThrow(() -> new RuntimeException("Attack power not found"));

        GameFighter target = gameFighterRepository.findById(targetFighterId)
                .orElseThrow(() -> new RuntimeException("Target not found"));

        // Validações
        if (!attacker.isActive() || !target.isActive()) {
            throw new IllegalStateException("Fighter inativo não pode participar do combate");
        }

        if (attackPower.getValue() > attacker.getPoints()) {
            throw new IllegalStateException("Power maior que os pontos do fighter");
        }

        // Registra o movimento (sem aplicar dano ainda)
        GameMove move = new GameMove();
        move.setGame(game);
        move.setAttackingFighter(attacker);
        move.setAttackPower(attackPower);
        move.setTargetFighter(target);
        gameMoveRepository.save(move);

        // Marca o power do ataque como usado
        attackPower.setUsed(true);
        powerRepository.save(attackPower);

        // Muda o turno para o defensor poder escolher a defesa
        Player defender = attacker.getPlayer().equals(game.getPlayer1()) ?
                game.getPlayer2() : game.getPlayer1();
        game.setCurrentTurn(defender);

        return gameRepository.save(game);
    }

    @Transactional
    public Game defendMove(Long gameId, Long moveId, Long defensePowerId) {
        Game game = findById(gameId);
        GameMove move = gameMoveRepository.findById(moveId)
                .orElseThrow(() -> new RuntimeException("Move not found"));

        // Se defensePowerId for null, significa que o defensor escolheu não usar power
        if (defensePowerId != null) {
            Power defensePower = powerRepository.findById(defensePowerId)
                    .orElseThrow(() -> new RuntimeException("Defense power not found"));

            if (defensePower.getValue() > move.getTargetFighter().getPoints()) {
                throw new IllegalStateException("Power maior que os pontos do fighter");
            }

            move.setDefensePower(defensePower);
            defensePower.setUsed(true);
            powerRepository.save(defensePower);
        }

        // Calcula dano
        int attackValue = move.getAttackPower().getValue();
        int defenseValue = move.getDefensePower() != null ?
                move.getDefensePower().getValue() : 0;

        int damage = attackValue - defenseValue;

        if (damage > 0) {
            // Defensor toma dano
            GameFighter target = move.getTargetFighter();
            target.setPoints(Math.max(0, target.getPoints() - damage));
            if (target.getPoints() <= 0) {
                target.setActive(false);
            }
            gameFighterRepository.save(target);
        } else if (damage < 0) {
            // Atacante toma dano do excesso de defesa
            GameFighter attacker = move.getAttackingFighter();
            attacker.setPoints(Math.max(0, attacker.getPoints() + damage));
            if (attacker.getPoints() <= 0) {
                attacker.setActive(false);
            }
            gameFighterRepository.save(attacker);
        }

        // Muda o turno para o próximo atacante
        Player nextAttacker = game.getCurrentTurn().equals(game.getPlayer1()) ?
                game.getPlayer2() : game.getPlayer1();
        game.setCurrentTurn(nextAttacker);

        return gameRepository.save(game);
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

    public boolean hasPlayerSetupFighters(Game game, Player player) {
        List<GameFighter> fighters = gameFighterRepository.findByGameAndPlayer(game, player);
        return !fighters.isEmpty();  // Se tem fighters, já fez setup
    }

    public List<Game> findActiveGames() {
        return gameRepository.findByStatusIn(Arrays.asList(GameStatus.SETUP, GameStatus.PLAYING));
    }
}