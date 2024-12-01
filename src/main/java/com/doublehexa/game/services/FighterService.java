package com.doublehexa.game.services;

import com.doublehexa.game.models.Fighter;
import com.doublehexa.game.repositories.FighterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FighterService {
    private final FighterRepository fighterRepository;

    @Transactional
    public Fighter createFighter(Fighter fighter) {
        return fighterRepository.save(fighter);
    }

    public List<Fighter> getActiveFightersByPlayer(Long playerId) {
        return fighterRepository.findByOwnerIdAndActiveTrue(playerId);
    }

    @Transactional
    public void distributePoints(List<Fighter> fighters) {
        int totalPoints = fighters.stream()
                .mapToInt(Fighter::getPoints)
                .sum();

        if (totalPoints != 16) {
            throw new IllegalArgumentException("Total points must be 16");
        }

        fighters.forEach(fighter -> {
            if (fighter.getPoints() < 1 || fighter.getPoints() > 8) {
                throw new IllegalArgumentException("Each fighter must have between 1 and 8 points");
            }
        });

        fighterRepository.saveAll(fighters);
    }
}