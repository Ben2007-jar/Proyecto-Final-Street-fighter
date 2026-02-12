package com.MBM.KOMaster;

import com.MBM.KOMaster.screens.MenuScreen;
import com.MBM.KOMaster.audio.SoundManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class Principal extends Game {
    
    public SpriteBatch batch;
    public BitmapFont font;
    public BitmapFont fontShadow;
    public BitmapFont bigFont;
    public BitmapFont bigFontShadow;
    
    public SoundManager soundManager;
    
    @Override
    public void create() {
        batch = new SpriteBatch();
        
        soundManager = new SoundManager();
        
        // Crear fuentes desde TTF
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/DePixel.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
        p.characters =
                "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "áéíóúñÁÉÍÓÚÑ" +
                "0123456789" +
                ".,:;!?¿¡()\"'-+*/=_<> ";
        
        // Fuente normal
        p.size = 20;
        p.color = Color.WHITE;
        font = generator.generateFont(p);
        p.color = Color.BLACK;
        fontShadow = generator.generateFont(p);
        
        // Fuente grande
        p.size = 40;
        p.color = Color.WHITE;
        bigFont = generator.generateFont(p);
        p.color = Color.BLACK;
        bigFontShadow = generator.generateFont(p);
        
        generator.dispose();
        
        setScreen(new MenuScreen(this));
    }
    
    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        fontShadow.dispose();
        bigFont.dispose();
        bigFontShadow.dispose();
        
        if (soundManager != null) {
            soundManager.dispose();
        }
        
        super.dispose();
    }
}