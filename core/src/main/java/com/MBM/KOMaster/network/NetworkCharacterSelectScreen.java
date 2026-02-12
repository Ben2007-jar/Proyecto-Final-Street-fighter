package com.MBM.KOMaster.network;

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
import com.MBM.KOMaster.data.CharacterDatabase;
import com.MBM.KOMaster.data.CharacterData;
import com.MBM.KOMaster.network.GameServer;
import com.MBM.KOMaster.network.GameClient;
import com.MBM.KOMaster.network.NetworkPacket;
import com.MBM.KOMaster.screens.NetworkMapSelectScreen;

/**
 * Pantalla de selección de personajes en modo red.
 * Sincroniza las selecciones entre jugadores.
 */
public class NetworkCharacterSelectScreen implements Screen {

    private static final int ROWS = 4;
    private static final int COLS = 3;
    
    private Principal game;
    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont fontShadow;
    private BitmapFont titleFont;
    private BitmapFont titleFontShadow;
    private GlyphLayout layout;
    private OrthographicCamera camera;
    private Viewport viewport;

    private Texture[] characterTextures;
    private Texture cursorTexture;
    private Texture background;

    private int cursorRow = 0;
    private int cursorCol = 0;
    private int selectedCharacter = -1;
    private boolean confirmed = false;
    
    private int opponentCursorRow = 0;
    private int opponentCursorCol = 1;
    private int opponentSelectedCharacter = -1;
    private boolean opponentConfirmed = false;

    private GameServer server;
    private GameClient client;
    private int playerId;  // 1 o 2

    public NetworkCharacterSelectScreen(Principal game, GameServer server, GameClient client, int playerId) {
        this.game = game;
        this.batch = game.batch;
        this.server = server;
        this.client = client;
        this.playerId = playerId;

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
        int characterCount = CharacterDatabase.getCharacterCount();
        characterTextures = new Texture[characterCount];

        background = new Texture("images/selectCharacter.png");

        for (int i = 0; i < characterCount; i++) {
            CharacterData character = CharacterDatabase.getCharacter(i);
            if (character.isAvailable() && character.getPortraitSprite() != null) {
                characterTextures[i] = new Texture(character.getPortraitSprite());
            }
        }

        cursorTexture = new Texture("images/cursor.png");
        
        game.soundManager.playCharacterSelectMusic();
    }

    @Override
    public void render(float delta) {
        handleInput();
        updateNetwork();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        batch.draw(background, 0, 0, 1280, 720);

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

        // Determinar qué jugador es cuál
        boolean isPlayer1 = (playerId == 1);
        
        int myChar = selectedCharacter;
        int oppChar = opponentSelectedCharacter;
        boolean myConfirmed = confirmed;
        boolean oppConfirmed = opponentConfirmed;
        
        // Panel P1
        String textP1 = "Jugador 1";
        layout.setText(font, textP1);
        float textP1X = leftPanelX + (panelWidth - layout.width) / 2f;

        fontShadow.draw(batch, textP1, textP1X + 2, panelY + panelHeight + 40 - 2);
        font.setColor(Color.RED);
        font.draw(batch, textP1, textP1X, panelY + panelHeight + 40);

        int p1Char = isPlayer1 ? myChar : oppChar;
        if (p1Char != -1 && characterTextures[p1Char] != null) {
            batch.draw(characterTextures[p1Char], leftPanelX, panelY, panelWidth, panelHeight);
        }

        String stateTextP1 = (isPlayer1 ? myConfirmed : oppConfirmed) ? "CONFIRMADO" : "ELIGIENDO";
        Color stateColorP1 = (isPlayer1 ? myConfirmed : oppConfirmed) ? Color.GREEN : Color.YELLOW;
        
        layout.setText(font, stateTextP1);
        float confX1 = leftPanelX + (panelWidth - layout.width) / 2f;
        float confY = panelY - 30;

        fontShadow.setColor(Color.BLACK);
        fontShadow.draw(batch, stateTextP1, confX1 + 2, confY - 2);
        font.setColor(stateColorP1);
        font.draw(batch, stateTextP1, confX1, confY);

        // Panel P2
        String textP2 = "Jugador 2";
        layout.setText(font, textP2);
        float textP2X = rightPanelX + (panelWidth - layout.width) / 2f;

        fontShadow.draw(batch, textP2, textP2X + 2, panelY + panelHeight + 40 - 2);
        font.setColor(Color.BLUE);
        font.draw(batch, textP2, textP2X, panelY + panelHeight + 40);

        int p2Char = isPlayer1 ? oppChar : myChar;
        if (p2Char != -1 && characterTextures[p2Char] != null) {
            batch.draw(characterTextures[p2Char], rightPanelX, panelY, panelWidth, panelHeight);
        }

        String stateTextP2 = (isPlayer1 ? oppConfirmed : myConfirmed) ? "CONFIRMADO" : "ELIGIENDO";
        Color stateColorP2 = (isPlayer1 ? oppConfirmed : myConfirmed) ? Color.GREEN : Color.YELLOW;
        
        layout.setText(font, stateTextP2);
        float confX2 = rightPanelX + (panelWidth - layout.width) / 2f;

        fontShadow.setColor(Color.BLACK);
        fontShadow.draw(batch, stateTextP2, confX2 + 2, confY - 2);
        font.setColor(stateColorP2);
        font.draw(batch, stateTextP2, confX2, confY);

        font.setColor(Color.WHITE);

        float gridStartX = 450;
        float gridStartY = 425;
        float iconWidth = 120;
        float iconHeight = 100;
        float xSpacing = 120;
        float ySpacing = 100;

        // Mi cursor
        int xMy = (int) (gridStartX + cursorCol * xSpacing);
        int yMy = (int) (gridStartY - cursorRow * ySpacing);
        batch.setColor(isPlayer1 ? 1 : 0, 0, isPlayer1 ? 0 : 1, 1);
        batch.draw(cursorTexture, xMy + 50, yMy + 70, iconWidth - 80, iconHeight - 80);

        // Cursor del oponente
        int xOpp = (int) (gridStartX + opponentCursorCol * xSpacing);
        int yOpp = (int) (gridStartY - opponentCursorRow * ySpacing);
        batch.setColor(isPlayer1 ? 0 : 1, 0, isPlayer1 ? 1 : 0, 1);
        batch.draw(cursorTexture, xOpp + 70, yOpp + 70, iconWidth - 80, iconHeight - 80);

        batch.setColor(1, 1, 1, 1);

        batch.end();
    }

