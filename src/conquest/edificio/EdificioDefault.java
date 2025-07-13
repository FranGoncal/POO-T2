package conquest.edificio;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import conquest.mundo.Batalhao;
import conquest.mundo.Equipa;
import conquest.mundo.Mundo;
import prof.jogos2D.image.*;
public abstract class EdificioDefault implements Edificio {
	/**
	 * Representa um edifício. Neste momento apenas contempla as características de uma edificio.
	 */
	protected int populacao;             // população atual
	protected int maxPop;                // máxima população suportada (sem contar extras da equipa)
	protected ComponenteVisual imagem;   // imagem gráfica
	protected Equipa equipa;             // equipa a que pertence
	protected Mundo mundo;               // muindo onde está
	protected int comidaProduz;          // quanta comida produz (fora extras da população)

	
	
	
	/** Cria um edificio.
	 * @param imagem imagem do edificio
	 * @param equipa equipa a que pertence
	 * @param mundo mundo onde está inserido
	 * @param pop população com que começa
	 * @param maxPop população máxima que suporta
	 */
	public EdificioDefault(ComponenteVisual imagem, Equipa equipa, Mundo mundo, int pop, int maxPop) {
		this.mundo = mundo;
		this.imagem = imagem;
		this.populacao = pop;		
		this.maxPop = maxPop;
		this.equipa = equipa;
		this.equipa.addEdificio( this );
	}
	
	/** faz o processamento antes de começar cada turno, ou seja,
	 * no início de cada ciclo de processamento
	 */
	@Override
	public abstract void comecaTurno();

	/** Indica quantas pessoas quer crescer
	 * @return quantas pessoas quer crescer
	 */
	@Override
	public abstract int quantoQuerCrescer();

	/** Gere a população, aumentando ou diminuindo a mesma
	 * @param incrementoMax máximo que pode aumentar 
	 */
	@Override
	public void regenerarPop(int incrementoMax) {
		gerirPopulacao( incrementoMax );
	}

	/** Aumenta ou diminui a população de um dado valor, se possível.
	 * Ainda verifica se está em excesso populacional. Se es+tiver a
	 * população pode descer, apesar do incremento ser positivo.
	 * @param incrementoMax qual o máximo de que pode incrementar a população,
	 * pois o edifício pode ter condições para crescer mais, mas a equipa não
	 */
	@Override
	public abstract void gerirPopulacao(int incrementoMax) ;
	
	/** Retorna a comida produzida por esta edificio
	 * @return a comida produzida por esta edificio
	 */
	@Override
	public int getComidaProduzida() {
		return getComidaProduz();
	}
	
	/** cria um batalhão que vai sair da edificio
	 * @param dest o edifício ao qual se destina o batalhão
	 * @return o batalhao criado, se houver população para isso
	 */
	@Override
	public Batalhao recrutaBatalhao(Edificio dest) {
		int popBat = recrutaHabitantes();
		if( popBat == 0 )
			return null;		
		return criaBatalhao( dest, popBat );
	}

	/** cria um batalhão que vai sair da quinta, alistando os habitantes
	 * @param dest o edifício ao qual se destina o batalhão
	 * @return o batalhao criado, se houver população para isso
	 */
	@Override
	public Batalhao alistaBatalhao(Edificio dest) {
		int popBat = alistaHabitantes();
		if( popBat == 0 )
			return null;		
		return criaBatalhao( dest, popBat );
	}

	/** Cria um batalhao 
	 * @param dest edifício que vai ser atacado
	 * @param pop número de atacantes 
	 * @return o batalhão criado
	 */
	private Batalhao criaBatalhao(Edificio dest, int popBat) {
		return new Batalhao(equipa, getPosicaoPorta(), dest, popBat, equipa.getAtaque(), equipa.getVelocidade() );
	}
	
	/** Atualiza este edificio. 
	 * Cada chamada a este método é um ciclo de processamento.
	 */
	@Override
	public abstract void atualiza();
	
	/** recruta habitantes para um batalhão e devolve o número de habitantes alistados
	 * @return o número de habitantes alistados
	 */
	@Override
	public abstract int recrutaHabitantes();

