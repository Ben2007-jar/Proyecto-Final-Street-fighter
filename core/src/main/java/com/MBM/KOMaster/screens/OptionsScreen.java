package com.MBM.KOMaster.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Input.Keys;
import com.MBM.KOMaster.Principal;

public class OptionsScreen implements Screen {

    private Principal game;
    private SpriteBatch batch;

    private BitmapFont font;
    private BitmapFont fontShadow;
    private BitmapFont titleFont;
    private BitmapFont titleFontShadow;

    private GlyphLayout layout;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Texture background;

    private final String[] controlsP1 = {
        "Jugador 1:",
        "W - SALTA",
        "A - MOVER IZQUIERDA",
        "D - MOVER DERECHA",
        "S - BLOQUEO",
        "F - GOLPE",
        "G - PATADA",
        "Q - CONFIRMAR SELECCION"
    };

    private final String[] controlsP2 = {
        "Jugador 2:",
        "Flecha Arriba - SALTA",
        "Flecha Izquierda - MOVER IZQUIERDA",
        "Flecha Derecha - MOVER DERECHA",
        "Flecha Abajo - BLOQUEO",
        "K - GOLPE",
        "L - PATADA",
        "ENTER - CONFIRMAR SELECCION"
    };

    public OptionsScreen(Principal game) {
        this.game = game;
        this.batch = game.batch;

        this.font = game.font;
        this.fontShadow = game.fontShadow;
        this.titleFont = game.bigFont;
        this.titleFontShadow = game.bigFontShadow;

        layout = new GlyphLayout();

        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        viewport.apply();
        camera.position.set(1280 / 2f, 720 / 2f, 0);
    }

    @Override
    public void show() { 
        background = new Texture("images/controls.png");
    }

    @Override
    public void render(float delta) {
        handleInput();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        batch.draw(background, 0, 0, 1280, 720);

        String title = "CONTROLES";
        layout.setText(titleFont, title);
        float titleX = (1280 - layout.width) / 2f;
        float titleY = 625;

        titleFontShadow.setColor(Color.BLACK);
        titleFontShadow.draw(batch, title, titleX + 3, titleY - 3);

        titleFont.setColor(Color.BLACK);
        titleFont.draw(batch, title, titleX, titleY);
        
        float startX = 100;
        float startY = 500;
        float lineHeight = 40;

        for (int i = 0; i < controlsP1.length; i++) {
            String text = controlsP1[i];

            fontShadow.setColor(Color.BLACK);
            fontShadow.draw(batch, text, startX + 2, startY - i * lineHeight - 2);

            if (i == 0) font.setColor(Color.RED);
            else font.setColor(Color.WHITE);
            font.draw(batch, text, startX, startY - i * lineHeight);
        }

        float startX2 = 700;
        for (int i = 0; i < controlsP2.length; i++) {
            String text = controlsP2[i];

            fontShadow.setColor(Color.BLACK);
            fontShadow.draw(batch, text, startX2 + 2, startY - i * lineHeight - 2);

            if (i == 0) font.setColor(Color.BLUE);
            else font.setColor(Color.WHITE);
            font.draw(batch, text, startX2, startY - i * lineHeight);
        }
        
        String beginText = "Presiona ESPACIO para comenzar la partida";
        layout.setText(font, beginText);
        float beginX = (1280 - layout.width) / 2f;
        float beginY = 150;

        fontShadow.setColor(Color.BLACK);
        fontShadow.draw(batch, beginText, beginX + 2, beginY - 2);

        font.setColor(Color.WHITE);
        font.draw(batch, beginText, beginX, beginY);

        font.setColor(Color.WHITE);

        String exitText = "Presiona ESC para volver al menú";
        layout.setText(font, exitText);
        float exitX = (1280 - layout.width) / 2f;
        float exitY = 100;

        fontShadow.setColor(Color.BLACK);
        fontShadow.draw(batch, exitText, exitX + 2, exitY - 2);

        font.setColor(Color.GRAY);
        font.draw(batch, exitText, exitX, exitY);

        font.setColor(Color.WHITE);

        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Keys.BACK)) {
            dispose();
            game.setScreen(new MenuScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(1280 / 2f, 720 / 2f, 0);
    }

    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }

    @Override
    public void dispose() {
        if (background != null) background.dispose();
    }
}