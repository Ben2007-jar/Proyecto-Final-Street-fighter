package com.MBM.KOMaster.ui;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;

public class HealthBar {
    private float x, y;
    private float width, height;

    public HealthBar(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void render(ShapeRenderer shapeRenderer, int currentHealth, int maxHealth){
        float healthPercent = (float) currentHealth / maxHealth;
        Color color = healthPercent < 0.3f ? Color.RED : Color.GREEN;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(x, y, width, height); // Fondo

        shapeRenderer.setColor(color);
        shapeRenderer.rect(x, y, width * healthPercent, height); // Barra de vida
        shapeRenderer.end();
    }
}
