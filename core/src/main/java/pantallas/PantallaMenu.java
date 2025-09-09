package pantallas;

import com.MBM.KOMaster.Principal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import elementos.Imagen;
import io.Entradas;
import personajes.Mauro;
import personajes.Nerfeado;
import utiles.Recursos;
import utiles.Render;

public class PantallaMenu implements Screen {
	Imagen menu;
	boolean aparicion = false;
	float a = 0;
	float contTiempo = 0, tiempoEspera = 5;

	BitmapFont fuente;
	BitmapFont fuenteTitulo;
	Entradas entrada = new Entradas();
	
	@Override
	public void show() {
		menu = new Imagen(Recursos.FONDO_MENU);
		Gdx.input.setInputProcessor(entrada);
		FreeTypeFontGenerator generador = new FreeTypeFontGenerator(Gdx.files.internal("fuentes/pixel.ttf"));
		FreeTypeFontGenerator generadorTitulo = new FreeTypeFontGenerator(Gdx.files.internal("fuentes/MK4.ttf"));
		FreeTypeFontParameter parametros = new FreeTypeFontGenerator.FreeTypeFontParameter();
		FreeTypeFontParameter parametrosTitulo = new FreeTypeFontGenerator.FreeTypeFontParameter();

		parametros.size = 50;
		parametros.color = Color.WHITE;
		parametros.shadowColor = Color.DARK_GRAY;
		parametros.shadowOffsetX = -3;
		parametros.shadowOffsetY = 3;
		
		parametrosTitulo.size = 150;
		parametrosTitulo.color = Color.WHITE;
		parametrosTitulo.shadowColor = Color.RED;
		parametrosTitulo.shadowOffsetX = -3;
		parametrosTitulo.shadowOffsetY = 3;
				
		fuenteTitulo = generadorTitulo.generateFont(parametrosTitulo);
		fuente = generador.generateFont(parametros);
	}

	@Override
	public void render(float delta) {
		Render.limpiarPantalla(0, 0, 0);
		procesarTransparencia();
		Render.batch.begin();
		menu.dibujar();
		fuente.draw(Render.batch, "> Juego en red", 400, 370);
		fuente.draw(Render.batch, "> Juego local", 410, 270);
		fuente.draw(Render.batch, "> Controles", 420, 170);
		fuenteTitulo.draw(Render.batch, "K.O Master", 200, 600);
		if (entrada.isAbajo()) {
		}
		Render.batch.end();
	}

	private void procesarTransparencia() {
		if (!aparicion) {
			a += 0.01f;
			if (a > 1) {
				a = 1;
				aparicion = true;
			}
		} else {
			contTiempo += 0.1f;
			if (contTiempo > tiempoEspera) {
				a -= 0.01f;
				if (a < 0) {
					a = 0;
					Render.app.setScreen(new PantallaPersonajes());
				}
			}
		}
		menu.setTransparencia(a);
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
	}

}
