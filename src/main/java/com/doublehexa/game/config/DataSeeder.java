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
    @Bean
    CommandLineRunner initDatabase(
            PlayerRepository playerRepository,
            GameRepository gameRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // Limpar dados existentes (opcional)
            playerRepository.deleteAll();

            // Criar jogador de teste
            Player testPlayer = new Player();
            testPlayer.setUsername("test");
            testPlayer.setEmail("test@example.com");
            testPlayer.setPassword(passwordEncoder.encode("123456"));
            playerRepository.save(testPlayer);

            System.out.println("Test user created: test/123456");

            // Criar segundo jogador de teste
            Player opponent = new Player();
            opponent.setUsername("opponent");
            opponent.setEmail("opponent@example.com");
            opponent.setPassword(passwordEncoder.encode("123456"));
            playerRepository.save(opponent);

            System.out.println("Test user created: opponent/123456");

            // Criar um jogo de teste
            Game testGame = new Game();
            testGame.setPlayer1(testPlayer);
            testGame.setPlayer2(opponent);
            testGame.setStatus(GameStatus.SETUP);
            gameRepository.save(testGame);

            System.out.println("Created game");
        };
    }
}