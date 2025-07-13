package conquest.edificio;

import java.awt.geom.Point2D;
import java.io.IOException;

import conquest.mundo.Batalhao;
import conquest.mundo.Equipa;
import conquest.mundo.Mundo;
import conquest.mundo.Projetil;
import prof.jogos2D.image.ComponenteSimples;
import prof.jogos2D.image.ComponenteVisual;

public class Forte extends EdificioDefault {

	private int proxDisparo = 30;      // decrementador que indica quando será feito o próximo disparo
	private int alcanceSq = 140*140;   // o quadrado do alcance (por causa das distâncias devolverem o quadrado)
	private ComponenteVisual projetil; // o desenho do projétil usado por este edifício

	/** Cria um forte.
	 * @param imagem imagem do forte
	 * @param equipa equipa a que pertence
	 * @param mundo mundo onde está inserido
	 * @param pop população com que começa
	 * @param maxPop população máxima que suporta
	 */
	public Forte(ComponenteVisual imagem, Equipa equipa, Mundo mundo, int pop, int maxPop) {
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
	@Override
	public void comecaTurno() {
		equipa.somaAtaqueExtra(3);						//Altera as caracteristicas da equipa consoante um forte
		equipa.somaDefesaExtra(3);
		equipa.somaVelocidadeExtra(1);
		int nGrupos = getPopulacao() / 20;
		getEquipa().somaAtaqueExtra( 1 * nGrupos );		
	}

	/** Indica quantas pessoas quer crescer
	 * @return quantas pessoas quer crescer
	 */
	@Override
	public int quantoQuerCrescer() {					//Crescimento zero
		return 0;
	}

	/** Aumenta ou diminui a população de um dado valor, se possível.
	 * Ainda verifica se está em excesso populacional. Se estiver a
	 * população pode descer, apesar do incremento ser positivo.
	 * @param incrementoMax qual o máximo de que pode incrementar a população,
	 * pois o edifício pode ter condições para crescer mais, mas a equipa não
	 */
	@Override
	public void gerirPopulacao(int incrementoMax) {
		int populacaoAMais=getMaxPopulacao()-populacao;
		int incremento = Math.min( incrementoMax, estaSobrelotada()? populacaoAMais: 0 );
		transitoHabitantes( incremento );   // está abaixo do limite, tem-se de aumentar a população
	}
	/** Atualiza este forte. 
	 * Cada chamada a este método é um ciclo de processamento.
	 */
	public void atualiza() {
		processarInimigos();
	}

	@Override
	public int recrutaHabitantes() {
		int popBat;
		if(populacao<=5)
			popBat=populacao;     //Se não for possivel sairem todos menos 5, saem todos menos 5
		else {
			int popRestante= populacao-5;
			popBat=popRestante;
		}
		transitoHabitantes( -popBat );
		return popBat;
	}

	@Override
	public int alistaHabitantes() {
		return recrutaHabitantes();					//Alista habitantes da mesma maneira que recruta
	}

	public double getDefesa() {
		return super.getDefesa()+20;			//Metodo especifico dos fortes, que aumentam a defesa extra em 20
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
