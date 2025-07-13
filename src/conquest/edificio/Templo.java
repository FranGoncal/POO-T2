package conquest.edificio;


import java.util.List;
import conquest.mundo.Equipa;
import conquest.mundo.Mundo;
import prof.jogos2D.image.ComponenteVisual;
import java.util.ArrayList;
import java.util.Collections;

public class Templo extends EdificioDefault {

	/** Cria um templo.
	 * @param imagem imagem do templo
	 * @param equipa equipa a que pertence
	 * @param mundo mundo onde está inserido
	 * @param pop população com que começa
	 * @param maxPop população máxima que suporta
	 */
	public Templo(ComponenteVisual imagem, Equipa equipa, Mundo mundo, int pop, int maxPop) {
		super(imagem, equipa, mundo, pop, maxPop);		
	}

	@Override
	public void comecaTurno() {
		equipa.somaAtaqueExtra(1);						//Altera as caracteristicas da equipa consoante um templo
		equipa.somaDefesaExtra(2);
		equipa.somaVelocidadeExtra( 2 );	
		//TODO FEITO diminuir em 2 a velocidade extra dos batalhoes inimigos
		reduzVelocidadeExtra(-2);
		int nGrupos = populacao / 10;
		equipa.somaRegeneracao(-1 * nGrupos);
	}

	@Override
	public int quantoQuerCrescer() {					//Crescimento zero
		return 0;
	}

	@Override
	public void gerirPopulacao(int incrementoMax) {
		int populacaoAMais=getMaxPopulacao()-populacao;
		int incremento = Math.min( incrementoMax, estaSobrelotada()? populacaoAMais: 0 );	//calculo do macimo de população adicional para fazer a redução automática
		transitoHabitantes( incremento );   // está abaixo do limite, tem-se de aumentar a população
	}

	public void atualiza() {
	}

	@Override
	public int recrutaHabitantes() {
		int popBat;
		if(populacao<=7)										//Se a população for menor ou igual a 7 a população adicionada ao batalhão é zero
			popBat=0;
		else {
			int popRestante=populacao-7;						//Garante 7pessoas da população
			popBat=(int) (popRestante*0.7);						//70% dos restantes
		}
		transitoHabitantes( -popBat );
		return popBat;
	}

	@Override
	public int alistaHabitantes() {
		int nGrupos = (int)(Math.floor(populacao*0.2)); 		//Numero de grupos de 5 inteiros que é possivel fazer
		int popBat = (nGrupos) * 5;								//População dos grupos
		transitoHabitantes( -popBat );
		return popBat;
	}
	/*
	 * Os seguintes métodos foram adicionados a esta classe 
	 * pela necessidade de decrementar velocidade às equipas inimigas
	 */

	private List<Equipa> getOutrasEquipas() {				
		List<Equipa> equipas = new ArrayList<>();
		for (Edificio e : getMundo().getEdificios()) 
			if(!equipas.contains(e.getEquipa())&&  e.getEquipa()!=equipa)
				equipas.add(e.getEquipa());
		return Collections.unmodifiableList(equipas);
	}
	
	private void reduzVelocidadeExtra(int reducao) {				
		List<Equipa> equipas = getOutrasEquipas();
		for(Equipa e : equipas)								
			e.somaVelocidadeExtra(reducao);
	}
}

