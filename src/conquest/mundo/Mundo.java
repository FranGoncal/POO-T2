package conquest.mundo;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import conquest.edificio.*;
import prof.jogos2D.image.*;

/**
 * A classe Mundo representa os edifícios e todos
 * os outros elementos que estão presentes num nível
 */
public class Mundo {

	private ComponenteVisual fundo;  // imagem de fundo do nível
	// TODO FEITO suportar os restantes edifícios
	// listas com os vários elementos presentes no jogo
	
	private ArrayList<EdificioDefault> edificios = new ArrayList<EdificioDefault>();
	
	private ArrayList<Batalhao> batalhoes = new ArrayList<Batalhao>();
	private ArrayList<Projetil> projeteis = new ArrayList<Projetil>();
		
	/** construtor do mundo
	 * @param img imagem de fundo do mundo
	 */
	public Mundo( ComponenteVisual img ){
		fundo = img;
	}
	
	/** vai desenhar todo os elementos do mundo
	 * @param g onde vai desenhar
	 */
	public synchronized void desenhar( Graphics2D g ){
		fundo.desenhar( g );
		
		// TODO FEITO suportar os restantes edifícios
		for( Edificio e : edificios )
			e.desenhar( g );
	
		
		for( Batalhao b : batalhoes )
			b.desenhar( g );
		
		for( Projetil b : projeteis )
			b.desenhar( g );
	}

	/**
	 * Atualiza todos os elementos do mundo e remove
	 * os elementos que já não são necessários
	 * Cada chamada a este método conta como um ciclo de processamento.
	 */
	public synchronized void atualiza(){
		for( Batalhao b : batalhoes )
			b.actualiza();
		
		// ver se batalhões atacam entre si
		for( int i=0; i < batalhoes.size()-1; i++ ) {
			for( int k = i+1; k < batalhoes.size(); k++ ) {
				Batalhao a = batalhoes.get( i );
				Batalhao b = batalhoes.get( k );
				// se são da mesma equipa ignoram-se
				if( a.getEquipa() == b.getEquipa() )
					continue;
				// se estiverem à distância de ataque (5 pixeis) há combate
				if( a.getPos().distance( b.getPos() ) < 5 )
					a.ataca( b );
			}
		}
			
		for( Projetil b : projeteis )
			b.atualiza();
			
		// TODO FEITO suportar os restantes edifícios
		for( Edificio e : edificios )
			e.atualiza();
		
		//retirar os batalhões terminados
		for( int i = batalhoes.size()-1; i >= 0; i-- ){
			if( batalhoes.get(i).estaTerminado() )
				batalhoes.remove( i );
		}
		
		// retirar os projéteis terminados
		for( int i = projeteis.size()-1; i >= 0; i-- ){
			if( projeteis.get(i).isTerminado() )
				projeteis.remove( i );
		}
	}
	
	/** adiciona uma quinta ao mundo
	 * @param q quinta a adicionar
	 */
	public synchronized void addEdificio( EdificioDefault e ){
		// TODO FEITO suportar os restantes edifícios
		edificios.add( e );
	}

	
	/** remove um edificio do mundo
	 * @param e edificio a remover
	 */
	public synchronized void remove( Edificio e ){
		// TODO FEITO suportar os restantes edifícios
		edificios.remove( e );
	}	
	
	/** devolve os edificios presentes no mundo
	 * @return os edificios presentes no mundo
	 */
	public List<EdificioDefault> getEdificios() {		
		// TODO FEITO suportar os restantes edifícios
		return Collections.unmodifiableList( edificios );
	}
	
	/** Determinar qual a quinta que está numa dada posição do écran
	 * @param pt coordenada onde pesquisar
	 * @return a quinta na posição pt, ou null caso não exista nenhum
	 */
	public EdificioDefault getEdificioAt(Point pt) {
		// TODO FEITO suportar os restantes edifícios
		for( EdificioDefault e : edificios )
			if( e.estaDentro( pt ) )
				return e;
		return null;
	}
	/** adicionar um batalhão ao mundo
	 * @param b batalhão a adicionar
	 */
	public synchronized void addBatalhao(Batalhao b) {
		batalhoes.add( b );
	}
	
	/** remover um batalhão do mundo
	 * @param b batalhão a remover
	 */
	public synchronized void removeBatalhao(Batalhao b) {
		batalhoes.remove( b );
	}

	/** devolve os batalhões presentes no mundo
	 * @return os batalhões presentes no mundo
	 */
	public List<Batalhao> getBatalhoes() {
		return Collections.unmodifiableList( batalhoes );
	}
	
	/** adiciona um projétil ao mundo
	 * @param p projétil a adicionar ao mundo
	 */
	public synchronized void addProjetil(Projetil p) {
		projeteis.add( p );
	}
	
	/** remove um projétil do mundo
	 * @param p o projétil a remover
	 */
	public synchronized void removeProjetil( Projetil p ) {
		projeteis.remove( p );
	}
	
	/** decolve os projéteis existentes
	 * @return os projéteis existentes
	 */
	public List<Projetil> getProjeteis(){
		return Collections.unmodifiableList( projeteis );
	}
}
