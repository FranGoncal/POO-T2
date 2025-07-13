package conquest.jogo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import conquest.mundo.Equipa;
import conquest.edificio.Edificio;
import conquest.edificio.EdificioDefault;

/** Representa a coleção das vilas selecionadas pelo jogador para fazer uma
 * deslocação de tropas
 */
public class SeleccaoEdificios {
	private Equipa equipa;  // a equipa do jogador
	
	// TODO FEITO suportar os restantes edifícios
	// a lista das vilas selecionadas
	private ArrayList<EdificioDefault> edificios = new ArrayList<EdificioDefault>();
	
	/** Cria uma seleção, dedicada a uma equipa
	 * @param equipa equipa do jogador
	 */
	public SeleccaoEdificios(Equipa equipa) {
		this.equipa = equipa;
	}

	/** indica se a seleção está vazia, isto é, não tem
	 * ainda nenhum edifício
	 * @return se a seleção está vazia
	 */
	public boolean estaVazia() {
		return edificios.isEmpty();
	}
	
	/** limpa a seleção, isto é, remove todos os edifícios nela presentes
	 */
	public void limpar() {
		edificios.clear();
	}
	
	/** Adiciona uma vila, se já lá não estiver e se não for null
	 * @param v vila a adicionar
	 * @return true se conseguiu adicionar a vila
	 */
	public boolean addEdificio( EdificioDefault e ) {
		// TODO FEITO suportar os restantes edifícios
		if( e == null || e.getEquipa() != equipa || edificios.contains( e ) )
			return false;
		return edificios.add( e );
	}
	
	/** remove uma vila da seleção
	 * @param v vila a remover
	 * @return true se realmente removeu
	 */
	public boolean removeEdificio( Edificio e ) {
		// TODO FEITO suportar os restantes edifícios
		return edificios.remove( e );
	}
	
	/** Retorna as vilas presentes na seleção
	 * @return as vilas presentes na seleção
	 */
	public List<EdificioDefault> getEdificios() {
		// TODO FEITO suportar os restantes edifícios
		// recheck se os edificios continuam na posse da equipa
		// pois podem ter sido tomados enquanto o jogador fazia a seleção
		for( int i=edificios.size()-1; i >= 0; i-- )
			if( edificios.get(i).getEquipa() != equipa )
				edificios.remove( i );
		return Collections.unmodifiableList( edificios );
	}
}
