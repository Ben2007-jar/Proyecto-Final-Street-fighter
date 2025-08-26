package personajes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import elementos.Imagen;
import utiles.Recursos;
import utiles.Render;

public class Mauro {

	private Imagen mauro;

	public Mauro(float x, float y, float ancho, float alto) {
		mauro = new Imagen(Recursos.MAURO);
		mauro.setParametros(x, y, ancho, alto);
	}

	public void dibujar() {
		mauro.dibujar();
	}

	public void setX(float x) {
		mauro.setX(x);
	}
}