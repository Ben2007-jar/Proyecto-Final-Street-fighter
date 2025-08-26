package pantallas;

import com.badlogic.gdx.Screen;

import elementos.Imagen;
import personajes.Reyes;
import personajes.Salgado;
import utiles.Recursos;
import utiles.Render;

public class PantallaPatioExterno implements Screen {

	Imagen fondo;

	@Override
	public void show() {
		fondo = new Imagen(Recursos.PATIO_EXTERNO);
	}

	@Override
	public void render(float delta) {
		Render.batch.begin();
		fondo.dibujar();
		Render.batch.end();
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