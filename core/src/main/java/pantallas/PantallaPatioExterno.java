package pantallas;

import com.badlogic.gdx.Screen;

import elementos.Imagen;
import personajes.Reyes;
import personajes.Salgado;
import utiles.Recursos;
import utiles.Render;

public class PantallaPatioExterno implements Screen {

	Imagen fondo;
	boolean aparicion = false;
	float a = 0;
	float contTiempo = 0, tiempoEspera = 5;

	@Override
	public void show() {
		fondo = new Imagen(Recursos.PATIO_EXTERNO);
	}

	@Override
	public void render(float delta) {
		Render.batch.begin();
		Render.limpiarPantalla(0, 0, 0);
		procesarCambioPantalla();
		fondo.dibujar();
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
					Render.app.setScreen(new PantallaAula());
				}
			}
		}
		fondo.setTransparencia(a);
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