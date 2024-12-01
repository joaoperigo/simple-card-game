package com.doublehexa.game.services;

import com.doublehexa.game.models.Power;
import com.doublehexa.game.repositories.PowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PowerService {
    private final PowerRepository powerRepository;

    @Transactional
    public void initializePlayerPowers(Long playerId) {
        List<Power> powers = powerRepository.findByOwnerId(playerId);
        if (powers.isEmpty()) {
            for (int i = 1; i <= 8; i++) {
                Power power = new Power();
                power.setValue(i);
                powerRepository.save(power);
            }
        }
    }

    public List<Power> getAvailablePowers(Long playerId) {
        return powerRepository.findByOwnerIdAndUsedFalse(playerId);
    }
}