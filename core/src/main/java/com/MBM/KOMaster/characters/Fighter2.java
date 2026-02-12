package com.MBM.KOMaster.characters;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.MBM.KOMaster.input.InputController;

/**
 * Personaje Enano
 */
public class Fighter2 extends Character {
    
    // Constantes específicas de Fighter2
    private static final float ATTACK_RANGE = 250f;
    private static final float KICK_RANGE = 270f;
    
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> attackAnimation;
    private Animation<TextureRegion> kickAnimation;
    private Animation<TextureRegion> blockIdleAnimation;
    private Animation<TextureRegion> blockWalkAnimation;
    
    private float stateTime = 0f;
    private boolean isMoving = false;

    public Fighter2(float x, float y, int health, InputController controller) {
        super(x, y, health, controller);
        setFacingRight(false);
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

        updateActionCooldown(delta);

        if (controller.isMovingLeft()) {
            x -= MOVE_SPEED * delta;
            isMoving = true;
            setFacingRight(false);
        }
        if (controller.isMovingRight()) {
            x += MOVE_SPEED * delta;
            isMoving = true;
            setFacingRight(true);
        }

        if (controller.isJumpPressed()) {
            jump();
        }

        updateJump(delta);

        if (isAttacking) {
            isBlocking = false;
            isKicking = false;
            
            if (attackAnimation.isAnimationFinished(stateTime)) {
                isAttacking = false;
                startPunchCooldown();
            }
        }
        else if (isKicking) {
            isBlocking = false;
            isAttacking = false;
            
            if (kickAnimation.isAnimationFinished(stateTime)) {
                isKicking = false;
                startKickCooldown();
            }
        }
        else {
            if (controller.isAttackPressed() && canPerformAction() && !controller.isBlocking()) {
                isAttacking = true;
                isBlocking = false;
                isKicking = false;
                stateTime = 0;
            }
            else if (controller.isKickPressed() && canPerformAction() && !controller.isBlocking()) {
                isKicking = true;
                isBlocking = false;
                isAttacking = false;
                stateTime = 0;
            }
            else if (controller.isBlocking() && canPerformAction()) {
                isBlocking = true;
            }
            else if (!controller.isBlocking() && isBlocking) {
                isBlocking = false;
                startPunchCooldown();
            }
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
        } else if (isKicking) {
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
        if (!isFacingRight()) {
            flippedFrame.flip(true, false);
        }

        return flippedFrame;
    }

    @Override
    public float getAttackRange() {
        return ATTACK_RANGE;
    }

    @Override
    public float getKickRange() {
        return KICK_RANGE;
    }
}