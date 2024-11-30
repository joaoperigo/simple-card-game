package com.doublehexa.game.repositories;

import com.doublehexa.game.models.Fighter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FighterRepository extends JpaRepository<Fighter, Long> {
    List<Fighter> findByOwnerId(Long ownerId);
    List<Fighter> findByOwnerIdAndActiveTrue(Long ownerId);
}