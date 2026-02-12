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
import com.badlogic.gdx.Input.Keys;
import com.MBM.KOMaster.Principal;
import com.MBM.KOMaster.network.GameServer;
import com.MBM.KOMaster.network.NetworkCharacterSelectScreen;
import com.MBM.KOMaster.network.GameClient;
import java.net.InetAddress;

/**
 * Pantalla de lobby para juego en red.
 * Permite crear servidor o conectarse como cliente.
 */
public class NetworkLobbyScreen implements Screen {

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

    private enum LobbyState {
        MENU,           // Seleccionar servidor o cliente
        HOSTING,        // Esperando cliente
        CONNECTING,     // Intentando conectar
        CONNECTED,      // Conectado exitosamente
        ERROR           // Error de conexión
    }

    private LobbyState state;
    private int selectedOption = 0;
    private final String[] menuOptions = {"Crear Servidor", "Conectarse", "Volver"};
    
    private GameServer server;
    private GameClient client;
    private String serverIp = "";
    private String errorMessage = "";
    private String localIp = "";
    private boolean isInputtingIp = false;

    public NetworkLobbyScreen(Principal game) {
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
        
        state = LobbyState.MENU;
        
        // Obtener IP local
        try {
            localIp = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            localIp = "Error obteniendo IP";
        }
    }

    @Override
    public void show() {
        background = new Texture("images/backgroundMenu.png");
    }

