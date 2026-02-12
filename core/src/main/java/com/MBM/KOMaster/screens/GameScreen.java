package com.MBM.KOMaster.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.MBM.KOMaster.Principal;
import com.MBM.KOMaster.characters.Character;
import com.MBM.KOMaster.characters.Fighter1;
import com.MBM.KOMaster.characters.Fighter2;
import com.MBM.KOMaster.input.InputController;
import com.MBM.KOMaster.ui.RoundManager;
import com.MBM.KOMaster.combat.CombatSystem;
import com.MBM.KOMaster.data.MapDatabase;
import com.MBM.KOMaster.data.MapData;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.Input;

public class GameScreen implements Screen {

    // Constantes
    private static final float CHARACTER_WIDTH = 300f;
    private static final float CHARACTER_HEIGHT = 300f;
    private static final float HEALTH_BAR_WIDTH = 500f;
    private static final float HEALTH_BAR_HEIGHT = 20f;
    private static final float HEALTH_BAR_Y_OFFSET = 50f;
    private static final float HEALTH_BAR_MARGIN = 50f;
    private static final float PLAYER1_START_X = 100f;
    private static final float PLAYER2_START_X = 930f;
    private static final float START_Y = 100f;
    
    private Principal game;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    private Character player1;
    private Character player2;
    
    private OrthographicCamera camera;
    private Viewport viewport;

    private Texture mapBackground;
    
    private RoundManager roundManager;
    private CombatSystem combatSystem;
    
    private BitmapFont font;
    private BitmapFont fontShadow;
    private BitmapFont bigFont;
    private BitmapFont bigFontShadow;
    private GlyphLayout layout;
    
    private int selectedP1Char;
    private int selectedP2Char;
    private int selectedMap;
    private InputController controller1;
    private InputController controller2;

    private boolean isPaused = false;
    private int pauseSelectedOption = 0;

    public GameScreen(Principal game, int selectedP1, int selectedP2, int selectedMap) {
        this.game = game;
        this.batch = game.batch;
        this.shapeRenderer = new ShapeRenderer();
        this.selectedMap = selectedMap;
        this.selectedP1Char = selectedP1;
        this.selectedP2Char = selectedP2;
        
        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        viewport.apply();
        camera.position.set(1280 / 2f, 720 / 2f, 0);
        
        controller1 = InputController.createPlayer1Controller();
        controller2 = InputController.createPlayer2Controller();
        
        roundManager = new RoundManager();
        combatSystem = new CombatSystem(game.soundManager);
        
        this.font = game.font;
        this.fontShadow = game.fontShadow;
        this.bigFont = game.bigFont;
        this.bigFontShadow = game.bigFontShadow;

        layout = new GlyphLayout();
        
        createPlayers();
    }
    
    private void createPlayers() {
        // SIMPLE Y DIRECTO: Crear Fighter1 o Fighter2 según selección
        if (selectedP1Char == 0) {
            player1 = new Fighter1(PLAYER1_START_X, START_Y, 100, controller1);
        } else {
            player1 = new Fighter2(PLAYER1_START_X, START_Y, 100, controller1);
        }
        
        if (selectedP2Char == 0) {
            player2 = new Fighter1(PLAYER2_START_X, START_Y, 100, controller2);
        } else {
            player2 = new Fighter2(PLAYER2_START_X, START_Y, 100, controller2);
        }
        
        combatSystem.reset();
    }

    @Override
    public void show() {
        MapData mapData = MapDatabase.getMap(selectedMap);
        if (mapData != null) {
            mapBackground = new Texture(mapData.getBackgroundPath());
        } else {
            mapBackground = new Texture("images/aula.png");
        }
        
        game.soundManager.playRandomBattleMusic();
    }

    @Override
    public void render(float delta) {
        handlePauseInput();
        
        if (!isPaused) {
            roundManager.update(delta);
            
            if (!roundManager.isInTransition() && !roundManager.isMatchFinished()) {
                update(delta);
                
                int roundWinner = roundManager.checkRoundEnd(player1.getHealth(), player2.getHealth());
                if (roundWinner > 0 && !roundManager.isMatchFinished()) {
                    createPlayers();
                }
            }
            
            handleMenuInput();
        } else {
            handlePauseMenuInput();
        }
        
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        batch.begin();
        if (mapBackground != null) {
            batch.draw(mapBackground, 0, 0, 1280, 720);
        }
        batch.end();

        batch.begin();
        TextureRegion currentFrameP1 = player1.getCurrentFrame();
        batch.draw(currentFrameP1, player1.getX(), player1.getY(), CHARACTER_WIDTH, CHARACTER_HEIGHT);

        TextureRegion currentFrameP2 = player2.getCurrentFrame();
        batch.draw(currentFrameP2, player2.getX(), player2.getY(), CHARACTER_WIDTH, CHARACTER_HEIGHT);
        batch.end();

        drawHealthBars();
        drawRoundUI();
        
        if (roundManager.isInTransition()) {
            drawTransitionScreen();
        } else if (roundManager.isMatchFinished()) {
            drawMatchEndScreen();
        }
        
        if (isPaused) {
            drawPauseMenu();
        }
    }

