package com.MBM.KOMaster.characters;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.MBM.KOMaster.input.InputController;

public abstract class Character {
    
    // ==================== CONSTANTES ====================
    protected static final int MAX_HEALTH = 100;
    protected static final float MOVE_SPEED = 200f;
    protected static final float GRAVITY = -8000f;
    protected static final float JUMP_VELOCITY = 2250f;
    protected static final float FLOOR_Y = 100f;
    protected static final float PUNCH_COOLDOWN_TIME = 0.5f;
    protected static final float KICK_COOLDOWN_TIME = 1.0f;
    
    // ==================== ATRIBUTOS ====================
    public int health;  // MODIFICADO: público para acceso de red
    protected float x, y;
    private boolean facingRight = true;
    
    // Estados
    public boolean isBlocking;   // MODIFICADO: público para acceso de red
    public boolean isAttacking;  // MODIFICADO: público para acceso de red
    public boolean isKicking;    // MODIFICADO: público para acceso de red
    
    // Salto
    public boolean isJumping = false;  // MODIFICADO: público para acceso de red
    protected float velocityY = 0f;
    
    // Input
    protected InputController controller;
    
    // Cooldown
    protected float actionCooldown = 0f;
    
    // ==================== CONSTRUCTOR ====================
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
    
    // ==================== MÉTODOS ABSTRACTOS ====================
    public abstract void update(float delta);
    public abstract TextureRegion getCurrentFrame();
    public abstract float getAttackRange();
    public abstract float getKickRange();
    
    // ==================== MÉTODOS DE MOVIMIENTO ====================
    
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
    
    public void jump() {
        if (!isJumping) {
            isJumping = true;
            velocityY = JUMP_VELOCITY;
        }
    }
    
    // ==================== SISTEMA DE COOLDOWN ====================
    
    protected void updateActionCooldown(float delta) {
        if (actionCooldown > 0) {
            actionCooldown -= delta;
            if (actionCooldown < 0) {
                actionCooldown = 0;
            }
        }
    }
    
    protected boolean canPerformAction() {
        return actionCooldown <= 0;
    }
    
    protected void startPunchCooldown() {
        actionCooldown = PUNCH_COOLDOWN_TIME;
    }
    
    protected void startKickCooldown() {
        actionCooldown = KICK_COOLDOWN_TIME;
    }
    
    // ==================== VIDA Y DAÑO ====================
    
    public void receiveDamage(int damage, boolean isBlockValid) {
        if (isBlockValid) {
            damage = damage / 2;
        }
        health -= damage;
        if (health < 0) health = 0;
    }
    
    public void heal(int amount) {
        health += amount;
        if (health > MAX_HEALTH) {
            health = MAX_HEALTH;
        }
    }
    
    public boolean isDead() {
        return health <= 0;
    }
    
    // ==================== GETTERS ====================
    
    public int getHealth() { return health; }
    public float getX() { return x; }
    public float getY() { return y; }
    public boolean isAttacking() { return isAttacking; }
    public boolean isKicking() { return isKicking; }
    public boolean isBlocking() { return isBlocking; }
    public boolean isFacingRight() { return facingRight; }
    
    // ==================== SETTERS ====================
    
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }
}