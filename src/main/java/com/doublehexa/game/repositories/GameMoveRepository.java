package com.doublehexa.game.repositories;

import com.doublehexa.game.models.GameMove;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GameMoveRepository extends JpaRepository<GameMove, Long> {
    List<GameMove> findByGameIdOrderByCreatedAtDesc(Long gameId);
}