    @Override
    public void render(float delta) {
        handleInput();
        
        // Verificar estado de conexión
        if (state == LobbyState.HOSTING && server != null && server.isConnected()) {
            state = LobbyState.CONNECTED;
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(background, 0, 0, 1280, 720);

        switch (state) {
            case MENU:
                drawMenu();
                break;
            case HOSTING:
                drawHosting();
                break;
            case CONNECTING:
                drawConnecting();
                break;
            case CONNECTED:
                drawConnected();
                break;
            case ERROR:
                drawError();
                break;
        }

        batch.end();
    }

    private void drawMenu() {
        String title = "JUEGO EN RED";
        layout.setText(titleFont, title);
        float titleX = (1280 - layout.width) / 2f;
        float titleY = 600;

        titleFontShadow.draw(batch, title, titleX + 3, titleY - 3);
        titleFont.setColor(Color.YELLOW);
        titleFont.draw(batch, title, titleX, titleY);

        float startY = 450;
        for (int i = 0; i < menuOptions.length; i++) {
            String option = menuOptions[i];
            String displayText = option;
            
            if (i == selectedOption) {
                displayText = "> " + option + " <";
            }

            layout.setText(font, displayText);
            float optionX = (1280 - layout.width) / 2f;
            float optionY = startY - i * 60;

            fontShadow.draw(batch, displayText, optionX + 2, optionY - 2);

            if (i == selectedOption) {
                font.setColor(Color.GREEN);
            } else {
                font.setColor(Color.WHITE);
            }
            font.draw(batch, displayText, optionX, optionY);
        }

        String ipInfo = "Tu IP local: " + localIp;
        layout.setText(font, ipInfo);
        float ipX = (1280 - layout.width) / 2f;
        fontShadow.draw(batch, ipInfo, ipX + 2, 152);
        font.setColor(Color.CYAN);
        font.draw(batch, ipInfo, ipX, 150);

        font.setColor(Color.WHITE);
        titleFont.setColor(Color.WHITE);
    }

    private void drawHosting() {
        String title = "SERVIDOR ACTIVO";
        layout.setText(titleFont, title);
        float titleX = (1280 - layout.width) / 2f;
        titleFontShadow.draw(batch, title, titleX + 3, 597);
        titleFont.setColor(Color.GREEN);
        titleFont.draw(batch, title, titleX, 600);

        String msg1 = "Esperando que un jugador se conecte...";
        layout.setText(font, msg1);
        float msg1X = (1280 - layout.width) / 2f;
        fontShadow.draw(batch, msg1, msg1X + 2, 402);
        font.setColor(Color.WHITE);
        font.draw(batch, msg1, msg1X, 400);

        String msg2 = "Comparte tu IP: " + localIp;
        layout.setText(font, msg2);
        float msg2X = (1280 - layout.width) / 2f;
        fontShadow.draw(batch, msg2, msg2X + 2, 302);
        font.setColor(Color.YELLOW);
        font.draw(batch, msg2, msg2X, 300);

        String msg3 = "Puerto: 25565";
        layout.setText(font, msg3);
        float msg3X = (1280 - layout.width) / 2f;
        fontShadow.draw(batch, msg3, msg3X + 2, 252);
        font.setColor(Color.CYAN);
        font.draw(batch, msg3, msg3X, 250);

        String cancelMsg = "ESC para cancelar";
        layout.setText(font, cancelMsg);
        float cancelX = (1280 - layout.width) / 2f;
        fontShadow.draw(batch, cancelMsg, cancelX + 2, 152);
        font.setColor(Color.GRAY);
        font.draw(batch, cancelMsg, cancelX, 150);

        font.setColor(Color.WHITE);
        titleFont.setColor(Color.WHITE);
    }

    private void drawConnecting() {
        String title = "CONECTANDO...";
        layout.setText(titleFont, title);
        float titleX = (1280 - layout.width) / 2f;
        titleFontShadow.draw(batch, title, titleX + 3, 597);
        titleFont.setColor(Color.ORANGE);
        titleFont.draw(batch, title, titleX, 600);

        if (isInputtingIp) {
            String prompt = "Ingresa la IP del servidor:";
            layout.setText(font, prompt);
            float promptX = (1280 - layout.width) / 2f;
            fontShadow.draw(batch, prompt, promptX + 2, 402);
            font.setColor(Color.WHITE);
            font.draw(batch, prompt, promptX, 400);

            String ipDisplay = serverIp + "_";
            layout.setText(font, ipDisplay);
            float ipX = (1280 - layout.width) / 2f;
            fontShadow.draw(batch, ipDisplay, ipX + 2, 302);
            font.setColor(Color.YELLOW);
            font.draw(batch, ipDisplay, ipX, 300);

            String instruction = "ENTER para conectar | ESC para cancelar";
            layout.setText(font, instruction);
            float instX = (1280 - layout.width) / 2f;
            fontShadow.draw(batch, instruction, instX + 2, 202);
            font.setColor(Color.GRAY);
            font.draw(batch, instruction, instX, 200);
        } else {
            String msg = "Intentando conectar a " + serverIp;
            layout.setText(font, msg);
            float msgX = (1280 - layout.width) / 2f;
            fontShadow.draw(batch, msg, msgX + 2, 402);
            font.setColor(Color.WHITE);
            font.draw(batch, msg, msgX, 400);
        }

        font.setColor(Color.WHITE);
        titleFont.setColor(Color.WHITE);
    }

    private void drawConnected() {
        String title = "¡CONECTADO!";
        layout.setText(titleFont, title);
        float titleX = (1280 - layout.width) / 2f;
        titleFontShadow.draw(batch, title, titleX + 3, 597);
        titleFont.setColor(Color.GREEN);
        titleFont.draw(batch, title, titleX, 600);

        String msg = "Pasando a selección de personajes...";
        layout.setText(font, msg);
        float msgX = (1280 - layout.width) / 2f;
        fontShadow.draw(batch, msg, msgX + 2, 402);
        font.setColor(Color.WHITE);
        font.draw(batch, msg, msgX, 400);

        font.setColor(Color.WHITE);
        titleFont.setColor(Color.WHITE);
    }

    private void drawError() {
        String title = "ERROR DE CONEXIÓN";
        layout.setText(titleFont, title);
        float titleX = (1280 - layout.width) / 2f;
        titleFontShadow.draw(batch, title, titleX + 3, 597);
        titleFont.setColor(Color.RED);
        titleFont.draw(batch, title, titleX, 600);

        layout.setText(font, errorMessage);
        float msgX = (1280 - layout.width) / 2f;
        fontShadow.draw(batch, errorMessage, msgX + 2, 402);
        font.setColor(Color.WHITE);
        font.draw(batch, errorMessage, msgX, 400);

        String backMsg = "ESC para volver";
        layout.setText(font, backMsg);
        float backX = (1280 - layout.width) / 2f;
        fontShadow.draw(batch, backMsg, backX + 2, 252);
        font.setColor(Color.GRAY);
        font.draw(batch, backMsg, backX, 250);

        font.setColor(Color.WHITE);
        titleFont.setColor(Color.WHITE);
    }

    private void handleInput() {
        switch (state) {
            case MENU:
                handleMenuInput();
                break;
            case HOSTING:
                handleHostingInput();
                break;
            case CONNECTING:
                handleConnectingInput();
                break;
            case CONNECTED:
                handleConnectedInput();
                break;
            case ERROR:
                handleErrorInput();
                break;
        }
    }

    private void handleMenuInput() {
        if (Gdx.input.isKeyJustPressed(Keys.DOWN) || Gdx.input.isKeyJustPressed(Keys.S)) {
            selectedOption = (selectedOption + 1) % menuOptions.length;
        }
        if (Gdx.input.isKeyJustPressed(Keys.UP) || Gdx.input.isKeyJustPressed(Keys.W)) {
            selectedOption = (selectedOption - 1 + menuOptions.length) % menuOptions.length;
        }
        if (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            switch (selectedOption) {
                case 0: // Crear Servidor
                    createServer();
                    break;
                case 1: // Conectarse
                    state = LobbyState.CONNECTING;
                    isInputtingIp = true;
                    serverIp = "";
                    break;
                case 2: // Volver
                    game.setScreen(new MenuScreen(game));
                    break;
            }
        }
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
        }
    }

