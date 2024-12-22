package com.doublehexa.game.repositories;

import com.doublehexa.game.models.Game;
import com.doublehexa.game.models.GameFighter;
import com.doublehexa.game.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;  // Adicionar este import

public interface GameFighterRepository extends JpaRepository<GameFighter, Long> {
    long countByGame(Game game);
    long countByGameAndPlayerAndActiveTrue(Game game, Player player);
    List<GameFighter> findByGameAndPlayer(Game game, Player player);
    List<GameFighter> findByGameAndPlayerAndActive(Game game, Player player, boolean active);
}