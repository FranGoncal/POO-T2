package conquest.edificio;

import prof.jogos2D.image.ComponenteVisual;
import conquest.mundo.Equipa;
import conquest.mundo.Mundo;

public class Quinta extends EdificioDefault {

	private int proxRegeneracao;       // quando é o próximo ciclo de regeneração

	
	public static final int COMIDA_POR_BLOCO = 15;
	public static final int BLOCO_PARA_COMIDA = 15;
	public static final int BLOCO_PARA_POPEXTRA = 15;
	
	
	/** Cria uma quinta.
	 * @param imagem imagem da quinta
	 * @param equipa equipa a que pertence
	 * @param mundo mundo onde está inserida
	 * @param pop população com que começa
	 * @param maxPop população máxima que suporta
	 */
	public Quinta(ComponenteVisual imagem, Equipa equipa, Mundo mundo, int pop, int maxPop) {
		super(imagem,equipa,mundo,pop,maxPop);
	}

	/** faz o processamento antes de começar cada turno, ou seja,
	 * no início de cada ciclo de processamento
	 */
	public void comecaTurno() {
		equipa.somaAtaqueExtra(-2);						//Altera as caracteristicas da equipa consoante uma quinta
		equipa.somaDefesaExtra(-2);
		// calcular a população extra que a quinta fornece
		int nGrupos = getPopulacao() / BLOCO_PARA_POPEXTRA;
		equipa.somaMaxPopExtra( 1*nGrupos );
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
	public void gerirPopulacao( int incrementoMax ) {		
		int incremento = Math.min( incrementoMax, estaSobrelotada()? -2: 1 );
		transitoHabitantes( incremento );   
	}
	
	/** reinicia a contagem do ciclo de regeneração
	 */
	public void resetRegeneracao() {
		setProximaRegeneracao( getEquipa().getRegeneracao() - 3 );			//Regeneração das quintas é a da equipa-3
	}
	
	/** Retorna a comida produzida por esta quinta
	 * @return a comida produzida por esta quinta
	 */
	public int getComidaProduzida() {
		return comidaProduz + (getPopulacao()/BLOCO_PARA_COMIDA)*COMIDA_POR_BLOCO;
	}
	
	/** Atualiza esta quinta. 
	 * Cada chamada a este método é um ciclo de processamento.
	 */
	public void atualiza() {
		// decrementa o regenerador da população
		proxRegeneracao--;
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
	public int recrutaHabitantes() {
		int popBat = (int)(getPopulacao() * 0.3);		//30%da população
		transitoHabitantes( -popBat );
		return popBat;
	}

	/** alista habitantes para um batalhão e devolve o número de habitantes alistados
	 * @return o número de habitantes alistados
	 */
	public int alistaHabitantes(){
		int popBat = (int)(getPopulacao() * 0.6);		//60%da população
		transitoHabitantes( -popBat );
		return popBat;
	}

	/** muda a equipa a quem pertence a quinta
	 * @param novaEquipa a nova equipa da quinta
	 */
	public void setEquipa(Equipa novaEquipa) {
		// TODO FEITO terminar de implementar este método
		super.setEquipa(novaEquipa);
		resetRegeneracao();
	}
}