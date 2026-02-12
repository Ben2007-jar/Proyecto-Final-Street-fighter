package com.MBM.KOMaster.data;

/**
 * Base de datos de personajes del juego.
 * Centraliza toda la información de los luchadores.
 */
public class CharacterDatabase {
    
    private static final CharacterData[] CHARACTERS = {
        // ID 0: Amigo.R
        new CharacterData(
            0,
            "Amigo.R",
            "images/fighter1_walk.png",
            "images/fighter1_hit.png",
            "images/fighter1_walkblock.png",
            "images/player1.png",
            210f,  // Rango de golpe
            230f,  // Rango de patada
            true   // Disponible
        ),
        
        // ID 1: Enano
        new CharacterData(
            1,
            "Enano",
            "images/fighter2_walk.png",
            "images/fighter2_hit.png",
            "images/fighter2_walkblock.png",
            "images/player2.png",
            250f,  // Rango de golpe
            270f,  // Rango de patada
            true   // Disponible
        ),
        
        // IDs 2-11: Personajes bloqueados (???)
        new CharacterData(2, "???", null, null, null, null, 0, 0, false),
        new CharacterData(3, "???", null, null, null, null, 0, 0, false),
        new CharacterData(4, "???", null, null, null, null, 0, 0, false),
        new CharacterData(5, "???", null, null, null, null, 0, 0, false),
        new CharacterData(6, "???", null, null, null, null, 0, 0, false),
        new CharacterData(7, "???", null, null, null, null, 0, 0, false),
        new CharacterData(8, "???", null, null, null, null, 0, 0, false),
        new CharacterData(9, "???", null, null, null, null, 0, 0, false),
        new CharacterData(10, "???", null, null, null, null, 0, 0, false),
        new CharacterData(11, "???", null, null, null, null, 0, 0, false)
    };
    
    /**
     * Obtiene un personaje por su ID
     */
    public static CharacterData getCharacter(int id) {
        if (id >= 0 && id < CHARACTERS.length) {
            return CHARACTERS[id];
        }
        return null;
    }
    
    /**
     * Obtiene todos los personajes
     */
    public static CharacterData[] getAllCharacters() {
        return CHARACTERS;
    }
    
    /**
     * Obtiene la cantidad total de personajes
     */
    public static int getCharacterCount() {
        return CHARACTERS.length;
    }
}