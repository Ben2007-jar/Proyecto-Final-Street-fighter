package com.MBM.KOMaster.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/**
 * Controlador que maneja las entradas de un jugador.
 * Cada jugador tiene su propio conjunto de teclas.
 */
public class InputController {
    
    // Teclas del controlador
    private int keyLeft;
    private int keyRight;
    private int keyJump;
    private int keyAttack;
    private int keyKick;    // NUEVO
    private int keyBlock;
    
    public InputController(int keyLeft, int keyRight, int keyJump, int keyAttack, int keyKick, int keyBlock) {
        this.keyLeft = keyLeft;
        this.keyRight = keyRight;
        this.keyJump = keyJump;
        this.keyAttack = keyAttack;
        this.keyKick = keyKick;
        this.keyBlock = keyBlock;
    }
    
    /**
     * Crea un controlador para el Jugador 1 (WASD + F + G + S)
     */
    public static InputController createPlayer1Controller() {
        return new InputController(
            Input.Keys.A,      // Izquierda
            Input.Keys.D,      // Derecha
            Input.Keys.W,      // Saltar
            Input.Keys.F,      // Atacar (golpe)
            Input.Keys.G,      // Patada
            Input.Keys.S       // Bloquear
        );
    }
    
    /**
     * Crea un controlador para el Jugador 2 (Flechas + K + L + DOWN)
     */
    public static InputController createPlayer2Controller() {
        return new InputController(
            Input.Keys.LEFT,   // Izquierda
            Input.Keys.RIGHT,  // Derecha
            Input.Keys.UP,     // Saltar
            Input.Keys.K,      // Atacar (golpe)
            Input.Keys.L,      // Patada
            Input.Keys.DOWN    // Bloquear
        );
    }
    
    // Métodos para verificar el estado de cada acción
    public boolean isMovingLeft() {
        return Gdx.input.isKeyPressed(keyLeft);
    }
    
    public boolean isMovingRight() {
        return Gdx.input.isKeyPressed(keyRight);
    }
    
    public boolean isJumpPressed() {
        return Gdx.input.isKeyJustPressed(keyJump);
    }
    
    public boolean isAttackPressed() {
        return Gdx.input.isKeyJustPressed(keyAttack);
    }
    
    public boolean isKickPressed() {  // NUEVO
        return Gdx.input.isKeyJustPressed(keyKick);
    }
    
    public boolean isBlocking() {
        return Gdx.input.isKeyPressed(keyBlock);
    }
}