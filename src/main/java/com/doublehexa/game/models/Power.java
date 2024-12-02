package com.doublehexa.game.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Power {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "power_value")  // Apenas esta mudança é necessária
    private int value;

    private boolean used = false;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Player owner;

    @PrePersist
    @PreUpdate
    public void validateValue() {
        if (value < 1 || value > 8) {
            throw new IllegalArgumentException("Power value must be between 1 and 8");
        }
    }
}