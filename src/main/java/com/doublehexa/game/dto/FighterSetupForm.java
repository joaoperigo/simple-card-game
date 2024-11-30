package com.doublehexa.game.dto;

import lombok.Data;
import java.util.List;

@Data
public class FighterSetupForm {
    private List<FighterSetup> fighters;

    @Data
    public static class FighterSetup {
        private String name;
        private int points;
    }
}