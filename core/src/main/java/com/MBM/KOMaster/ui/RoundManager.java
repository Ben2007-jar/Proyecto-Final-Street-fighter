package com.MBM.KOMaster.ui;

/**
 * Gestiona el sistema de rounds de la pelea.
 * Mejor de 3 rounds gana la partida.
 */
public class RoundManager {
    
    private int player1Rounds;      // Rounds ganados por jugador 1
    private int player2Rounds;      // Rounds ganados por jugador 2
    private int currentRound;       // Round actual (1, 2 o 3)
    
    private float roundTimer;       // Tiempo restante del round actual
    private final float MAX_ROUND_TIME = 99f;  // 99 segundos por round
    
    private boolean roundActive;    // Si el round está en curso
    private boolean matchFinished;  // Si la partida terminó (alguien ganó 2 rounds)
    private int matchWinner;        // 0 = ninguno, 1 = player1, 2 = player2
    
    private RoundEndReason lastRoundEndReason;
    private int lastRoundWinner;
    
    // Estados del round
    private float transitionTimer;  // Tiempo de transición entre rounds
    private final float TRANSITION_TIME = 3f;  // 3 segundos de pausa entre rounds
    private boolean inTransition;
    
    public enum RoundEndReason {
        KNOCKOUT,       // Un jugador murió (vida = 0)
        TIME_OUT,       // Se acabó el tiempo
        MATCH_WON       // Alguien ganó el mejor de 3
    }
    
    public RoundManager() {
        player1Rounds = 0;
        player2Rounds = 0;
        currentRound = 1;
        roundTimer = MAX_ROUND_TIME;
        roundActive = true;
        matchFinished = false;
        matchWinner = 0;
        inTransition = false;
        transitionTimer = 0f;
    }
    
    /**
     * Actualiza el temporizador del round
     */
    public void update(float delta) {
        if (inTransition) {
            transitionTimer -= delta;
            if (transitionTimer <= 0) {
                inTransition = false;
                startNewRound();
            }
            return;
        }
        
        if (roundActive && !matchFinished) {
            roundTimer -= delta;
            
            if (roundTimer <= 0) {
                roundTimer = 0;
                roundActive = false;
            }
        }
    }
    
    /**
     * Verifica si algún jugador ganó el round por KO o por tiempo
     * Retorna el ganador del round (1 o 2) o 0 si el round sigue
     */
    public int checkRoundEnd(int player1Health, int player2Health) {
        if (!roundActive || matchFinished || inTransition) {
            return 0;
        }
        
        // Caso 1: Jugador 1 murió (KO)
        if (player1Health <= 0) {
            endRound(2, RoundEndReason.KNOCKOUT);
            return 2;
        }
        
        // Caso 2: Jugador 2 murió (KO)
        if (player2Health <= 0) {
            endRound(1, RoundEndReason.KNOCKOUT);
            return 1;
        }
        
        // Caso 3: Se acabó el tiempo
        if (roundTimer <= 0) {
            int winner = determineWinnerByHealth(player1Health, player2Health);
            endRound(winner, RoundEndReason.TIME_OUT);
            return winner;
        }
        
        return 0;  // El round continúa
    }
    
    /**
     * Determina el ganador por vida cuando se acaba el tiempo
     */
    private int determineWinnerByHealth(int player1Health, int player2Health) {
        if (player1Health > player2Health) {
            return 1;
        } else if (player2Health > player1Health) {
            return 2;
        } else {
            // Empate - lo gana el jugador 2 por defecto (puedes cambiarlo)
            return 2;
        }
    }
    
    /**
     * Finaliza el round actual y otorga el punto
     */
    private void endRound(int winner, RoundEndReason reason) {
        roundActive = false;
        lastRoundWinner = winner;
        lastRoundEndReason = reason;
        
        if (winner == 1) {
            player1Rounds++;
        } else if (winner == 2) {
            player2Rounds++;
        }
        
        // Verificar si alguien ganó el match (mejor de 3)
        if (player1Rounds >= 2) {
            matchFinished = true;
            matchWinner = 1;
        } else if (player2Rounds >= 2) {
            matchFinished = true;
            matchWinner = 2;
        } else {
            // Preparar siguiente round
            inTransition = true;
            transitionTimer = TRANSITION_TIME;
        }
    }
    
    /**
     * Inicia un nuevo round
     */
    private void startNewRound() {
        currentRound++;
        roundTimer = MAX_ROUND_TIME;
        roundActive = true;
    }
    
    /**
     * Reinicia el manager para una nueva partida
     */
    public void reset() {
        player1Rounds = 0;
        player2Rounds = 0;
        currentRound = 1;
        roundTimer = MAX_ROUND_TIME;
        roundActive = true;
        matchFinished = false;
        matchWinner = 0;
        inTransition = false;
        transitionTimer = 0f;
    }
    
    // Getters
    public int getPlayer1Rounds() { return player1Rounds; }
    public int getPlayer2Rounds() { return player2Rounds; }
    public int getCurrentRound() { return currentRound; }
    public float getRoundTimer() { return roundTimer; }
    public boolean isRoundActive() { return roundActive; }
    public boolean isMatchFinished() { return matchFinished; }
    public int getMatchWinner() { return matchWinner; }
    public boolean isInTransition() { return inTransition; }
    public float getTransitionTimer() { return transitionTimer; }
    public RoundEndReason getLastRoundEndReason() { return lastRoundEndReason; }
    public int getLastRoundWinner() { return lastRoundWinner; }
    
    /**
     * Obtiene el tiempo formateado como MM:SS
     */
    public String getFormattedTime() {
        int seconds = (int) roundTimer;
        return String.format("%02d", seconds);
    }
    
    /**
     * Obtiene el mensaje del ganador del round
     */
    public String getRoundWinnerMessage() {
        if (lastRoundWinner == 1) {
            return "¡JUGADOR 1 GANA EL ROUND!";
        } else if (lastRoundWinner == 2) {
            return "¡JUGADOR 2 GANA EL ROUND!";
        }
        return "";
    }
    
    /**
     * Obtiene el mensaje del ganador del match
     */
    public String getMatchWinnerMessage() {
        if (matchWinner == 1) {
            return "¡JUGADOR 1 GANA LA PELEA!";
        } else if (matchWinner == 2) {
            return "¡JUGADOR 2 GANA LA PELEA!";
        }
        return "";
    }
    
    /**
     * Obtiene el motivo de finalización del round
     */
    public String getRoundEndReasonMessage() {
        if (lastRoundEndReason == RoundEndReason.KNOCKOUT) {
            return "K.O.!";
        } else if (lastRoundEndReason == RoundEndReason.TIME_OUT) {
            return "¡TIEMPO AGOTADO!";
        }
        return "";
    }
}