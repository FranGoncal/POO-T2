package conquest.mundo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import conquest.edificio.Edificio;
import conquest.edificio.EdificioDefault;
import prof.jogos2D.image.*;

/**
 * Uma equipa do jogo.
 * Cada equipa tem um conjunto de edifícios e uma série de características próprias,
 * como a velocidade dos soldados, força de ataque e defesa, a capacidade de
 * regeneração da população, etc
 */
public class Equipa {

	// constantes para truncar os máximos de bónus que uma equipa pode ter
	public static final int MAX_FORCA = 20;
	public static final int MAX_VELOCIDADE = 15;
	public static final int MAX_REGENERACAO = 10;
	public static final int MAX_POP_EXTRA = 10;
	

	private String nome;                      // nome da equipa
	private boolean neutra;                   // indicação se é uma equipa neutra
	
	// TODO FEITO Feito suportar os restantes edifícios
	// lista com as vilas que a equipa possui num dado momento
	private ArrayList<EdificioDefault> edificios = new ArrayList<EdificioDefault>();	
	
	
	// elementos gráficos da equipa
	private ComponenteVisual flag;            // a bandeira da equipa
	private ComponenteVisual soldado;         // a imagem do soldado da equipa
	
	// representam as várias caracteristicas da equipa
	private int comidaBase = 30;              // comida base gerada pela equipa
	private int ataque, ataqueExtra;          // capacidade de ataque dos soldados (base e extras)
	private int defesa, defesaExtra;          // capacidade de defesa dos soldados 
	private int velocidade, velocidadeExtra;  // velocidade de movimento dos soldados 
	private int regeneracao, regeneracaoExtra;// de quantos em quatos ciclos se regenera um edifício  
	private int excessoPop, excessoPopExtra;  // população permitida além do máximo permitido por cada edifício
	private int comidaExistente;			  // comida que é produzida
	private int comidaPrecisa;                // comida que é necessária para suster a população e crescer
	private int populacao;                    // população atual
	
	/**
	 * Construtor da equipa com as várias características próprias da equipa
	 * @param nome nome da equipa
	 * @param ataque capacidade de ataque  
	 * @param defesa capacidade de defesa
	 * @param veloc velocidade de movimento dos soldados
	 * @param crescimento decrementador de tempo (em ciclos) entre regenerações
	 * @param maxPopExtra quantos habitantes podem ser acrescentado ao máximo permitido por cada edifício
	 */
	public Equipa( String nome, int ataque, int defesa, int veloc, int crescimento, int maxPopExtra ){
		this.nome = nome;
		// carregar as imagens da bandeira e do soldado
		try {
			String dataDir = "data/civs/"+nome+"/";
			flag = new ComponenteSimples(dataDir + "bandeira.gif");
			soldado = new ComponenteMultiAnimado( null, dataDir + "soldado.gif", 2, 4, 8 );
		} catch (IOException e) {
		}
		if( nome.equals("Neutros") )
			neutra = true;
		
		this.ataque = ataque;
		this.defesa = defesa;
		this.velocidade = veloc;
		this.regeneracao = crescimento;
		this.excessoPop = maxPopExtra;
	}
	
	public void comecarTurno() {
		if( neutra )
			return;
		
		regenerarPopulacao();

		// depois da população regenerada é preciso preparar o resto do turno
		ataqueExtra = 0;
		defesaExtra = 0;
		velocidadeExtra = 0;
		excessoPopExtra = 0;
		regeneracaoExtra = 0;
		// TODO FEITO suportar os restantes edifícios
		for( Edificio e : edificios )
			e.comecaTurno();
	}

	/** regenera a população da equipa, calculando a comida que é precisa
	 * e distribuindo a mesma pelos vários edifícios
	 */
	private void regenerarPopulacao() {
		// calcular a comida que há e quanta comida é requerida (para crescimento)
		// e também calcular a população
		populacao = 0;
		comidaExistente = comidaBase;
		comidaPrecisa = 0;
		// TODO FEITO suportar os restantes edifícios
		for( Edificio e : edificios ) {
			comidaExistente += e.getComidaProduzida();
			comidaPrecisa += e.quantoQuerCrescer();
			populacao += e.getPopulacao();
		}

		// além da comida precisa para crescer é preciso sustentar os que já existem
		comidaPrecisa += populacao;
		
		// se há comida para todos, todos recebem o que pedem
		if( comidaExistente > comidaPrecisa ) { 
			// TODO FEITO suportar os restantes edifícios
			for( Edificio e : edificios )
				e.regenerarPop( (int)(e.quantoQuerCrescer() ) );
		}
		// se não há comida para todos, a população desce de 1 em cada edifício
		else if( comidaExistente < populacao){
			// TODO FEITO suportar os restantes edifícios
			for( Edificio e : edificios )
				e.regenerarPop( -1 );
		}
		// há comida para todos, mas nem todos podem crescer, vai-se pela ordem de pedidos
		// mas cada um só leva 1 em vez do que pediram
		else {
			int comidaDistribuir = comidaExistente - populacao;
			// TODO FEITO suportar os restantes edifícios
			for( Edificio e : edificios ) {
				if( e.quantoQuerCrescer() > 0 ) {
					e.regenerarPop( 1 );
					comidaDistribuir--;
					// se acabou a comida, deixar de distribuir
					if( comidaDistribuir == 0 )
						break;
				}
			}
		}
	}


