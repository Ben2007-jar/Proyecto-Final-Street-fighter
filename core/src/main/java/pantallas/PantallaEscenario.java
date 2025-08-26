package pantallas;

import com.badlogic.gdx.Screen;

import elementos.Imagen;
import personajes.Mauro;
import personajes.Nerfeado;
import utiles.Recursos;
import utiles.Render;

public class PantallaEscenario implements Screen {

	Imagen fondo;
	Mauro mauro;
	Nerfeado nerfeado;
	int cont = 0;
	boolean aparicion = false;
	float a = 0;
	float contTiempo = 0, tiempoEspera = 10;

	@Override
	public void show() {
		fondo = new Imagen(Recursos.ESCENARIO);
		mauro = new Mauro(1000, 200, 110, 190);
		nerfeado = new Nerfeado(250, 200, 170, 210);
	}

	@Override
	public void render(float delta) {
		Render.batch.begin();
		Render.limpiarPantalla(0, 0, 0);
		procesarCambioPantalla();
		fondo.dibujar();
		mauro.dibujar();
		nerfeado.dibujar();
		update();
		Render.batch.end();
	}

	private void update() {
		cont++;
		mauro.setX(800 - cont);
		nerfeado.setX(250 + cont);
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
					Render.app.setScreen(new PantallaPatioInterno());
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