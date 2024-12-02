package com.doublehexa.game.services;

import com.doublehexa.game.models.Player;
import com.doublehexa.game.models.Power;
import com.doublehexa.game.repositories.PowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PowerService {
    private final PowerRepository powerRepository;

    @Transactional
    public void initializePlayerPowers(Player player) {
        List<Power> powers = powerRepository.findByOwnerIdAndUsed(player.getId(), false);
        if (powers.isEmpty()) {
            List<Power> newPowers = new ArrayList<>(); // criado para o log
            for (int i = 1; i <= 8; i++) {
                Power power = new Power();
                power.setValue(i);
                power.setUsed(false);
                power.setOwner(player);
                newPowers.add(power);
            }
            powerRepository.saveAll(newPowers);
            System.out.println("Powers created for player: " + player.getUsername() + " - Count: " + newPowers.size());
        } else {
            System.out.println("Powers already exist for player: " + player.getUsername() + " - Count: " + powers.size());
        }
    }

    public List<Power> getAvailablePowers(Player player) {
        return powerRepository.findByOwnerIdAndUsed(player.getId(), false);
    }
}