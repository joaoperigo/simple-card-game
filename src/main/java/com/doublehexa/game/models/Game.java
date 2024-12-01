package com.doublehexa.game.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Player player1;

    @ManyToOne
    private Player player2;

    @ManyToOne
    private Player currentTurn;

    @Enumerated(EnumType.STRING)
    private GameStatus status = GameStatus.SETUP;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private List<GameMove> moves;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private List<GameFighter> player1Fighters;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private List<GameFighter> player2Fighters;
}