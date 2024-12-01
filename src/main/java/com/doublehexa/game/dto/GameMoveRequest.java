package com.doublehexa.game.dto;

import lombok.Data;

@Data
public class GameMoveRequest {
    private Long gameId;
    private Long attackingFighterId;
    private Long attackPowerId;
    private Long targetFighterId;
}