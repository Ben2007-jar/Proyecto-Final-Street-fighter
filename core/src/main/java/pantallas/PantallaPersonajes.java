package pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import elementos.Imagen;
import personajes.Mauro;
import personajes.Nerfeado;
import utiles.Recursos;
import utiles.Render;

public class PantallaPersonajes implements Screen {

	Imagen fondo;
	boolean aparicion = false;
	float contTiempoPersonajes = 0, tiempoPersonajes = 20;
	float a = 0;
	float contTiempo = 0, tiempoEspera = 40;
	Imagen mauro;
	Imagen nerfeado;
	Imagen salgado;
	Imagen reyes;
	BitmapFont fuente;

	@Override
	public void show() {
		fondo = new Imagen(Recursos.FONDO_PERSONAJES);
		mauro = new Imagen(Recursos.MAURO);
		nerfeado = new Imagen(Recursos.NERFEADO);
		salgado = new Imagen(Recursos.SALGADO);
		reyes = new Imagen(Recursos.REYES);

		// MEJORAR
		mauro.setParametros(130, 223, 110, 190);
		nerfeado.setParametros(960, 220, 160, 200);
		salgado.setParametros(130, 240, 120, 170);
		reyes.setParametros(960, 220, 160, 200);

		FreeTypeFontGenerator generador = new FreeTypeFontGenerator(Gdx.files.internal("pixel.ttf"));
		FreeTypeFontParameter parametros = new FreeTypeFontGenerator.FreeTypeFontParameter();

		parametros.size = 45;
		parametros.color = Color.LIGHT_GRAY;
		parametros.shadowColor = Color.DARK_GRAY;
		parametros.shadowOffsetX = -2;
		parametros.shadowOffsetY = 3;

		fuente = generador.generateFont(parametros);
	}

	@Override
	public void render(float delta) {
		Render.batch.begin();
		Render.limpiarPantalla(0, 0, 0);
		procesarCambioPantalla();
		fondo.dibujar();
		salgado.dibujar();
		reyes.dibujar();

//		 HACER DENTRO DE UNA FUNCIONNNNNNNNNNN
		contTiempo += 0.1f;
		if (contTiempo > tiempoPersonajes) {
			salgado.setTransparencia(0);
			reyes.setTransparencia(0);
			mauro.dibujar();
			nerfeado.dibujar();
			fuente.draw(Render.batch, "> JUGADOR 2", 480, 125);
		} else {
			fuente.draw(Render.batch, "> JUGADOR 1", 480, 125);
		}
		Render.batch.end();
	}

	private void procesarCambioPantalla() {
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
					Render.app.setScreen(new PantallaEscenario());
				}
			}
		}
		fondo.setTransparencia(a);
		mauro.setTransparencia(a);
		nerfeado.setTransparencia(a);
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