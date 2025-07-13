package conquest.edificio;


import java.awt.geom.Point2D;
import java.io.IOException;

import conquest.mundo.Batalhao;
import conquest.mundo.Equipa;
import conquest.mundo.Mundo;
import conquest.mundo.Projetil;
import prof.jogos2D.image.*;

/**
 * Representa um edifício. Neste momento apenas contempla as características de uma vila.
 */
public class Vila extends EdificioDefault {

	private int proxRegeneracao;       // quando é o próximo ciclo de regeneração
	private int proxDisparo = 30;      // decrementador que indica quando será feito o próximo disparo
	private int alcanceSq = 140*140;   // o quadrado do alcance (por causa das distâncias devolverem o quadrado)
	private ComponenteVisual projetil; // o desenho do projétil usado por este edifício
	
	/** Cria uma vila.
	 * @param imagem imagem da vila
	 * @param equipa equipa a que pertence
	 * @param mundo mundo onde está inserida
	 * @param pop população com que começa
	 * @param maxPop população máxima que suporta
	 */
	public Vila(ComponenteVisual imagem, Equipa equipa, Mundo mundo, int pop, int maxPop) {
		super(imagem,equipa,mundo,pop,maxPop);
		// carregar a imagem da bala
		try {
			projetil = new ComponenteSimples( "data/fireball_small.png");
		} catch (IOException e) {
		}
	}

	/** faz o processamento antes de começar cada turno, ou seja,
	 * no início de cada ciclo de processamento
	 */
	public void comecaTurno() {
		int nGrupos = getPopulacao() / 20; 						//Altera as caracteristicas da equipa consoante uma vila
		getEquipa().somaDefesaExtra( 1 * nGrupos );		
	}

	/** Indica quantas pessoas quer crescer
	 * @return quantas pessoas quer crescer
	 */
	public int quantoQuerCrescer() {
		return estaHoraCrescer()? 1: 0;
	}

	/** Gere a população, aumentando ou diminuindo a mesma
	 * @param incrementoMax máximo que pode aumentar 
	 */
	public void regenerarPop(int incrementoMax) {
		if( !estaHoraCrescer() )
			return;
		resetRegeneracao();
		
		// se pode aumentar mas está cheio não faz nada
		if( incrementoMax >= 0 && estaCheia() )
			return;
		
		// chegou a altura de fazer a atualização da população	
		super.regenerarPop(incrementoMax);
	}

	/** Aumenta ou diminui a população de um dado valor, se possível.
	 * Ainda verifica se está em excesso populacional. Se estiver a
	 * população pode descer, apesar do incremento ser positivo.
	 * @param incrementoMax qual o máximo de que pode incrementar a população,
	 * pois o edifício pode ter condições para crescer mais, mas a equipa não
	 */
	public void gerirPopulacao(int incrementoMax) {		
		int incremento = Math.min( incrementoMax, estaSobrelotada()? -1: 1 );
		transitoHabitantes( incremento );   // está abaixo do limite, tem-se de aumentar a população
	}

	/** reinicia a contagem do ciclo de regeneração
	 */
	public void resetRegeneracao() {
		setProximaRegeneracao( equipa.getRegeneracao() ); // recomeçar o decrementador
	}

	/** Atualiza esta vila. 
	 * Cada chamada a este método é um ciclo de processamento.
	 */
	public void atualiza(){
		// decrementa o regenerador da população
		proxRegeneracao--;
		
		processarInimigos();
	}
	
	/** coloca um novo valor na regeneração
	 * @param regeneracao o novo valor
	 */
	public void setProximaRegeneracao(int regeneracao) {
		proxRegeneracao = regeneracao;		
	}

	/** Indica se está na hora de crescer, isto é,
	 * se a população pode aumentar
	 * @return true, se está num ciclo de regeneração
	 */
	public boolean estaHoraCrescer() {
		return proxRegeneracao <= 0;
	}
	
	/** recruta habitantes para um batalhão e devolve o número de habitantes alistados
	 * @return o número de habitantes alistados
	 */
	public int recrutaHabitantes(){
		int popBat = getPopulacao()/2;  // Tira sempre metade da população
		transitoHabitantes( -popBat );
		return popBat;
	}
	
	/** alista habitantes para um batalhão e devolve o número de habitantes alistados
	 * @return o número de habitantes alistados
	 */
	public int alistaHabitantes(){
		int popBat = (int)(getPopulacao()*0.8);  // Tira 80% da população
		transitoHabitantes( -popBat );
		return popBat;
	}
	
	/** muda a equipa a quem pertence a vila
	 * @param novaEquipa a nova equipa da vila
	 */
	public void setEquipa(Equipa novaEquipa) {
		// TODO FEITO terminar de implementar este método
		super.setEquipa(novaEquipa);
		resetRegeneracao();
	}

	
	/** atacar inimigos que estejam dentro do alcance
	 */
	private void processarInimigos(){
		// actualizar o decrementador de disparos e ver se já é hora de disparar
		proxDisparo--;
		if( proxDisparo > 0 ) return;
		
		proxDisparo = 30;  // recomeçar o decrementador
		disparar( 1, projetil.clone() );     // vai disparar
	}

	/** vai disparar sobre o alvo
	 * @param estrago a quantidade de estrago que o disparo faz
	 */
	private void disparar( int estrago, ComponenteVisual projetilImg ) {
		// escolher o alvo
		Batalhao alvo = null;
		for( Batalhao b : getMundo().getBatalhoes() ){
			// tem de ser de equipa diferente e estar dentro de alcance
			if( b.getEquipa() != getEquipa() && estaDentroAlcance( b.getPos() ) ){
				alvo = b;
			}
		}
		// se encontrou um alvo vai disparar sobre ele
		if( alvo != null ){
			ComponenteVisual balaImg = projetilImg;
			balaImg.setPosicao( getPosicaoPorta() );
			Projetil b = new Projetil( balaImg, estrago, alvo);
			getMundo().addProjetil( b );
		}
	}
	
	/** verifica se uma posição está ao alcance das defesas 
	 * @param pos posição a verificar
	 * @return true se pos está dentro do alcance das defesas 
	 */
	private boolean estaDentroAlcance(Point2D.Double pos) {
		return getPosicaoPorta().distanceSq( pos  ) < alcanceSq;
	}
}
