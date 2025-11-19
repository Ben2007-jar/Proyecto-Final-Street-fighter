package com.MBM.KOMaster.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import java.util.Random;

/**
 * Gestor centralizado de todos los sonidos y música del juego
 */
public class SoundManager {
    
    // Música de fondo
    private Music menuMusic;
    private Music characterSelectMusic;
    private Music[] battleMusics; // 4 músicas aleatorias para las batallas
    private Music currentBattleMusic;
    
    // Efectos de sonido
    private Sound punchSound;
    private Sound kickSound;
    private Sound blockSound;
    
    // Control de volumen
    private float musicVolume = 0.015f;
    private float sfxVolume = 0.1f;
    
    private Random random;
    
    public SoundManager() {
        random = new Random();
        loadSounds();
    }
    
    /**
     * Carga todos los archivos de audio
     */
    private void loadSounds() {
        try {
            // ==================== MÚSICA ====================
            // Música del menú principal
            menuMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/menu_music.mp3"));
            menuMusic.setLooping(true);
            menuMusic.setVolume(musicVolume);
            
            // Música de selección de personajes
            characterSelectMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/character_select_music.mp3"));
            characterSelectMusic.setLooping(true);
            characterSelectMusic.setVolume(musicVolume);
            
            // Músicas de batalla (4 canciones diferentes)
            battleMusics = new Music[4];
            battleMusics[0] = Gdx.audio.newMusic(Gdx.files.internal("sounds/battle_music_1.mp3"));
            battleMusics[1] = Gdx.audio.newMusic(Gdx.files.internal("sounds/battle_music_2.mp3"));
            battleMusics[2] = Gdx.audio.newMusic(Gdx.files.internal("sounds/battle_music_3.mp3"));
            battleMusics[3] = Gdx.audio.newMusic(Gdx.files.internal("sounds/battle_music_4.mp3"));
            
            for (Music music : battleMusics) {
                music.setLooping(true);
                music.setVolume(musicVolume);
            }
            
            // ==================== EFECTOS DE SONIDO ====================
            punchSound = Gdx.audio.newSound(Gdx.files.internal("sounds/punch.mp3"));
            kickSound = Gdx.audio.newSound(Gdx.files.internal("sounds/kick.mp3"));
            blockSound = Gdx.audio.newSound(Gdx.files.internal("sounds/block.mp3"));
            
        } catch (Exception e) {
            System.err.println("Error al cargar sonidos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // ==================== MÚSICA DE FONDO ====================
    
    /**
     * Reproduce la música del menú principal (solo si no está sonando)
     */
    public void playMenuMusic() {
        // Si ya está sonando, no hacer nada
        if (menuMusic != null && menuMusic.isPlaying()) {
            return;
        }
        
        stopAllMusic();
        if (menuMusic != null) {
            menuMusic.play();
        }
    }
    
    /**
     * Reproduce la música de selección de personajes
     */
    public void playCharacterSelectMusic() {
        stopAllMusic();
        if (characterSelectMusic != null) {
            characterSelectMusic.play();
        }
    }
    
    /**
     * Reproduce una música de batalla aleatoria
     */
    public void playRandomBattleMusic() {
        stopAllMusic();
        int randomIndex = random.nextInt(battleMusics.length);
        currentBattleMusic = battleMusics[randomIndex];
        if (currentBattleMusic != null) {
            currentBattleMusic.play();
        }
    }
    
    /**
     * Pausa la música de batalla actual
     */
    public void pauseBattleMusic() {
        if (currentBattleMusic != null && currentBattleMusic.isPlaying()) {
            currentBattleMusic.pause();
        }
    }
    
    /**
     * Reanuda la música de batalla actual
     */
    public void resumeBattleMusic() {
        if (currentBattleMusic != null) {
            currentBattleMusic.play();
        }
    }
    
    /**
     * Detiene toda la música
     */
    public void stopAllMusic() {
        if (menuMusic != null && menuMusic.isPlaying()) {
            menuMusic.stop();
        }
        if (characterSelectMusic != null && characterSelectMusic.isPlaying()) {
            characterSelectMusic.stop();
        }
        for (Music music : battleMusics) {
            if (music != null && music.isPlaying()) {
                music.stop();
            }
        }
        currentBattleMusic = null;
    }
    
    // ==================== EFECTOS DE SONIDO ====================
    
    /**
     * Reproduce el sonido de golpe (puño)
     */
    public void playPunchSound() {
        if (punchSound != null) {
            punchSound.play(sfxVolume);
        }
    }
    
    /**
     * Reproduce el sonido de patada
     */
    public void playKickSound() {
        if (kickSound != null) {
            kickSound.play(sfxVolume);
        }
    }
    
    /**
     * Reproduce el sonido de bloqueo
     */
    public void playBlockSound() {
        if (blockSound != null) {
            blockSound.play(sfxVolume);
        }
    }
    
    // ==================== CONTROL DE VOLUMEN ====================
    
    /**
     * Cambia el volumen de la música (0.0 a 1.0)
     */
    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0f, Math.min(1f, volume));
        if (menuMusic != null) menuMusic.setVolume(musicVolume);
        if (characterSelectMusic != null) characterSelectMusic.setVolume(musicVolume);
        for (Music music : battleMusics) {
            if (music != null) music.setVolume(musicVolume);
        }
    }
    
    /**
     * Cambia el volumen de efectos de sonido (0.0 a 1.0)
     */
    public void setSfxVolume(float volume) {
        this.sfxVolume = Math.max(0f, Math.min(1f, volume));
    }
    
    public float getMusicVolume() {
        return musicVolume;
    }
    
    public float getSfxVolume() {
        return sfxVolume;
    }
    
    // ==================== LIMPIEZA ====================
    
    /**
     * Libera todos los recursos de audio
     */
    public void dispose() {
        if (menuMusic != null) menuMusic.dispose();
        if (characterSelectMusic != null) characterSelectMusic.dispose();
        for (Music music : battleMusics) {
            if (music != null) music.dispose();
        }
        if (punchSound != null) punchSound.dispose();
        if (kickSound != null) kickSound.dispose();
        if (blockSound != null) blockSound.dispose();
    }
}