    private void handleInput() {
        if (!confirmed) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                cursorRow = (cursorRow - 1 + ROWS) % ROWS;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                cursorRow = (cursorRow + 1) % ROWS;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.A) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                cursorCol = (cursorCol - 1 + COLS) % COLS;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.D) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                cursorCol = (cursorCol + 1) % COLS;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || 
            Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            int selectedIndex = cursorRow * COLS + cursorCol;
            if (confirmed) {
                confirmed = false;
                sendSelectionUpdate();
            } else if (canSelect(selectedIndex)) {
                selectedCharacter = selectedIndex;
                confirmed = true;
                sendSelectionUpdate();
            }
        }

        // Si ambos confirmaron, ir al juego
        if (confirmed && opponentConfirmed) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                dispose();
                if (playerId == 1) {
                    // Servidor elige mapa y lo envía
                    game.setScreen(new NetworkMapSelectScreen(game, server, null, playerId, selectedCharacter, opponentSelectedCharacter));
                } else {
                    // Cliente espera el mapa
                    game.setScreen(new NetworkMapSelectScreen(game, null, client, playerId, selectedCharacter, opponentSelectedCharacter));
                }
            }
        }
    }

    private void sendSelectionUpdate() {
        NetworkPacket packet = new NetworkPacket(NetworkPacket.PacketType.CHARACTER_SELECT);
        packet.playerId = playerId;
        packet.selectedCharacter = confirmed ? selectedCharacter : -1;
        
        if (server != null) {
            server.sendPacket(packet);
        } else if (client != null) {
            client.sendPacket(packet);
        }
    }

    private void updateNetwork() {
        NetworkPacket packet = null;
        
        if (server != null) {
            packet = server.receivePacket();
        } else if (client != null) {
            packet = client.receivePacket();
        }
        
        if (packet != null && packet.type == NetworkPacket.PacketType.CHARACTER_SELECT) {
            if (packet.playerId != playerId) {
                // Actualizar estado del oponente
                if (packet.selectedCharacter >= 0) {
                    opponentSelectedCharacter = packet.selectedCharacter;
                    opponentConfirmed = true;
                } else {
                    opponentConfirmed = false;
                }
            }
        }
    }

    private boolean canSelect(int idx) {
        CharacterData character = CharacterDatabase.getCharacter(idx);
        return character != null && character.isAvailable();
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