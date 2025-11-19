package com.MBM.KOMaster.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

public class CharacterSelectScreen implements Screen {

    private Principal game;
    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont fontShadow;
    private BitmapFont titleFont;
    private BitmapFont titleFontShadow;
    private GlyphLayout layout;
    private OrthographicCamera camera;
    private Viewport viewport;

    private final int ROWS = 4;
    private final int COLS = 3;

    private Texture[] characterTextures;
    private Texture cursorTexture;
    private Texture background;

    private int cursorRowP1 = 0;
    private int cursorColP1 = 0;
    private int cursorRowP2 = 0;
    private int cursorColP2 = 1;

    private int selectedP1 = -1;
    private int selectedP2 = -1;

    private boolean confirmedP1 = false;
    private boolean confirmedP2 = false;

    private String[] characterNames = {
        "Amigo.R", "Enano", "???", "???",
        "???", "???", "???", "???",
        "???", "???", "???", "???"
    };

    public CharacterSelectScreen(Principal game) {
        this.game = game;
        this.batch = game.batch;

        // USAR FUENTES DEL PRINCIPAL
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
        characterTextures = new Texture[12];

        background = new Texture("images/selectCharacter.png");

        characterTextures[0] = new Texture("images/player1.png");
        characterTextures[1] = new Texture("images/player2.png");

        cursorTexture = new Texture("images/cursor.png");
        
        // Reproducir música de selección de personajes
        game.soundManager.playCharacterSelectMusic();
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

        // Título
        String title = "ELIGE TU PROFE";
        layout.setText(titleFont, title);
        float titleX = (1280 - layout.width) / 2f;
        float titleY = 680;

        titleFontShadow.draw(batch, title, titleX + 3, titleY - 3);
        titleFont.setColor(Color.BLACK);
        titleFont.draw(batch, title, titleX, titleY);

        float leftPanelX = 125;
        float rightPanelX = 925;
        float panelY = 200;
        float panelWidth = 250;
        float panelHeight = 300;

        // ===== Panel P1 =====
        String textP1 = "Jugador 1";
        layout.setText(font, textP1);
        float textP1X = leftPanelX + (panelWidth - layout.width) / 2f;

        fontShadow.draw(batch, textP1, textP1X + 2, panelY + panelHeight + 40 - 2);
        font.setColor(Color.RED);
        font.draw(batch, textP1, textP1X, panelY + panelHeight + 40);

        if (selectedP1 != -1 && characterTextures[selectedP1] != null) {
            batch.draw(characterTextures[selectedP1], leftPanelX, panelY, panelWidth, panelHeight);
        }

        // Texto de estado P1
        {
            String stateText;
            Color stateColor;

            if (confirmedP1) {
                stateText = "CONFIRMADO";
                stateColor = Color.GREEN;
            } else {
                stateText = "ELIGIENDO";
                stateColor = Color.YELLOW;
            }

            layout.setText(font, stateText);
            float confX = leftPanelX + (panelWidth - layout.width) / 2f;
            float confY = panelY - 30;

            fontShadow.setColor(Color.BLACK);
            fontShadow.draw(batch, stateText, confX + 2, confY - 2);
            font.setColor(stateColor);
            font.draw(batch, stateText, confX, confY);
        }

        // ===== Panel P2 =====
        String textP2 = "Jugador 2";
        layout.setText(font, textP2);
        float textP2X = rightPanelX + (panelWidth - layout.width) / 2f;

        fontShadow.draw(batch, textP2, textP2X + 2, panelY + panelHeight + 40 - 2);
        font.setColor(Color.BLUE);
        font.draw(batch, textP2, textP2X, panelY + panelHeight + 40);

        if (selectedP2 != -1 && characterTextures[selectedP2] != null) {
            batch.draw(characterTextures[selectedP2], rightPanelX, panelY, panelWidth, panelHeight);
        }

        // Texto de estado P2
        {
            String stateText;
            Color stateColor;

            if (confirmedP2) {
                stateText = "CONFIRMADO";
                stateColor = Color.GREEN;
            } else {
                stateText = "ELIGIENDO";
                stateColor = Color.YELLOW;
            }

            layout.setText(font, stateText);
            float confX = rightPanelX + (panelWidth - layout.width) / 2f;
            float confY = panelY - 30;

            fontShadow.setColor(Color.BLACK);
            fontShadow.draw(batch, stateText, confX + 2, confY - 2);
            font.setColor(stateColor);
            font.draw(batch, stateText, confX, confY);
        }

        font.setColor(Color.WHITE);

        float gridStartX = 450;
        float gridStartY = 425;

        float iconWidth = 120;
        float iconHeight = 100;
        float xSpacing = 120;
        float ySpacing = 100;

        // Cursor P1
        int xP1 = (int) (gridStartX + cursorColP1 * xSpacing);
        int yP1 = (int) (gridStartY - cursorRowP1 * ySpacing);
        batch.setColor(1, 0, 0, 1);
        batch.draw(cursorTexture, xP1 + 50, yP1 + 70, iconWidth - 80, iconHeight - 80);

        // Cursor P2
        int xP2 = (int) (gridStartX + cursorColP2 * xSpacing);
        int yP2 = (int) (gridStartY - cursorRowP2 * ySpacing);
        batch.setColor(0, 0, 1, 1);
        batch.draw(cursorTexture, xP2 + 70, yP2 + 70, iconWidth - 80, iconHeight - 80);

        batch.setColor(1, 1, 1, 1);

        batch.end();
    }

    private void handleInput() {
        if (!confirmedP1) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
                cursorRowP1 = (cursorRowP1 - 1 + ROWS) % ROWS;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                cursorRowP1 = (cursorRowP1 + 1) % ROWS;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
                cursorColP1 = (cursorColP1 - 1 + COLS) % COLS;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
                cursorColP1 = (cursorColP1 + 1) % COLS;
            }
        }

        if (!confirmedP2) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                cursorRowP2 = (cursorRowP2 - 1 + ROWS) % ROWS;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                cursorRowP2 = (cursorRowP2 + 1) % ROWS;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                cursorColP2 = (cursorColP2 - 1 + COLS) % COLS;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                cursorColP2 = (cursorColP2 + 1) % COLS;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            int selectedIndex = cursorRowP1 * COLS + cursorColP1;
            if (confirmedP1) {
                confirmedP1 = false;
            } else if (canSelect(selectedIndex, 1)) {
                selectedP1 = selectedIndex;
                confirmedP1 = true;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            int selectedIndex = cursorRowP2 * COLS + cursorColP2;
            if (confirmedP2) {
                confirmedP2 = false;
            } else if (canSelect(selectedIndex, 2)) {
                selectedP2 = selectedIndex;
                confirmedP2 = true;
            }
        }

        if (confirmedP1 && confirmedP2 && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            dispose();
            game.setScreen(new MapSelectScreen(game, selectedP1, selectedP2));
        }
    }

    private boolean canSelect(int idx, int player) {
        if (characterTextures[idx] == null) return false;
        if (characterNames[idx].equals("???")) return false;
        return true;
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
        if(background != null) background.dispose();
        if(characterTextures != null) {
            for (Texture tex : characterTextures) {
                if (tex != null) tex.dispose();
            }
        }
        if(cursorTexture != null) cursorTexture.dispose();
    }
}