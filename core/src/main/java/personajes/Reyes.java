package personajes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import elementos.Imagen;
import utiles.Recursos;
import utiles.Render;

public class Reyes {

	private Imagen reyes;

	public Reyes(int x, int y, int ancho, int alto) {
		reyes = new Imagen(Recursos.REYES);
		reyes.setParametros(x, y, ancho, alto);
	}

	public void dibujar() {
		reyes.dibujar();
	}
}