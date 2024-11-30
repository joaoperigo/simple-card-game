package com.doublehexa.game.repositories;

import com.doublehexa.game.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    // Métodos especiais, se necessário
}