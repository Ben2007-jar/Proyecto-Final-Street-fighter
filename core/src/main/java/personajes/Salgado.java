package personajes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import elementos.Imagen;
import utiles.Recursos;
import utiles.Render;

public class Salgado {

	private Imagen salgado;

	public Salgado(int x, int y, int ancho, int alto) {
		salgado = new Imagen(Recursos.SALGADO);
		salgado.setParametros(x, y, ancho, alto);
	}

	public void dibujar() {
		salgado.dibujar();
	}
}