    private void update(float delta) {
        player1.update(delta);
        player2.update(delta);

        float leftLimit = -25;
        float rightLimit = viewport.getWorldWidth();

        player1.setX(Math.max(leftLimit, Math.min(rightLimit - 250, player1.getX())));
        player2.setX(Math.max(leftLimit, Math.min(rightLimit - 250, player2.getX())));

        combatSystem.update(player1, player2);
    }

    private void drawHealthBars() {
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float p1BarX = HEALTH_BAR_MARGIN;
        float p1BarY = worldHeight - HEALTH_BAR_Y_OFFSET;
        
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(p1BarX, p1BarY, HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT);
        
        float p1HealthPercent = (float) player1.getHealth() / 100f;
        shapeRenderer.setColor(player1.getHealth() > 30 ? Color.GREEN : Color.RED);
        shapeRenderer.rect(p1BarX, p1BarY, HEALTH_BAR_WIDTH * p1HealthPercent, HEALTH_BAR_HEIGHT);

        float p2BarX = worldWidth - HEALTH_BAR_MARGIN - HEALTH_BAR_WIDTH;
        float p2BarY = worldHeight - HEALTH_BAR_Y_OFFSET;
        
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(p2BarX, p2BarY, HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT);
        
        float p2HealthPercent = (float) player2.getHealth() / 100f;
        float p2HealthWidth = HEALTH_BAR_WIDTH * p2HealthPercent;
        float p2HealthX = p2BarX + (HEALTH_BAR_WIDTH - p2HealthWidth);
        
        shapeRenderer.setColor(player2.getHealth() > 30 ? Color.GREEN : Color.RED);
        shapeRenderer.rect(p2HealthX, p2BarY, p2HealthWidth, HEALTH_BAR_HEIGHT);

        shapeRenderer.end();
    }
    
    private void drawRoundUI() {
        batch.begin();
        
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        
        String timeText = roundManager.getFormattedTime();
        layout.setText(bigFont, timeText);
        float timeX = (worldWidth - layout.width) / 2f;
        float timeY = worldHeight - 15;
        
        bigFontShadow.draw(batch, timeText, timeX + 3, timeY - 3);
        
        if (roundManager.getRoundTimer() <= 10) {
            bigFont.setColor(Color.RED);
        } else {
            bigFont.setColor(Color.WHITE);
        }
        bigFont.draw(batch, timeText, timeX, timeY);
        
        String roundText = "ROUND " + roundManager.getCurrentRound();
        layout.setText(font, roundText);
        float roundX = (worldWidth - layout.width) / 2f;
        float roundY = worldHeight - 80;
        
        fontShadow.draw(batch, roundText, roundX + 2, roundY - 2);
        font.setColor(Color.YELLOW);
        font.draw(batch, roundText, roundX, roundY);
        
        String p1RoundsText = "P1: " + roundManager.getPlayer1Rounds();
        fontShadow.draw(batch, p1RoundsText, 52, worldHeight - 85);
        font.setColor(Color.RED);
        font.draw(batch, p1RoundsText, 50, worldHeight - 83);
        
        String p2RoundsText = "P2: " + roundManager.getPlayer2Rounds();
        layout.setText(font, p2RoundsText);
        float p2RoundsX = worldWidth - layout.width - 50;
        
        fontShadow.draw(batch, p2RoundsText, p2RoundsX + 2, worldHeight - 85);
        font.setColor(Color.BLUE);
        font.draw(batch, p2RoundsText, p2RoundsX, worldHeight - 83);
        
        font.setColor(Color.WHITE);
        bigFont.setColor(Color.WHITE);
        
        batch.end();
    }
    
    private void drawTransitionScreen() {
        batch.begin();
        
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        
        String winnerMsg = roundManager.getRoundWinnerMessage();
        layout.setText(bigFont, winnerMsg);
        float winnerX = (worldWidth - layout.width) / 2f;
        float winnerY = worldHeight / 2f + 80;
        
        bigFontShadow.draw(batch, winnerMsg, winnerX + 4, winnerY - 4);
        
        if (roundManager.getLastRoundWinner() == 1) {
            bigFont.setColor(Color.RED);
        } else {
            bigFont.setColor(Color.BLUE);
        }
        bigFont.draw(batch, winnerMsg, winnerX, winnerY);
        
        String reasonMsg = roundManager.getRoundEndReasonMessage();
        layout.setText(bigFont, reasonMsg);
        float reasonX = (worldWidth - layout.width) / 2f;
        float reasonY = worldHeight / 2f;
        
        bigFontShadow.draw(batch, reasonMsg, reasonX + 4, reasonY - 4);
        bigFont.setColor(Color.ORANGE);
        bigFont.draw(batch, reasonMsg, reasonX, reasonY);
        
        String nextRoundMsg = "SIGUIENTE ROUND EN " + (int)Math.ceil(roundManager.getTransitionTimer());
        layout.setText(font, nextRoundMsg);
        float nextX = (worldWidth - layout.width) / 2f;
        float nextY = worldHeight / 2f - 80;
        
        fontShadow.draw(batch, nextRoundMsg, nextX + 2, nextY - 2);
        font.setColor(Color.WHITE);
        font.draw(batch, nextRoundMsg, nextX, nextY);
        
        bigFont.setColor(Color.WHITE);
        
        batch.end();
    }
    