	/** devolve o nome da equipa
	 *  @return o nome da equipa
	 */
	public String getNome() {
		return nome;
	}

	/** altera o nome da equipa
	 * @param nome novo nome
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/**
	 * indica quantas vilas a equipa possui 
	 * @return o número de vilas que a equipa possui
	 */
	public int  getNumEdificios(){
		// TODO FEITO suportar os restantes edifícios
		return edificios.size();
	}

	/** devolve uma lista com as quintas da equipa
	 * @return uma lista com as quintas da equipa
	 */
	public List<EdificioDefault> getEdificios() {
		// TODO FEITO suportar os restantes edifícios
		return Collections.unmodifiableList( edificios );
	}
	
	/** adiciona um edificio à equipa
	 * @param e edificio a adicionar
	 */
	public void addEdificio( EdificioDefault e ){
		// TODO FEITO suportar os restantes edifícios
		edificios.add( e );
	}
	/** remove um edificio da equipa
	 * @param e edificio a remover
	 */
	public void removeEdificio( Edificio e ){
		// TODO FEITO suportar os restantes edifícios
		edificios.remove( e );
	}
	
	/** devolve o desenho da bandeira
	 * @return  o desenho da bandeira
	 */
	public ComponenteVisual getBandeira(){
		return flag;
	}
	
	/** indica se se trata de uma equipa neutra
	 * @return true se é neutra
	 */
	public boolean isNeutra() {
		return neutra;
	}

	/** devolve o nível de ataque
	 * @return o nível de ataque
	 */
	public int getAtaque() {
		return ataque + ataqueExtra;
	}

	/** devolve o nível de defesa
	 * @return o nível de defesa
	 */
	public int getDefesa() {
		return defesa + defesaExtra;
	}

	/** devolve a imagem do soldado
	 * @return a imagem do soldado
	 */
	public ComponenteVisual getSoldado() {
		return soldado;
	}

	/** devolve a velocidade dos soldados
	 * @return a velocidade dos soldados
	 */
	public int getVelocidade() {
		return velocidade + velocidadeExtra;
	}

	/** devolve a capacidade de regeneração
	 * @return a capacidade de regeneração
	 */
	public int getRegeneracao() {
		return regeneracao - regeneracaoExtra;
	}
	
	/** permite incrementar/decrementar a capacidade de regeneração de um dado fator 
	 * @param factor valor a acrescentar/diminuir a capacidade de regeneração
	 */
	public void somaRegeneracao( int factor ){
		regeneracaoExtra += factor;
		if( regeneracaoExtra > MAX_REGENERACAO )
			regeneracaoExtra = MAX_REGENERACAO;
	}
	
	/** permite incementar/decrementar ao nível de defesa
	 * @param d valor a incrementar à defesa
	 */
	public void somaDefesaExtra(int d) {
		defesaExtra += d;
		if( defesaExtra > MAX_FORCA )
			defesaExtra = MAX_FORCA;
	}

	/** permite incementar/decrementar ao nível de ataque
	 * @param d valor a incrementar ao ataque
	 */
	public void somaAtaqueExtra(int a) {
		ataqueExtra += a;
		if( ataqueExtra > MAX_FORCA )
			ataqueExtra = MAX_FORCA;
	}
	/** permite incementar/decrementar ao nível de velocidade
	 * @param d valor a incrementar a velocidade
	 */
	public void somaVelocidadeExtra(int v) {
		velocidadeExtra += v;
		if( velocidadeExtra > MAX_VELOCIDADE )
			velocidadeExtra = MAX_VELOCIDADE;
	}
	
	/** devolve a capacidade extra de população que a equipa permite  
	 * @return a capacidade extra de população que a equipa permite 
	 */
	public int getMaxPopExtra() {
		return excessoPop + excessoPopExtra;
	}
	
	/** permite alterar o valor da capacidade extra de população que a equipa permite
	 * @param factor factor a acrescentar/diminuir ao valor da capacidade extra de população que a equipa permite
	 */
	public void somaMaxPopExtra( int factor ){
		excessoPopExtra += factor;
		if( excessoPopExtra > MAX_POP_EXTRA )
			excessoPopExtra = MAX_POP_EXTRA;
	}

	/** retorna a comida disponível
	 * @return a comida disponível
	 */
	public int getComidaDisponivel() {
		return comidaExistente;
	}

	/** retorna a população da equipa.
	 * A população não conta com os soldados dos batalhõe, apenas com
	 * os habitantes dos edifícios
	 * @return a população da equipa
	 */
	public int getPopulacao() {
		return populacao;
	}
}
