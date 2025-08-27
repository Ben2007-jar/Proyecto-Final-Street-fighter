package pantallas;

import com.badlogic.gdx.Screen;

import elementos.Imagen;
import utiles.Recursos;
import utiles.Render;

public class PantallaAula implements Screen {

	Imagen fondo;
	
	@Override
	public void show() {
		fondo = new Imagen(Recursos.AULA);
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