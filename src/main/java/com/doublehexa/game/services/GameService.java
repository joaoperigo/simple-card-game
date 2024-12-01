package com.doublehexa.game.services;

import com.doublehexa.game.models.*;
import com.doublehexa.game.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final FighterRepository fighterRepository;
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
    public Game makeMove(Long gameId, Long attackingFighterId, Long attackPowerId, Long targetFighterId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        Fighter attacker = fighterRepository.findById(attackingFighterId)
                .orElseThrow(() -> new RuntimeException("Attacker not found"));

        Power attackPower = powerRepository.findById(attackPowerId)
                .orElseThrow(() -> new RuntimeException("Attack power not found"));

        Fighter target = fighterRepository.findById(targetFighterId)
                .orElseThrow(() -> new RuntimeException("Target not found"));

        // TODO: Implementar lógica do jogo aqui

        return gameRepository.save(game);
    }
}