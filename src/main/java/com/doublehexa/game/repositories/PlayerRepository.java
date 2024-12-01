package com.doublehexa.game.repositories;

import com.doublehexa.game.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByUsername(String username);
    Optional<Player> findByEmail(String email);
    List<Player> findByIdNot(Long playerId);
}