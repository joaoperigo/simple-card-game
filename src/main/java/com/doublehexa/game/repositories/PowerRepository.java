package com.doublehexa.game.repositories;

import com.doublehexa.game.models.Power;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PowerRepository extends JpaRepository<Power, Long> {
    List<Power> findByOwnerIdAndUsedFalse(Long ownerId);
    List<Power> findByOwnerId(Long ownerId);
}