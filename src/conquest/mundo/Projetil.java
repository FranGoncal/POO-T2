package conquest.mundo;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;

import prof.jogos2D.image.ComponenteVisual;
import prof.jogos2D.util.Vector2D;

/**
 * Um projétil disparado por um edifício
 */
public class Projetil {
	// a velocidade é fixa
	private static final int VELOCIDADE = 12;
	
	private ComponenteVisual imagem;    // imagem do projétil
	private int poder;                  // quantos soldados mata
	private boolean terminado = false;  // se já acabou (atingiu ou falhou, embora acerte sempre)
	private Batalhao alvo;              // batalhão a quem a bala se destina
	private Point2D.Double centro;      // posição do centro do projetil
	
	/**
	 * Para criar um projétil é preciso
	 * @param imagem a imagem do projétil
	 * @param poder  o poder destrutivo (quantos soldados mata)
	 * @param alvo   a quem se destina
	 */
	public Projetil(ComponenteVisual imagem, int poder, Batalhao alvo) {
		this.imagem = imagem;
		this.poder = poder;
		this.alvo = alvo;
		Point c = imagem.getPosicaoCentro();
		centro = new Point2D.Double( c.x, c.y );
	}

	/** atualiza o projétil
	 * move-se e verifica se atingiu o alvo
	 */
	public void atualiza(){
		// qual a direcão que a bala deve tomar para atingir o alvo
		Vector2D dir = new Vector2D( centro, alvo.getPos() );
		dir.normalizar();
		// mover
		centro.x += dir.x * VELOCIDADE;
		centro.y += dir.y * VELOCIDADE;
		imagem.setPosicaoCentro( new Point((int)centro.x, (int)centro.y) );
		
		// se estiver a menos velocidade pixeis do destino é porque atingiu o destino
		if( centro.distance( alvo.getPos() ) < VELOCIDADE ){
			alvo.mata( poder );
			terminado = true;
		}
	}

	/** desenha o projétil
	 * @param g onde desenhar 
	 */
	public void desenhar( Graphics g ){
		imagem.desenhar( g );
	}
	
	/** devolve o alvo do projétil
	 * @return o alvo
	 */
	public Batalhao getAlvo() {
		return alvo;
	}
	
	/** indica se o projétil já fez o que tinha a fazer
	 * @return true se o projétil já atingiu o alvo, false caso contrário
	 */
	public boolean isTerminado() {
		return terminado;
	}
}
