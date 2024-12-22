package com.doublehexa.game.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

import com.doublehexa.game.models.MoveStatus;


@Data
@Entity
public class GameMove {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Game game;

    @ManyToOne
    private GameFighter attackingFighter;  // Mudou de Fighter para GameFighter

    @ManyToOne
    private Power attackPower;

    @ManyToOne
    private GameFighter targetFighter;     // Mudou de Fighter para GameFighter

    @ManyToOne
    private Power defensePower;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private MoveStatus status = MoveStatus.PENDING_DEFENSE;
}