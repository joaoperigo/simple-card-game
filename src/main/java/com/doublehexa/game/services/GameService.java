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

        // Aplica o dano
        int damage = attackPower.getValue();
        int newPoints = target.getPoints() - damage;
        target.setPoints(Math.max(0, newPoints));

        if (newPoints <= 0) {
            target.setActive(false);

            // Se houver excesso de dano, dá pontos para o atacante
            int excess = Math.abs(newPoints);
            attacker.setPoints(attacker.getPoints() + excess);
        }

        // Salva as alterações nos fighters
        gameFighterRepository.save(attacker);
        gameFighterRepository.save(target);

        // Marca o power como usado
        attackPower.setUsed(true);
        powerRepository.save(attackPower);

        // Registra o movimento
        GameMove move = new GameMove();
        move.setGame(game);
        move.setAttackingFighter(attacker);
        move.setAttackPower(attackPower);
        move.setTargetFighter(target);
//        move.setStatus(MoveStatus.COMPLETED);
        move.setStatus(MoveStatus.PENDING_DEFENSE);
        gameMoveRepository.save(move);

        // Muda o turno
        game.setCurrentTurn(game.getCurrentTurn().equals(game.getPlayer1()) ?
                game.getPlayer2() : game.getPlayer1());

        return gameRepository.save(game);
    }

    @Transactional
    public Game registerAttack(Long gameId, Long attackingFighterId, Long attackPowerId, Long targetFighterId) {
        Game game = findById(gameId);

        GameFighter attacker = gameFighterRepository.findById(attackingFighterId)
                .orElseThrow(() -> new RuntimeException("Attacker not found"));

        Power attackPower = powerRepository.findById(attackPowerId)
                .orElseThrow(() -> new RuntimeException("Attack power not found"));

        GameFighter target = gameFighterRepository.findById(targetFighterId)
                .orElseThrow(() -> new RuntimeException("Target not found"));

        // Marca o power como usado
        attackPower.setUsed(true);
        powerRepository.save(attackPower);

        // Registra o movimento como pendente
        GameMove move = new GameMove();
        move.setGame(game);
        move.setAttackingFighter(attacker);
        move.setAttackPower(attackPower);
        move.setTargetFighter(target);
        move.setStatus(MoveStatus.PENDING_DEFENSE);
        gameMoveRepository.save(move);

        // Não muda o turno aqui!

        return gameRepository.save(game);
    }

    @Transactional
    public Game defendMove(Long gameId, Long moveId, Long defensePowerId) {
        Game game = findById(gameId);
        GameMove move = gameMoveRepository.findById(moveId)
                .orElseThrow(() -> new RuntimeException("Movimento não encontrado"));

        if (move.getStatus() != MoveStatus.PENDING_DEFENSE) {
            throw new IllegalStateException("Este movimento não está aguardando defesa");
        }

        int attackValue = move.getAttackPower().getValue();
        int defenseValue = 0;

        // Se escolheu defender com power
        if (defensePowerId != null) {
            Power defensePower = powerRepository.findById(defensePowerId)
                    .orElseThrow(() -> new RuntimeException("Power de defesa não encontrado"));

            if (defensePower.getValue() > move.getTargetFighter().getPoints()) {
                throw new IllegalStateException("Power maior que os pontos do fighter");
            }

            move.setDefensePower(defensePower);
            defensePower.setUsed(true);
            powerRepository.save(defensePower);
            defenseValue = defensePower.getValue();
        }

        // Calcula resultado do combate
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
            int excessDamage = Math.abs(damage);
            attacker.setPoints(Math.max(0, attacker.getPoints() - excessDamage));
            if (attacker.getPoints() <= 0) {
                attacker.setActive(false);
            }
            gameFighterRepository.save(attacker);
        }

        // Finaliza o movimento
        move.setStatus(MoveStatus.COMPLETED);
        gameMoveRepository.save(move);

        // Agora sim, muda o turno após a defesa
        Player nextPlayer = move.getTargetFighter().getPlayer();
        game.setCurrentTurn(nextPlayer);

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