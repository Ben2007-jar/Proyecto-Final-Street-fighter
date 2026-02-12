package com.MBM.KOMaster.network;

import java.io.Serializable;

/**
 * Paquete de datos que se envía por la red.
 * Contiene toda la información necesaria para sincronizar el juego.
 */
public class NetworkPacket implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    public enum PacketType {
        CONNECT,           // Solicitud de conexión
        DISCONNECT,        // Desconexión
        CHARACTER_SELECT,  // Selección de personaje
        MAP_SELECT,        // Selección de mapa
        GAME_STATE,        // Estado del juego (posiciones, vida, etc.)
        INPUT_STATE,       // Estado de inputs del jugador
        ROUND_UPDATE,      // Actualización de rounds
        MATCH_END          // Fin de la partida
    }
    
    public PacketType type;
    
    // Datos de conexión
    public int playerId;  // 1 o 2
    
    // Datos de selección
    public int selectedCharacter;
    public int selectedMap;
    
    // Datos de juego
    public float posX;
    public float posY;
    public int health;
    public boolean facingRight;
    public boolean isAttacking;
    public boolean isKicking;
    public boolean isBlocking;
    public boolean isJumping;
    
    // Datos de inputs
    public boolean inputLeft;
    public boolean inputRight;
    public boolean inputJump;
    public boolean inputAttack;
    public boolean inputKick;
    public boolean inputBlock;
    
    // Datos de rounds
    public int player1Rounds;
    public int player2Rounds;
    public int currentRound;
    public float roundTimer;
    
    public NetworkPacket(PacketType type) {
        this.type = type;
    }
}