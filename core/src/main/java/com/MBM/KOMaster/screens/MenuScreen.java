package com.MBM.KOMaster.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.MBM.KOMaster.Principal;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class MenuScreen implements Screen {

    private Principal game;
    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont fontShadow;
    private OrthographicCamera camera;
    private Viewport viewport;
    private GlyphLayout layout;

    private Texture background;

    private int selectedOption = 0;
    private final String[] menuOptions = {"Juego local", "Juego en red", "Opciones", "Salir"};

    public MenuScreen(Principal game) {
        this.game = game;
        this.batch = game.batch;

        this.font = game.bigFont;
        this.fontShadow = game.bigFontShadow;

        layout = new GlyphLayout();

        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        viewport.apply();
        camera.position.set(1280 / 2f, 720 / 2f, 0);
    }

    @Override
    public void show() {
        background = new Texture("images/backgroundMenu.png");
        game.soundManager.playMenuMusic();
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

        float startY = 400;
        for (int i = 0; i < menuOptions.length; i++) {
            String option = menuOptions[i];

            String displayText = option;
            if (i == selectedOption) {
                displayText = "> " + option + " <";
            }

            layout.setText(fontShadow, displayText);
            float textWidth = layout.width;
            float optionX = (1280 - textWidth) / 2f;
            float optionY = startY - i * 60;

            fontShadow.draw(batch, displayText, optionX + 2, optionY - 2);

            if (i == selectedOption) {
                font.setColor(Color.RED);
            } else {
                font.setColor(Color.WHITE);
            }
            font.draw(batch, displayText, optionX, optionY);
        }

        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Keys.DOWN) || Gdx.input.isKeyJustPressed(Keys.S)) {
            selectedOption = (selectedOption + 1) % menuOptions.length;
        }
        if (Gdx.input.isKeyJustPressed(Keys.UP) || Gdx.input.isKeyJustPressed(Keys.W)) {
            selectedOption = (selectedOption - 1 + menuOptions.length) % menuOptions.length;
        }
        if (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            switch (menuOptions[selectedOption]) {
                case "Juego local":
                    dispose();
                    game.setScreen(new CharacterSelectScreen(game));
                    break;
                case "Juego en red":
                    dispose();
                    game.setScreen(new NetworkLobbyScreen(game));
                    break;
                case "Opciones":
                    dispose();
                    game.setScreen(new OptionsScreen(game));
                    break;
                case "Salir":
                    Gdx.app.exit();
                    break;
            }
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
        if(background != null) background.dispose();
    }
}