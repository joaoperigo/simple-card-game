package com.doublehexa.game.config;

import com.doublehexa.game.models.*;
import com.doublehexa.game.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {
    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            // Criar jogadores de teste
            Player player1 = new Player();
            player1.setUsername("player1");
            player1.setEmail("player1@test.com");
            player1.setPassword(passwordEncoder.encode("123456"));
            playerRepository.save(player1);

            Player player2 = new Player();
            player2.setUsername("player2");
            player2.setEmail("player2@test.com");
            player2.setPassword(passwordEncoder.encode("123456"));
            playerRepository.save(player2);

            // Criar um jogo de teste
            Game game = new Game();
            game.setPlayer1(player1);
            game.setPlayer2(player2);
            game.setStatus(GameStatus.SETUP);
            gameRepository.save(game);
        };
    }
}