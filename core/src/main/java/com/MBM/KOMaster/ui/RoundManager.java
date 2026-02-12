package com.MBM.KOMaster.ui;

/**
 * Gestiona el sistema de rounds de la pelea.
 * Mejor de 3 rounds gana la partida.
 */
public class RoundManager {
    
    private int player1Rounds;
    private int player2Rounds;
    private int currentRound;
    
    private float roundTimer;
    private final float MAX_ROUND_TIME = 99f;
    
    private boolean roundActive;
    private boolean matchFinished;
    private int matchWinner;
    
    private RoundEndReason lastRoundEndReason;
    private int lastRoundWinner;
    
    private float transitionTimer;
    private final float TRANSITION_TIME = 3f;
    private boolean inTransition;
    
    public enum RoundEndReason {
        KNOCKOUT,
        TIME_OUT,
        MATCH_WON
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
    
    public int checkRoundEnd(int player1Health, int player2Health) {
        if (!roundActive || matchFinished || inTransition) {
            return 0;
        }
        
        if (player1Health <= 0) {
            endRound(2, RoundEndReason.KNOCKOUT);
            return 2;
        }
        
        if (player2Health <= 0) {
            endRound(1, RoundEndReason.KNOCKOUT);
            return 1;
        }
        
        if (roundTimer <= 0) {
            int winner = determineWinnerByHealth(player1Health, player2Health);
            endRound(winner, RoundEndReason.TIME_OUT);
            return winner;
        }
        
        return 0;
    }
    
    private int determineWinnerByHealth(int player1Health, int player2Health) {
        if (player1Health > player2Health) {
            return 1;
        } else if (player2Health > player1Health) {
            return 2;
        } else {
            return 2;
        }
    }
    
    private void endRound(int winner, RoundEndReason reason) {
        roundActive = false;
        lastRoundWinner = winner;
        lastRoundEndReason = reason;
        
        if (winner == 1) {
            player1Rounds++;
        } else if (winner == 2) {
            player2Rounds++;
        }
        
        if (player1Rounds >= 2) {
            matchFinished = true;
            matchWinner = 1;
        } else if (player2Rounds >= 2) {
            matchFinished = true;
            matchWinner = 2;
        } else {
            inTransition = true;
            transitionTimer = TRANSITION_TIME;
        }
    }
    
    private void startNewRound() {
        currentRound++;
        roundTimer = MAX_ROUND_TIME;
        roundActive = true;
    }
    
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
    
    public String getFormattedTime() {
        int seconds = (int) roundTimer;
        return String.format("%02d", seconds);
    }
    
    public String getRoundWinnerMessage() {
        if (lastRoundWinner == 1) {
            return "¡JUGADOR 1 GANA EL ROUND!";
        } else if (lastRoundWinner == 2) {
            return "¡JUGADOR 2 GANA EL ROUND!";
        }
        return "";
    }
    
    public String getMatchWinnerMessage() {
        if (matchWinner == 1) {
            return "¡JUGADOR 1 GANA LA PELEA!";
        } else if (matchWinner == 2) {
            return "¡JUGADOR 2 GANA LA PELEA!";
        }
        return "";
    }
    
    public String getRoundEndReasonMessage() {
        if (lastRoundEndReason == RoundEndReason.KNOCKOUT) {
            return "K.O.!";
        } else if (lastRoundEndReason == RoundEndReason.TIME_OUT) {
            return "¡TIEMPO AGOTADO!";
        }
        return "";
    }
}