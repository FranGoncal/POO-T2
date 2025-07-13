package conquest.mundo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;

import conquest.edificio.Edificio;
import prof.jogos2D.image.*;
import prof.jogos2D.util.Vector2D;

/**
 *  conjunto de soldados de saem de um edifício
 */
public class Batalhao {
	private Equipa equipa;                  // equipa a que pertence
	// TODO FEITO suportar os restantes edifícios
	private Edificio dest;                      // vila para onde se dirige
	
	private int nAtacantes;                 // nº de elementos    
	private double ataque;                  // poder de ataque 
	private Point2D.Double pos;             // posição onde está 
	private ComponenteVisual imagem;        // imagem do soldado do batalhão 
	private Vector2D dir;                   // direção do movimento 
	private double velocidade, velOriginal; // velocidade atual e original
	private boolean terminado = false;      // se está terminado

	/** construtor do batalhão
	 * @param equipa      equipa a que o batalhão pertence
	 * @param origem      local de onde parte
	 * @param dest        vila de destino
	 * @param nAtacantes  número de soldados que o constituem 
	 * @param ataque      valor do ataque dos soldados 
	 * @param veloc       velocidade dos soldados 
	 */
	public Batalhao(Equipa e, Point origem, Edificio dest, int nAtacantes, int ataque, int veloc ) {
		this.equipa = e;
		this.dest = dest; 		// TODO FEITO suportar os restantes edifícios
		setAtaque( ataque );           
		this.nAtacantes = nAtacantes;

		// posição do batalhão no mundo
		this.pos = new Point2D.Double( origem.x, origem.y );

		// determinar a direção e velocidade do movimento
		dir = new Vector2D( origem, dest.getPosicaoPorta() );
		dir.normalizar();
		setVelocidade( veloc ); 
		velOriginal = velocidade;

		// cria um clone da imagem do soldado para o batalhão 
		ComponenteMultiAnimado img = (ComponenteMultiAnimado)equipa.getSoldado().clone();
		// ver se o soldado deve estar virado para a esquerda ou direita
		if( dir.x < 0 )
			img.setAnim( 1 );
		this.imagem = img;
	}

	/** Actualiza o batalhão
	 * Cada chamada a este método é considerada um ciclo de processamento
	 */
	public void actualiza() {
		// deslocar o batalhão
		pos.x += dir.x * velocidade;
		pos.y += dir.y * velocidade;

		// se estiver a menos de velocidade do destino é porque chegou 
		if( dest.getPosicaoPorta().distance( pos ) < velocidade ){
			// se o destino for da mesma equipa é para entrar, senão é um ataque 
			if( equipa == dest.getEquipa() )
				entra();
			else
				ataca( dest );				
		}
		// repor a velocidade original
		velocidade = velOriginal;
	}

	/**
	 * atacar um edifício
	 */
	private void ataca( Edificio e ){
		// TODO FEITO suportar os restantes edifícios
		// determinar os valores de ataque, defesa e da luta
		double totalDefesa = e.getDefesa();
		double totalAtaque = ataque*nAtacantes;
		double luta = totalDefesa - totalAtaque;
		if( luta > 0 ){
			// se luta for positiva então ganham os defensores
	        // população sobrevivente ao ataque
			int popFinal = (int)(e.getPopulacao() * (luta / totalDefesa ));
			int decrementoPop = e.getPopulacao() - popFinal;  // população morta na defesa
			e.transitoHabitantes( -decrementoPop );		  // diminuir a população atual das baixas  
		}
		else {
			// neste caso ganharam os atacantes
			e.transitoHabitantes( -e.getPopulacao() );  // retirar toda a população do edifício 
			e.setEquipa( equipa );                      // mudar de equipa
            // nº de atacantes que sobreviveram
			int popFinal = (int)(nAtacantes *(-luta / totalAtaque));
			e.transitoHabitantes( popFinal );              // serão a nova população do edifício
		}
		terminado = true; // o batalhão já não tem mais nada a fazer
	}
	
	/** Atacar outro batalhão
	 * @param b batalhão com que vai lutar
	 */
	public void ataca(Batalhao b) {
		// determinar os valores de ataque de ambos
		double totalAtaqueA = ataque*nAtacantes;
		double totalAtaqueB = b.ataque*b.nAtacantes;
		double luta = totalAtaqueA - totalAtaqueB;
		if( luta > 0 ){
			// se luta for positiva então ganhamos nós
			nAtacantes = (int)(luta/ataque); // ajustar os sobreviventes
			b.terminado = true;              // o outro batalhão foi eliminado
		}
		else {
			// senão ganharam os outros
			b.nAtacantes = (int)(-luta/b.ataque); // ajustar os sobrevicentes
			terminado = true;                     // este batalão foi eliminado
		}
	}

	/** batalhão chega ao destino e os soldados são adicionados à população do edifício
	 */
	private void entra(){
		dest.transitoHabitantes( nAtacantes );
		terminado = true;
	}
	
	/** desenhar o batalhão
	 * @param g onde desenhar
	 */
	public void desenhar(Graphics g) {
		// desenhar o número de soldados acima do soldado
		g.setColor( Color.BLACK );  // fazer a sombra		
		g.drawString( ""+nAtacantes, (int)(pos.x+16), (int)(pos.y-3) );
		g.setColor( Color.WHITE );		
		g.drawString( ""+nAtacantes, (int)(pos.x+15), (int)(pos.y-4) );
		// desenhar um soldado por cada grupo de 5 soldados
		int nDesenhos = nAtacantes / 5 + 1;
		for( int i = nDesenhos; i >= 0; i--){
			imagem.setPosicaoCentro( new Point( (int)(getPos().x-i*dir.x*15), (int)(getPos().y-i*dir.y*15)) );
			imagem.desenhar(g);
		}
	}

	/** indica se o batalhão terminou as suas tarefas
	 * @return true se está terminado
	 */
	public boolean estaTerminado() {
		return terminado;
	}
	
	/** devolve a posição atual do batalhão no mundo
	 * @return a posição atual do batalhão no mundo
	 */
	public Point2D.Double getPos() {
		return pos;
	}

	/** diminui o número de soldados no batalhão
	 * @param nMortos número de mortos no batalhão 
	 */
	public void mata(int nMortos) {
		nAtacantes -= nMortos;
		if( nAtacantes <= 0)
			terminado = true;
	}

	/** devolve a equipa a que pertence este batalhão
	 * @return a equipa a que pertence 
	 */
	public Equipa getEquipa() {
		return equipa;
	}

	/** retorna a velocidade de deslocamento
	 * @return a velocidade de deslocamento
	 */
	public double getVelocidade() {
		return velocidade;
	}
	
	/** define a velocidade de deslocamento
	 * @param velocidade a nova velocidade 
	 */
	public void setVelocidade(double velocidade) {
		if( velocidade > Equipa.MAX_VELOCIDADE )
			this.velocidade = Equipa.MAX_VELOCIDADE;
		else if( velocidade < 0 )
			this.velocidade = 0;
		else
			this.velocidade = velocidade;
	}

	/** define o valor do ataque deste batalhão
	 * 
	 * @param a o novo valor de ataque
	 */
	public void setAtaque(int a) {
		if( a > Equipa.MAX_FORCA )
			this.ataque = Equipa.MAX_FORCA;
		else if( a < 0 )
			this.ataque = 0;
		else
			this.ataque = a;	} 
}
