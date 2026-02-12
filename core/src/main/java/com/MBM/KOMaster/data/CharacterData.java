package com.MBM.KOMaster.data;

/**
 * Almacena la información de un personaje del juego.
 * Centraliza todos los datos en un solo lugar.
 */
public class CharacterData {
    
    private final int id;
    private final String name;
    private final String walkSprite;
    private final String hitSprite;
    private final String blockSprite;
    private final String portraitSprite;
    private final float attackRange;
    private final float kickRange;
    private final boolean available;  // Si está desbloqueado o es ???
    
    public CharacterData(int id, String name, String walkSprite, String hitSprite, 
                        String blockSprite, String portraitSprite,
                        float attackRange, float kickRange, boolean available) {
        this.id = id;
        this.name = name;
        this.walkSprite = walkSprite;
        this.hitSprite = hitSprite;
        this.blockSprite = blockSprite;
        this.portraitSprite = portraitSprite;
        this.attackRange = attackRange;
        this.kickRange = kickRange;
        this.available = available;
    }
    
    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getWalkSprite() { return walkSprite; }
    public String getHitSprite() { return hitSprite; }
    public String getBlockSprite() { return blockSprite; }
    public String getPortraitSprite() { return portraitSprite; }
    public float getAttackRange() { return attackRange; }
    public float getKickRange() { return kickRange; }
    public boolean isAvailable() { return available; }
}