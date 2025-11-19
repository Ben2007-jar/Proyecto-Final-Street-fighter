package com.MBM.KOMaster.characters;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.MBM.KOMaster.input.InputController;

/**
 * Personaje Enano - Puede ser controlado por cualquier jugador
 */
public class Fighter2 extends Character {
    
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> attackAnimation;
    private Animation<TextureRegion> kickAnimation;  // NUEVO
    private Animation<TextureRegion> blockIdleAnimation;
    private Animation<TextureRegion> blockWalkAnimation;
    private float stateTime = 0f;
    private boolean isMoving = false;

    public Fighter2(float x, float y, int health, InputController controller) {
        super(x, y, health, controller);
        facingRight = false;
        loadAnimations();
    }

    private void loadAnimations() {
        Texture walkSpriteSheet = new Texture("images/fighter2_walk.png");
        TextureRegion[][] tmpWalk = TextureRegion.split(walkSpriteSheet,
            walkSpriteSheet.getWidth() / 3, walkSpriteSheet.getHeight());

        Array<TextureRegion> idleFrames = new Array<>();
        idleFrames.add(tmpWalk[0][0]);
        idleAnimation = new Animation<>(0.2f, idleFrames);

        Array<TextureRegion> walkFrames = new Array<>();
        walkFrames.add(tmpWalk[0][1]);
        walkFrames.add(tmpWalk[0][2]);
        walkAnimation = new Animation<>(0.15f, walkFrames);

        Texture attackSpriteSheet = new Texture("images/fighter2_hit.png");
        TextureRegion[][] tmpAttack = TextureRegion.split(attackSpriteSheet,
            attackSpriteSheet.getWidth() / 2, attackSpriteSheet.getHeight());

        Array<TextureRegion> attackFrames = new Array<>();
        attackFrames.add(tmpAttack[0][0]);
        attackFrames.add(tmpAttack[0][1]);
        attackAnimation = new Animation<>(0.1f, attackFrames, Animation.PlayMode.NORMAL);

        // NUEVO: Cargar animación de patada (usa la misma imagen que hit por ahora)
        Texture kickSpriteSheet = new Texture("images/fighter2_hit.png");
        TextureRegion[][] tmpKick = TextureRegion.split(kickSpriteSheet,
            kickSpriteSheet.getWidth() / 2, kickSpriteSheet.getHeight());

        Array<TextureRegion> kickFrames = new Array<>();
        kickFrames.add(tmpKick[0][0]);
        kickFrames.add(tmpKick[0][1]);
        kickAnimation = new Animation<>(0.15f, kickFrames, Animation.PlayMode.NORMAL);

        Texture blockWalkSpriteSheet = new Texture("images/fighter2_walkblock.png");
        TextureRegion[][] tmpBlockWalk = TextureRegion.split(blockWalkSpriteSheet,
            blockWalkSpriteSheet.getWidth() / 2, blockWalkSpriteSheet.getHeight());

        Array<TextureRegion> blockIdleFrames = new Array<>();
        blockIdleFrames.add(tmpBlockWalk[0][0]);
        blockIdleAnimation = new Animation<>(1f, blockIdleFrames);

        Array<TextureRegion> blockWalkFrames = new Array<>();
        blockWalkFrames.add(tmpBlockWalk[0][0]);
        blockWalkFrames.add(tmpBlockWalk[0][1]);
        blockWalkAnimation = new Animation<>(0.15f, blockWalkFrames);
    }

    @Override
    public void update(float delta) {
        stateTime += delta;
        isMoving = false;

        // Actualizar cooldown de acciones
        updateActionCooldown(delta);

        // Movimiento (siempre permitido)
        if (controller.isMovingLeft()) {
            x -= 200 * delta;
            isMoving = true;
            facingRight = false;
        }
        if (controller.isMovingRight()) {
            x += 200 * delta;
            isMoving = true;
            facingRight = true;
        }

        // Salto (siempre permitido)
        if (controller.isJumpPressed()) {
            jump();
        }

        updateJump(delta);

        // LÓGICA DE COOLDOWN: Solo puede atacar, patear O bloquear, no múltiples a la vez
        
        // Si está atacando (golpe)
        if (isAttacking) {
            isBlocking = false;
            isKicking = false;
            
            if (attackAnimation.isAnimationFinished(stateTime)) {
                isAttacking = false;
                startPunchCooldown();
            }
        }
        // Si está pateando
        else if (isKicking) {
            isBlocking = false;
            isAttacking = false;
            
            if (kickAnimation.isAnimationFinished(stateTime)) {
                isKicking = false;
                startKickCooldown();  // Cooldown más largo
            }
        }
        // Si NO está atacando ni pateando, puede intentar nuevas acciones
        else {
            // Intentar golpe SOLO si no hay cooldown y NO está bloqueando
            if (controller.isAttackPressed() && canPerformAction() && !controller.isBlocking()) {
                isAttacking = true;
                isBlocking = false;
                isKicking = false;
                stateTime = 0;
            }
            // Intentar patada SOLO si no hay cooldown y NO está bloqueando
            else if (controller.isKickPressed() && canPerformAction() && !controller.isBlocking()) {
                isKicking = true;
                isBlocking = false;
                isAttacking = false;
                stateTime = 0;
            }
            // Intentar bloquear solo si no hay cooldown
            else if (controller.isBlocking() && canPerformAction()) {
                isBlocking = true;
            }
            // Si suelta el bloqueo
            else if (!controller.isBlocking() && isBlocking) {
                isBlocking = false;
                startPunchCooldown();
            }
            // Si no presiona nada
            else if (!controller.isBlocking()) {
                isBlocking = false;
            }
        }
    }

    @Override
    public TextureRegion getCurrentFrame() {
        TextureRegion frame;
        
        if (isAttacking) {
            frame = attackAnimation.getKeyFrame(stateTime, false);
        } else if (isKicking) {  // NUEVO: Prioridad a la patada
            frame = kickAnimation.getKeyFrame(stateTime, false);
        } else if (isBlocking && isMoving) {
            frame = blockWalkAnimation.getKeyFrame(stateTime, true);
        } else if (isBlocking) {
            frame = blockIdleAnimation.getKeyFrame(stateTime, true);
        } else if (isMoving) {
            frame = walkAnimation.getKeyFrame(stateTime, true);
        } else {
            frame = idleAnimation.getKeyFrame(stateTime, true);
        }

        TextureRegion flippedFrame = new TextureRegion(frame);
        if (!facingRight) {
            flippedFrame.flip(true, false);
        }

        return flippedFrame;
    }

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

    @Override
    public float getAttackRange() {
        return 250f; // Rango de golpe normal
    }

    @Override
    public float getKickRange() {
        return 270f; // NUEVO: Rango de patada (un poco más largo)
    }
}