    private void drawMatchEndScreen() {
        batch.begin();
        
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        
        String winnerMsg = roundManager.getMatchWinnerMessage();
        layout.setText(bigFont, winnerMsg);
        float winnerX = (worldWidth - layout.width) / 2f;
        float winnerY = worldHeight / 2f + 80;
        
        bigFontShadow.draw(batch, winnerMsg, winnerX + 4, winnerY - 4);
        
        if (roundManager.getMatchWinner() == 1) {
            bigFont.setColor(Color.RED);
        } else {
            bigFont.setColor(Color.BLUE);
        }
        bigFont.draw(batch, winnerMsg, winnerX, winnerY);
        
        String scoreMsg = roundManager.getPlayer1Rounds() + " - " + roundManager.getPlayer2Rounds();
        layout.setText(bigFont, scoreMsg);
        float scoreX = (worldWidth - layout.width) / 2f;
        float scoreY = worldHeight / 2f;
        
        bigFontShadow.draw(batch, scoreMsg, scoreX + 4, scoreY - 4);
        bigFont.setColor(Color.GOLD);
        bigFont.draw(batch, scoreMsg, scoreX, scoreY);
        
        String optionsMsg = "SPACE: REVANCHA \n ESC: MENU";
        layout.setText(font, optionsMsg);
        float optionsX = (worldWidth - layout.width) / 2f;
        float optionsY = worldHeight / 2f - 100;
        
        fontShadow.draw(batch, optionsMsg, optionsX + 2, optionsY - 2);
        font.setColor(Color.WHITE);
        font.draw(batch, optionsMsg, optionsX, optionsY);
        
        bigFont.setColor(Color.WHITE);
        
        batch.end();
    }
    
    private void handlePauseInput() {
        if (!roundManager.isInTransition() && !roundManager.isMatchFinished()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                isPaused = !isPaused;
                pauseSelectedOption = 0;
                
                if (isPaused) {
                    game.soundManager.pauseBattleMusic();
                } else {
                    game.soundManager.resumeBattleMusic();
                }
            }
        }
    }
    
    private void handlePauseMenuInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            pauseSelectedOption = 0;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            pauseSelectedOption = 1;
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (pauseSelectedOption == 0) {
                isPaused = false;
                game.soundManager.resumeBattleMusic();
            } else {
                game.setScreen(new MenuScreen(game));
            }
        }
    }
    
    private void drawPauseMenu() {
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        
        batch.begin();
        
        String pauseTitle = "PAUSA";
        layout.setText(bigFont, pauseTitle);
        float titleX = (worldWidth - layout.width) / 2f;
        float titleY = worldHeight / 2f + 150;
        
        bigFontShadow.draw(batch, pauseTitle, titleX + 4, titleY - 4);
        bigFont.setColor(Color.YELLOW);
        bigFont.draw(batch, pauseTitle, titleX, titleY);
        
        String[] pauseOptions = {"REANUDAR", "SALIR AL MENU"};
        float startY = worldHeight / 2f + 50;
        
        for (int i = 0; i < pauseOptions.length; i++) {
            String option = pauseOptions[i];
            String displayText = option;
            
            if (i == pauseSelectedOption) {
                displayText = "> " + option + " <";
            }
            
            layout.setText(bigFont, displayText);
            float optionX = (worldWidth - layout.width) / 2f;
            float optionY = startY - i * 80;
            
            bigFontShadow.draw(batch, displayText, optionX + 3, optionY - 3);
            
            if (i == pauseSelectedOption) {
                bigFont.setColor(Color.GREEN);
            } else {
                bigFont.setColor(Color.WHITE);
            }
            bigFont.draw(batch, displayText, optionX, optionY);
        }
        
        bigFont.setColor(Color.WHITE);
        
        batch.end();
    }
    
    private void handleMenuInput() {
        if (roundManager.isMatchFinished()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                roundManager.reset();
                createPlayers();
                game.soundManager.playRandomBattleMusic();
            }
            
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                game.setScreen(new MenuScreen(game));
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }
    
    @Override
    public void dispose() {
        shapeRenderer.dispose();
        if (mapBackground != null) mapBackground.dispose();
    }
}