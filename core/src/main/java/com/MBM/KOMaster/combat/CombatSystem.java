package com.MBM.KOMaster.combat;

import com.MBM.KOMaster.characters.Character;
import com.MBM.KOMaster.audio.SoundManager;

/**
 * Sistema de combate que maneja todas las colisiones y daño entre personajes.
 * Separa la lógica de detección de golpes de la pantalla de juego.
 */
public class CombatSystem {
    
    // Constantes de daño
    private static final int PUNCH_DAMAGE = 10;
    private static final int PUNCH_DAMAGE_BLOCKED = 5;
    private static final int KICK_DAMAGE = 15;
    private static final int KICK_DAMAGE_BLOCKED = 8;
    
    // Constantes de hitbox
    private static final float MAX_VERTICAL_RANGE = 150f;  // NUEVO: Máxima diferencia de altura para golpear
    private static final float CHARACTER_HEIGHT = 250f;    // NUEVO: Altura aproximada del personaje
    
    private final SoundManager soundManager;
    
    // Flags para evitar múltiples hits en un solo ataque
    private boolean player1PunchHit = false;
    private boolean player1KickHit = false;
    private boolean player2PunchHit = false;
    private boolean player2KickHit = false;
    
    public CombatSystem(SoundManager soundManager) {
        this.soundManager = soundManager;
    }
    
    /**
     * Actualiza el sistema de combate, detectando y aplicando todos los ataques
     */
    public void update(Character player1, Character player2) {
        float distanceX = Math.abs(player1.getX() - player2.getX());
        
        // Procesar ataques del jugador 1
        processAttacks(player1, player2, distanceX, true);
        
        // Procesar ataques del jugador 2
        processAttacks(player2, player1, distanceX, false);
    }
    
    /**
     * Procesa los ataques de un atacante hacia un defensor
     * 
     * @param attacker El personaje que ataca
     * @param defender El personaje que defiende
     * @param distanceX Distancia horizontal entre ambos
     * @param isPlayer1 Si el atacante es el jugador 1 (para los flags de hit)
     */
    private void processAttacks(Character attacker, Character defender, float distanceX, boolean isPlayer1) {
        // Procesar golpe normal
        if (attacker.isAttacking()) {
            processPunch(attacker, defender, distanceX, isPlayer1);
        } else {
            // Resetear flag cuando termina el ataque
            if (isPlayer1) player1PunchHit = false;
            else player2PunchHit = false;
        }
        
        // Procesar patada
        if (attacker.isKicking()) {
            processKick(attacker, defender, distanceX, isPlayer1);
        } else {
            // Resetear flag cuando termina el ataque
            if (isPlayer1) player1KickHit = false;
            else player2KickHit = false;
        }
    }
    
    /**
     * Procesa un golpe normal (puño)
     */
    private void processPunch(Character attacker, Character defender, float distanceX, boolean isPlayer1) {
        // Verificar si ya golpeó en este ataque
        boolean alreadyHit = isPlayer1 ? player1PunchHit : player2PunchHit;
        if (alreadyHit) return;
        
        // Verificar si el atacante está mirando hacia el defensor
        if (!isFacingTarget(attacker, defender)) return;
        
        // Verificar si está en rango horizontal
        if (distanceX > attacker.getAttackRange()) return;
        
        // NUEVO: Verificar si está en rango vertical (altura)
        if (!isInVerticalRange(attacker, defender)) return;
        
        // Verificar si el defensor está bloqueando correctamente
        boolean isBlocked = isBlockingCorrectly(defender, attacker);
        
        // Aplicar daño
        int damage = isBlocked ? PUNCH_DAMAGE_BLOCKED : PUNCH_DAMAGE;
        defender.receiveDamage(damage, isBlocked);
        
        // Reproducir sonido
        if (isBlocked) {
            soundManager.playBlockSound();
        } else {
            soundManager.playPunchSound();
        }
        
        // Marcar que ya golpeó
        if (isPlayer1) player1PunchHit = true;
        else player2PunchHit = true;
    }
    
    /**
     * Procesa una patada
     */
    private void processKick(Character attacker, Character defender, float distanceX, boolean isPlayer1) {
        // Verificar si ya golpeó en este ataque
        boolean alreadyHit = isPlayer1 ? player1KickHit : player2KickHit;
        if (alreadyHit) return;
        
        // Verificar si el atacante está mirando hacia el defensor
        if (!isFacingTarget(attacker, defender)) return;
        
        // Verificar si está en rango horizontal
        if (distanceX > attacker.getKickRange()) return;
        
        // NUEVO: Verificar si está en rango vertical (altura)
        if (!isInVerticalRange(attacker, defender)) return;
        
        // Verificar si el defensor está bloqueando correctamente
        boolean isBlocked = isBlockingCorrectly(defender, attacker);
        
        // Aplicar daño
        int damage = isBlocked ? KICK_DAMAGE_BLOCKED : KICK_DAMAGE;
        defender.receiveDamage(damage, isBlocked);
        
        // Reproducir sonido
        if (isBlocked) {
            soundManager.playBlockSound();
        } else {
            soundManager.playKickSound();
        }
        
        // Marcar que ya golpeó
        if (isPlayer1) player1KickHit = true;
        else player2KickHit = true;
    }
    
    /**
     * Verifica si el atacante está mirando hacia el defensor
     */
    private boolean isFacingTarget(Character attacker, Character defender) {
        if (attacker.isFacingRight()) {
            return attacker.getX() < defender.getX();
        } else {
            return attacker.getX() > defender.getX();
        }
    }
    
    /**
     * NUEVO: Verifica si los personajes están a una altura alcanzable
     * Considera el centro vertical de cada personaje para mayor precisión
     */
    private boolean isInVerticalRange(Character attacker, Character defender) {
        // Calcular el centro vertical de cada personaje
        float attackerCenterY = attacker.getY() + (CHARACTER_HEIGHT / 2);
        float defenderCenterY = defender.getY() + (CHARACTER_HEIGHT / 2);
        
        // Calcular la diferencia de altura
        float verticalDistance = Math.abs(attackerCenterY - defenderCenterY);
        
        // Solo golpea si están a una altura razonable
        return verticalDistance <= MAX_VERTICAL_RANGE;
    }
    
    /**
     * Verifica si el defensor está bloqueando en la dirección correcta
     */
    private boolean isBlockingCorrectly(Character defender, Character attacker) {
        if (!defender.isBlocking()) return false;
        
        // El defensor debe estar mirando hacia el atacante
        if (defender.isFacingRight()) {
            return attacker.getX() > defender.getX();
        } else {
            return attacker.getX() < defender.getX();
        }
    }
    
    /**
     * Resetea los flags de golpes (llamar al inicio de cada round)
     */
    public void reset() {
        player1PunchHit = false;
        player1KickHit = false;
        player2PunchHit = false;
        player2KickHit = false;
    }
}