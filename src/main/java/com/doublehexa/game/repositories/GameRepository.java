package com.doublehexa.game.repositories;

import com.doublehexa.game.models.Game;
import com.doublehexa.game.models.GameStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Collection;

public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByStatusIn(Collection<GameStatus> statuses);
    List<Game> findByStatus(GameStatus status);
}