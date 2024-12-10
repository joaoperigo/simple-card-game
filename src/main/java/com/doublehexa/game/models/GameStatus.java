package com.doublehexa.game.models;

public enum GameStatus {
    SETUP,      // Jogadores distribuindo pontos
    PLAYING,    // Jogo em andamento
    FINISHED,    // Jogo terminado
    PENDING_DEFENSE,  // Aguardando defensor escolher power
    COMPLETED        // Ataque finalizado
}