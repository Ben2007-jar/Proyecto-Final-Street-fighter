package com.MBM.KOMaster.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import java.util.Random;

/**
 * Gestor centralizado de todos los sonidos y música del juego
 */
public class SoundManager {
    
    private Music menuMusic;
    private Music characterSelectMusic;
    private Music[] battleMusics;
    private Music currentBattleMusic;
    
    private Sound punchSound;
    private Sound kickSound;
    private Sound blockSound;
    
    private float musicVolume = 0.015f;
    private float sfxVolume = 0.1f;
    
    private Random random;
    
    public SoundManager() {
        random = new Random();
        loadSounds();
    }
    
    private void loadSounds() {
        try {
            menuMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/menu_music.mp3"));
            menuMusic.setLooping(true);
            menuMusic.setVolume(musicVolume);
            
            characterSelectMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/character_select_music.mp3"));
            characterSelectMusic.setLooping(true);
            characterSelectMusic.setVolume(musicVolume);
            
            battleMusics = new Music[4];
            battleMusics[0] = Gdx.audio.newMusic(Gdx.files.internal("sounds/battle_music_1.mp3"));
            battleMusics[1] = Gdx.audio.newMusic(Gdx.files.internal("sounds/battle_music_2.mp3"));
            battleMusics[2] = Gdx.audio.newMusic(Gdx.files.internal("sounds/battle_music_3.mp3"));
            battleMusics[3] = Gdx.audio.newMusic(Gdx.files.internal("sounds/battle_music_4.mp3"));
            
            for (Music music : battleMusics) {
                music.setLooping(true);
                music.setVolume(musicVolume);
            }
            
            punchSound = Gdx.audio.newSound(Gdx.files.internal("sounds/punch.mp3"));
            kickSound = Gdx.audio.newSound(Gdx.files.internal("sounds/kick.mp3"));
            blockSound = Gdx.audio.newSound(Gdx.files.internal("sounds/block.mp3"));
            
        } catch (Exception e) {
            System.err.println("Error al cargar sonidos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void playMenuMusic() {
        if (menuMusic != null && menuMusic.isPlaying()) {
            return;
        }
        stopAllMusic();
        if (menuMusic != null) {
            menuMusic.play();
        }
    }
    
    public void playCharacterSelectMusic() {
        stopAllMusic();
        if (characterSelectMusic != null) {
            characterSelectMusic.play();
        }
    }
    
    public void playRandomBattleMusic() {
        stopAllMusic();
        int randomIndex = random.nextInt(battleMusics.length);
        currentBattleMusic = battleMusics[randomIndex];
        if (currentBattleMusic != null) {
            currentBattleMusic.play();
        }
    }
    
    public void pauseBattleMusic() {
        if (currentBattleMusic != null && currentBattleMusic.isPlaying()) {
            currentBattleMusic.pause();
        }
    }
    
    public void resumeBattleMusic() {
        if (currentBattleMusic != null) {
            currentBattleMusic.play();
        }
    }
    
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
    
    public void playPunchSound() {
        if (punchSound != null) {
            punchSound.play(sfxVolume);
        }
    }
    
    public void playKickSound() {
        if (kickSound != null) {
            kickSound.play(sfxVolume);
        }
    }
    
    public void playBlockSound() {
        if (blockSound != null) {
            blockSound.play(sfxVolume);
        }
    }
    
    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0f, Math.min(1f, volume));
        if (menuMusic != null) menuMusic.setVolume(musicVolume);
        if (characterSelectMusic != null) characterSelectMusic.setVolume(musicVolume);
        for (Music music : battleMusics) {
            if (music != null) music.setVolume(musicVolume);
        }
    }
    
    public void setSfxVolume(float volume) {
        this.sfxVolume = Math.max(0f, Math.min(1f, volume));
    }
    
    public float getMusicVolume() {
        return musicVolume;
    }
    
    public float getSfxVolume() {
        return sfxVolume;
    }
    
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