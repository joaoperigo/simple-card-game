package com.doublehexa.game.config;

import com.doublehexa.game.models.*;
import com.doublehexa.game.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(
            PlayerRepository playerRepository,
            GameRepository gameRepository,
            GameFighterRepository gameFighterRepository,
            PowerRepository powerRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // Limpar dados existentes
            gameRepository.deleteAll();
            playerRepository.deleteAll();

            // Criar vários jogadores
            Player player1 = createPlayer("test", "test@example.com", "123456", passwordEncoder);
            Player player2 = createPlayer("test2", "test2@example.com", "123456", passwordEncoder);
            Player player3 = createPlayer("player3", "player3@example.com", "123456", passwordEncoder);
            Player player4 = createPlayer("player4", "player4@example.com", "123456", passwordEncoder);

            List<Player> players = Arrays.asList(player1, player2, player3, player4);
            playerRepository.saveAll(players);

            // Criar alguns jogos em diferentes estados
            Game game1 = createGame(player1, player2, GameStatus.SETUP);
            Game game2 = createGame(player3, player4, GameStatus.PLAYING);
            Game game3 = createGame(player1, player3, GameStatus.PLAYING);

            List<Game> games = Arrays.asList(game1, game2, game3);
            gameRepository.saveAll(games);

            // Criar fighters para o jogo em andamento (game2)
            createFightersForPlayer(game2, player3, gameFighterRepository);
            createFightersForPlayer(game2, player4, gameFighterRepository);

            System.out.println("=== Test Data Created ===");
            System.out.println("Players created: test, test2, player3, player4 (all with password: 123456)");
            System.out.println("Games created: " + games.size());
        };
    }

    private Player createPlayer(String username, String email, String password, PasswordEncoder passwordEncoder) {
        Player player = new Player();
        player.setUsername(username);
        player.setEmail(email);
        player.setPassword(passwordEncoder.encode(password));
        return player;
    }

    private Game createGame(Player player1, Player player2, GameStatus status) {
        Game game = new Game();
        game.setPlayer1(player1);
        game.setPlayer2(player2);
        game.setStatus(status);
        game.setCurrentTurn(status == GameStatus.PLAYING ? player1 : null);
        return game;
    }

    private void createFightersForPlayer(Game game, Player player, GameFighterRepository repo) {
        List<GameFighter> fighters = Arrays.asList(
                createFighter(game, player, "Fighter 1", 4),
                createFighter(game, player, "Fighter 2", 3),
                createFighter(game, player, "Fighter 3", 5),
                createFighter(game, player, "Fighter 4", 4)
        );
        repo.saveAll(fighters);
    }

    private GameFighter createFighter(Game game, Player player, String name, int points) {
        GameFighter fighter = new GameFighter();
        fighter.setGame(game);
        fighter.setPlayer(player);
        fighter.setName(name);
        fighter.setPoints(points);
        fighter.setActive(true);
        return fighter;
    }
}