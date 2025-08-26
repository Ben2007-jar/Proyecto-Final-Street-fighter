package com.MBM.KOMaster;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import pantallas.PantallaEscenario;
import pantallas.PantallaMenu;
import pantallas.PantallaPersonajes;
import utiles.Render;

public class Principal extends Game {

	@Override
	public void create() {
		Render.app = this;
		Render.batch = new SpriteBatch();
		this.setScreen(new PantallaMenu());
	}

	@Override
	public void render() {
		ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
		super.render();
	}

	@Override
	public void dispose() {
		Render.batch.dispose();
	}

}