package com.MBM.KOMaster.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.Color;
import com.MBM.KOMaster.Principal;
import com.MBM.KOMaster.data.MapDatabase;
import com.MBM.KOMaster.data.MapData;
import java.util.Random;

public class MapSelectScreen implements Screen {

    private static final float SELECTION_TIME = 5f;
    private static final float PAUSE_TIME = 3f;
    private static final float SPIN_INTERVAL = 0.5f;
    
    private Principal game;
    private SpriteBatch batch;
    private BitmapFont titleFont;
    private BitmapFont titleFontShadow;
    private GlyphLayout layout;
    private OrthographicCamera camera;
    private Viewport viewport;

    private Texture[] mapTextures;
    private Texture background;

    private float animationTimer = 0f;
    private int currentMapIndex = 0;
    private int finalMap = -1;
    private boolean isAnimating = true;
    private float pauseTimer = 0f;
    private Random random = new Random();

    private int selectedP1Char, selectedP2Char;

    public MapSelectScreen(Principal game, int selectedP1Char, int selectedP2Char) {
        this.game = game;
        this.batch = game.batch;
        this.selectedP1Char = selectedP1Char;
        this.selectedP2Char = selectedP2Char;

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
        int mapCount = MapDatabase.getMapCount();
        mapTextures = new Texture[mapCount];
        background = new Texture("images/selectMap.png");

        // Cargar texturas de mapas
        for (int i = 0; i < mapCount; i++) {
            MapData mapData = MapDatabase.getMap(i);
            if (mapData != null) {
                mapTextures[i] = new Texture(mapData.getBackgroundPath());
            }
        }
    }

    @Override
    public void render(float delta) {
        animationTimer += delta;

        if (isAnimating) {
            if (animationTimer % SPIN_INTERVAL < delta) {
                currentMapIndex = random.nextInt(mapTextures.length);
            }

            if (animationTimer >= SELECTION_TIME) {
                isAnimating = false;
                finalMap = currentMapIndex;
                pauseTimer = 0f;
            }
        } else {
            pauseTimer += delta;
            if (pauseTimer >= PAUSE_TIME) {
                dispose();
                game.setScreen(new GameScreen(game, selectedP1Char, selectedP2Char, finalMap));
                return;
            }
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        batch.draw(background, 0, 0, 1280, 720);

        if (isAnimating) {
            String title = "SELECCIONANDO MAPA...";
            layout.setText(titleFont, title);
            float titleX = (1280 - layout.width) / 2f;
            float titleY = 340;

            titleFontShadow.draw(batch, title, titleX + 3, titleY - 3);
            titleFont.setColor(Color.WHITE);
            titleFont.draw(batch, title, titleX, titleY);

            String progress = "TIEMPO RESTANTE: " + (int) (SELECTION_TIME - animationTimer) + "s";
            layout.setText(titleFont, progress);
            float progressX = (1280 - layout.width) / 2f;
            float progressY = 125;
            titleFont.setColor(Color.WHITE);
            titleFont.draw(batch, progress, progressX, progressY);
        } else {
            String title = "MAPA SELECCIONADO";
            layout.setText(titleFont, title);
            float titleX = (1280 - layout.width) / 2f;
            float titleY = 340;

            titleFontShadow.draw(batch, title, titleX + 3, titleY - 3);
            titleFont.setColor(Color.GREEN);
            titleFont.draw(batch, title, titleX, titleY);

            String progress = "TRANSITANDO EN " + (int) (PAUSE_TIME - pauseTimer) + "s";
            layout.setText(titleFont, progress);
            float progressX = (1280 - layout.width) / 2f;
            float progressY = 125;
            titleFont.setColor(Color.WHITE);
            titleFont.draw(batch, progress, progressX, progressY);
        }

        int mapToShow = isAnimating ? currentMapIndex : finalMap;
        if (mapTextures[mapToShow] != null) {
            float mapX = (1280 - 400) / 2f;
            float mapY = 350;
            batch.draw(mapTextures[mapToShow], mapX, mapY, 400, 300);
        }

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(1280 / 2f, 720 / 2f, 0);
    }

    @Override public void hide() { dispose(); }
    @Override public void pause() { }
    @Override public void resume() { }

    @Override
    public void dispose() {
        if (background != null) background.dispose();
        if (mapTextures != null) {
            for (Texture tex : mapTextures) {
                if (tex != null) tex.dispose();
            }
        }
    }
}