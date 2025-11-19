package com.MBM.KOMaster.characters;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.MBM.KOMaster.input.InputController;

public abstract class Character {
    protected int health;
    protected float x, y;
    protected boolean isBlocking;
    protected boolean isAttacking;
    protected boolean isKicking;  // NUEVO: Estado de patada
    protected InputController controller;
    protected final int MAX_HEALTH = 100;
    
    // Variables para salto
    protected boolean isJumping = false;
    protected float velocityY = 0f;
    protected final float GRAVITY = -8000f;
    protected final float JUMP_VELOCITY = 2250f;
    protected final float FLOOR_Y = 100f;
    public boolean facingRight = true;
    
    // Sistema de cooldown para ataques y bloqueos
    protected float actionCooldown = 0f;
    protected final float PUNCH_COOLDOWN_TIME = 0.5f;  // Cooldown normal para golpes
    protected final float KICK_COOLDOWN_TIME = 1.0f;   // NUEVO: Cooldown más largo para patadas
    
    public Character(float x, float y, int initialHealth, InputController controller) {
        this.x = x;
        this.y = y;
        this.health = initialHealth;
        this.isBlocking = false;
        this.isAttacking = false;
        this.isKicking = false;
        this.y = FLOOR_Y;
        this.controller = controller;
    }
    
    protected void updateJump(float delta) {
        if (isJumping) {
            velocityY += GRAVITY * delta;
            y += velocityY * delta;
            if (y <= FLOOR_Y) {
                y = FLOOR_Y;
                isJumping = false;
                velocityY = 0;
            }
        } else {
            y = FLOOR_Y;
        }
    }
    
    /**
     * Actualiza el cooldown de las acciones (ataque/bloqueo/patada)
     */
    protected void updateActionCooldown(float delta) {
        if (actionCooldown > 0) {
            actionCooldown -= delta;
            if (actionCooldown < 0) {
                actionCooldown = 0;
            }
        }
    }
    
    /**
     * Verifica si puede realizar una acción (atacar, patear o bloquear)
     */
    protected boolean canPerformAction() {
        return actionCooldown <= 0;
    }
    
    /**
     * Inicia el cooldown después de un golpe normal
     */
    protected void startPunchCooldown() {
        actionCooldown = PUNCH_COOLDOWN_TIME;
    }
    
    /**
     * Inicia el cooldown después de una patada (más largo)
     */
    protected void startKickCooldown() {
        actionCooldown = KICK_COOLDOWN_TIME;
    }
    
    public void jump() {
        if (!isJumping) {
            isJumping = true;
            velocityY = JUMP_VELOCITY;
        }
    }
    
    public abstract void update(float delta);
    public abstract TextureRegion getCurrentFrame();
    public abstract void setX(float x);
    public abstract void setY(float y);
    public abstract float getAttackRange();
    public abstract float getKickRange();  // NUEVO: Rango de patada
    
    public void receiveDamage(int damage, boolean isBlockValid) {
        if (isBlockValid) {
            damage = damage / 2;
        }
        health -= damage;
        if (health < 0) health = 0;
    }
    
    public void heal(int amount) {
        health += amount;
        if (health > MAX_HEALTH)
            health = MAX_HEALTH;
    }
    
    public int getHealth() {
        return health;
    }
    
    public float getX() { return x; }
    public float getY() { return y; }
    
    public boolean isDead() {
        return health <= 0;
    }
    
    public boolean isAttacking() { return isAttacking; }
    public boolean isKicking() { return isKicking; }  // NUEVO
    public boolean isBlocking() { return isBlocking; }
}