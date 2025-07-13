package conquest.ia;

import java.util.List;

import conquest.edificio.*;
import conquest.mundo.*;

/**
 * Esta classe implementa a "Inteligência Artificial" do jogo.
 * NÃO É PRECISO ALTERAR ESTA CLASSE
 * NÃO É PRECISO ALTERAR ESTA CLASSE
 * NÃO É PRECISO ALTERAR ESTA CLASSE
 */
public class IAPlayer {

	private Equipa equipa;  // a equipa comandada por esta IA
	private Mundo mundo;    // o mundo onde a IA joga
	
	/**
	 * criar uma nova IA
	 * @param mundo mundo em que atua
	 * @param equipa equipa que controla
	 */
	public IAPlayer(Mundo mundo, Equipa equipa) {
		this.equipa = equipa;
		this.mundo = mundo;
	}

	/** Fazer uma jogada
	 */
	public void jogar( ){
		// ver para cada um dos edifícios que esta equipa tem
		// se a população é superior a metade da sua capacidade máxima
		// se for, ataca o edifício mais perto
		// TODO FEITO suportar os restantes edifícios
		for( Edificio v : equipa.getEdificios() ){
			// tem mais de metade da população?
			if( v.getPopulacao() > v.getMaxPopulacao() / 2 ){
				List<EdificioDefault> possiveisAlvos = mundo.getEdificios();
				Edificio alvo = null;
				double distanciaMenorSq = 0;
				// TODO FEITO suportar os restantes edifícios
				// escolher o alvo a atacar
				for( Edificio ea : possiveisAlvos ){
					if( ea.getEquipa() == equipa ) // se for da mesma equipa passa ao próximo edificio
						continue;
					if( alvo == null ){
						alvo = ea;
						distanciaMenorSq = v.getPosicaoPorta().distance( alvo.getPosicaoPorta() );
					}
					else {
						double distancia = v.getPosicaoPorta().distance( ea.getPosicaoPorta() );
						if( distancia < distanciaMenorSq ){
							alvo = ea;
							distanciaMenorSq = distancia;
						}
					}
				}
				if( alvo != null ) {// quer dizer que há alvo
					Batalhao b = v.recrutaBatalhao( alvo );
					mundo.addBatalhao( b );
				}
			}
		}		
	}
	
	/**
	 * Devolve a equipa que esta IA comanda
	 * @return a equipa que esta IA comanda
	 */
	public Equipa getEquipa() {
		return equipa;
	}
	
	/**
	 * Define a equipa que esta IA comanda
	 * @param equipa equipa que vai passar a comandar
	 */
	public void setEquipa(Equipa equipa) {
		this.equipa = equipa;
	}
}
