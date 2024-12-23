package com.doublehexa.game.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FighterSetup {
    private int position;
    private String name;
    private int points;
    private boolean active;
}