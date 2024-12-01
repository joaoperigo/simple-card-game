package com.doublehexa.game.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class GameFighter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Game game;

    @ManyToOne
    private Player player;  // dono do fighter nesta partida

    private String name;
    private int points;
    private boolean active = true;

    @PrePersist
    @PreUpdate
    public void validatePoints() {
        if (points < 1 || points > 8) {
            throw new IllegalArgumentException("Fighter points must be between 1 and 8");
        }
    }
}