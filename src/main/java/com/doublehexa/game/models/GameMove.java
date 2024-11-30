package com.doublehexa.game.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class GameMove {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Game game;

    @ManyToOne
    private Fighter attackingFighter;

    @ManyToOne
    private Power attackPower;

    @ManyToOne
    private Fighter targetFighter;

    @ManyToOne
    private Power defensePower;

    private LocalDateTime createdAt = LocalDateTime.now();
}