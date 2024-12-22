package com.doublehexa.game.dto;

import lombok.Data;

@Data
public class DefenseRequest {
    private Long gameId;
    private Long moveId;
    private Long defensePowerId;
}