	/** alista habitantes para um batalhão e devolve o número de habitantes alistados
	 * @return o número de habitantes alistados
	 */
	@Override
	public abstract int alistaHabitantes();

	/** A capacidade de defesa da edificio
	 * @return capacidade de defesa da edificio
	 */
	@Override
	public double getDefesa() {
		return equipa.getDefesa() * populacao;
	}
	
	/** muda a equipa a quem pertence a edificio
	 * @param novaEquipa a nova equipa da edificio
	 */
	@Override
	public void setEquipa(Equipa novaEquipa) {
		equipa.removeEdificio( this );
		equipa = novaEquipa;
		equipa.addEdificio( this );
	}
	
	/** Aumenta ou diminui a população.
	 * Usar este método em vez de um setPopulacao
	 */
	@Override
	public void transitoHabitantes(int habitantes) {
		populacao += habitantes;
		if( populacao <= 0 )
			populacao = 0;
	}

	/** desenha a edificio
	 * @param g onde desenhar
	 */
	@Override
	public void desenhar(Graphics g) {
		// desenhar a imagem do edificio
		imagem.desenhar( g );
		Point p = imagem.getPosicaoCentro();

		// desenhar a bandeira da equipa
		desenharBandeira(g, p);
	}

	/** desenhar a bandeira da equipa por cima da edificio
	 * @param g onde desenhar
	 * @param p a posição de desenho
	 */
	public void desenharBandeira(Graphics g, Point p) {
		ComponenteVisual bandeira = equipa.getBandeira();
		bandeira.setPosicaoCentro( new Point(p.x, imagem.getPosicao().y-8) );
		bandeira.desenhar( g );
		g.setColor( Color.BLACK );
		g.drawString( ""+populacao, p.x-(populacao>=10? 8: 4), imagem.getPosicao().y-3);
	}

	/**  a indica se uma coordenada está dentro do edificio
	 * @param ptcoordenada a verificar
	 * @return se uma coordenada esta dentro da área do edificio
	 */
	@Override
	public boolean estaDentro(Point pt) {	
		return imagem.getBounds().contains(pt);
	}

	/** devolve o número de habitantes
	 * @return o número de habitantes
	 */
	@Override
	public int getPopulacao() {
		return populacao;
	}

	/**
	 * devolve a posição da porta do edificio, isto é, o local de onde saem os soldados
	 * @return a posição da porta do edificio
	 */
	@Override
	public Point getPosicaoPorta() {
		int x = imagem.getPosicaoCentro().x;
		int y = imagem.getPosicao().y+imagem.getAltura(); 
		return new Point( x, y );
	}

	/** Devolve a equipa a que pertence
	 * @return  a equipa a que pertence
	 */
	@Override
	public Equipa getEquipa() {
		return equipa;
	}

	/** devolve o número máximo de habitantes admitidos
	 * @return o número máximo de habitantes admitidos
	 */
	@Override
	public int getMaxPopulacao() {
		return maxPop + equipa.getMaxPopExtra();
	}

	/** Indica se está cheia, isto é, se tem a população
	 * igual ou maior que o máximo permitido
	 * @return se está cheia
	 */
	@Override
	public boolean estaCheia() {
		return populacao == getMaxPopulacao();
	}

	/** Indica se a edificio está sobrelotada.
	 * Está sobrelotada se tiver uma população
	 * acima do máximo permitido 
	 * @return se está sobrelotada.
	 */
	@Override
	public boolean estaSobrelotada() {
		return populacao > getMaxPopulacao();
	}

	/** indica quanta comida base é produzida
	 * @return quanta comida base é produzida
	 */
	@Override
	public int getComidaProduz() {
		return comidaProduz;
	}

	/** define a comida base produzida
	 * @param comidaProduz quanta comida de base produz
	 */
	@Override
	public void setComidaProduz(int comidaProduz) {
		this.comidaProduz = comidaProduz;
	}

	/** em que mundo está a edificio colocado
	 * @return mundo onde está a edificio colocada
	 */
	@Override
	public Mundo getMundo() {
		return mundo;
	}

	

}
