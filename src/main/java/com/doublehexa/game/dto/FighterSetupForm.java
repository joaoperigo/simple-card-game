package com.doublehexa.game.dto;

import com.doublehexa.game.models.GameFighter;
import lombok.Data;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Data
public class FighterSetupForm {
    private List<FighterData> fighters = new ArrayList<>();

    @Data
    public static class FighterData {
        private String name;
        private int points;
    }

    public List<GameFighter> toGameFighters() {
        return fighters.stream()
                .map(fighterData -> {
                    GameFighter fighter = new GameFighter();
                    fighter.setName(fighterData.getName());
                    fighter.setPoints(fighterData.getPoints());
                    return fighter;
                })
                .collect(Collectors.toList());
    }

    // Validação dos 16 pontos
    public boolean isValid() {
        int totalPoints = fighters.stream()
                .mapToInt(FighterData::getPoints)
                .sum();

        boolean hasValidPoints = fighters.stream()
                .allMatch(f -> f.getPoints() >= 1 && f.getPoints() <= 8);

        return totalPoints == 16 && fighters.size() == 4 && hasValidPoints;
    }
}