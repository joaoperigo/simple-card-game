package com.doublehexa.game.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Fighter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int points;
    private boolean active = true;

    @ManyToOne
    private Player owner;

    // Validação de pontos (1-8)
    @PrePersist
    @PreUpdate
    public void validatePoints() {
        if (points < 1 || points > 8) {
            throw new IllegalArgumentException("Fighter points must be between 1 and 8");
        }
    }
}