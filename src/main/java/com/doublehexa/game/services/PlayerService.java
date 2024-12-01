package com.doublehexa.game.services;

import com.doublehexa.game.models.Player;
import com.doublehexa.game.repositories.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;

    public Player findById(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Player not found"));
    }

    public Player findByUsername(String username) {
        return playerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Player not found"));
    }

    @Transactional
    public Player createPlayer(Player player) {
        return playerRepository.save(player);
    }

    public List<Player> findAllPlayers() {
        return playerRepository.findAll();
    }

    public List<Player> findAllPlayersExcept(Player currentPlayer) {
        return playerRepository.findByIdNot(currentPlayer.getId());
    }
}