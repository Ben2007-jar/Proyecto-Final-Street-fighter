package personajes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import elementos.Imagen;
import utiles.Recursos;
import utiles.Render;

public class Nerfeado {

	private Imagen nerfeado;

	public Nerfeado(float x, float y, float ancho, float alto) {
		nerfeado = new Imagen(Recursos.NERFEADO);
		nerfeado.setParametros(x, y, ancho, alto);
	}

	public void dibujar() {
		nerfeado.dibujar();
	}

	public void setX(float x) {
		nerfeado.setX(x);
	}
}