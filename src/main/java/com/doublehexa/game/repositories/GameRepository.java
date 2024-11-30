package com.doublehexa.game.repositories;

import com.doublehexa.game.models.Game;
import com.doublehexa.game.models.GameStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByStatus(GameStatus status);
    List<Game> findByPlayer1IdOrPlayer2Id(Long player1Id, Long player2Id);
}