    private void createServer() {
        server = new GameServer();
        if (server.start()) {
            state = LobbyState.HOSTING;
            // Iniciar espera en hilo separado
            new Thread(() -> {
                if (server.waitForClient()) {
                    state = LobbyState.CONNECTED;
                }
            }).start();
        } else {
            errorMessage = "Error al crear el servidor";
            state = LobbyState.ERROR;
        }
    }

    private void handleHostingInput() {
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            if (server != null) {
                server.stop();
                server = null;
            }
            state = LobbyState.MENU;
        }
    }

    private void handleConnectingInput() {
        if (isInputtingIp) {
            // Capturar entrada de texto
            for (int i = Keys.NUM_0; i <= Keys.NUM_9; i++) {
                if (Gdx.input.isKeyJustPressed(i)) {
                    serverIp += (char) ('0' + (i - Keys.NUM_0));
                }
            }
            if (Gdx.input.isKeyJustPressed(Keys.PERIOD) || Gdx.input.isKeyJustPressed(Keys.NUMPAD_DOT)) {
                serverIp += ".";
            }
            if (Gdx.input.isKeyJustPressed(Keys.BACKSPACE) && serverIp.length() > 0) {
                serverIp = serverIp.substring(0, serverIp.length() - 1);
            }
            
            if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
                isInputtingIp = false;
                connectToServer();
            }
            
            if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
                state = LobbyState.MENU;
                serverIp = "";
                isInputtingIp = false;
            }
        }
    }

    private void connectToServer() {
        client = new GameClient();
        new Thread(() -> {
            if (client.connect(serverIp)) {
                state = LobbyState.CONNECTED;
            } else {
                errorMessage = "No se pudo conectar a " + serverIp;
                state = LobbyState.ERROR;
                client = null;
            }
        }).start();
    }

    private void handleConnectedInput() {
        // Esperar 2 segundos y pasar a selección de personajes
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        if (server != null) {
            // Soy servidor (Jugador 1)
            game.setScreen(new NetworkCharacterSelectScreen(game, server, null, 1));
        } else if (client != null) {
            // Soy cliente (Jugador 2)
            game.setScreen(new NetworkCharacterSelectScreen(game, null, client, 2));
        }
    }

    private void handleErrorInput() {
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            state = LobbyState.MENU;
            errorMessage = "";
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(1280 / 2f, 720 / 2f, 0);
    }

    @Override
    public void pause() { }
    @Override
    public void resume() { }
    @Override
    public void hide() { }

    @Override
    public void dispose() {
        if (background != null) background.dispose();
        if (server != null) server.stop();
        if (client != null) client.disconnect();
    }
}