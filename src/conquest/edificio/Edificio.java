package conquest.edificio;

import java.awt.Graphics;
import java.awt.Point;

import conquest.mundo.Batalhao;
import conquest.mundo.Equipa;
import conquest.mundo.Mundo;

public interface Edificio {

	/** faz o processamento antes de começar cada turno, ou seja,
	 * no início de cada ciclo de processamento
	 */
	void comecaTurno();

	/** Indica quantas pessoas quer crescer
	 * @return quantas pessoas quer crescer
	 */
	int quantoQuerCrescer();

	/** Gere a população, aumentando ou diminuindo a mesma
	 * @param incrementoMax máximo que pode aumentar 
	 */
	void regenerarPop(int incrementoMax);

	/** Aumenta ou diminui a população de um dado valor, se possível.
	 * Ainda verifica se está em excesso populacional. Se es+tiver a
	 * população pode descer, apesar do incremento ser positivo.
	 * @param incrementoMax qual o máximo de que pode incrementar a população,
	 * pois o edifício pode ter condições para crescer mais, mas a equipa não
	 */
	void gerirPopulacao(int incrementoMax);

	/** Retorna a comida produzida por esta edificio
	 * @return a comida produzida por esta edificio
	 */
	int getComidaProduzida();

	/** cria um batalhão que vai sair da edificio
	 * @param dest o edifício ao qual se destina o batalhão
	 * @return o batalhao criado, se houver população para isso
	 */
	Batalhao recrutaBatalhao(Edificio dest);

	/** cria um batalhão que vai sair da quinta, alistando os habitantes
	 * @param dest o edifício ao qual se destina o batalhão
	 * @return o batalhao criado, se houver população para isso
	 */
	Batalhao alistaBatalhao(Edificio dest);

	/** Atualiza esta edificio. 
	 * Cada chamada a este método é um ciclo de processamento.
	 */
	void atualiza();

	/** recruta habitantes para um batalhão e devolve o número de habitantes alistados
	 * @return o número de habitantes alistados
	 */
	int recrutaHabitantes();

	/** alista habitantes para um batalhão e devolve o número de habitantes alistados
	 * @return o número de habitantes alistados
	 */
	int alistaHabitantes();

	/** A capacidade de defesa da edificio
	 * @return capacidade de defesa da edificio
	 */
	double getDefesa();

	/** muda a equipa a quem pertence a edificio
	 * @param novaEquipa a nova equipa da edificio
	 */
	void setEquipa(Equipa novaEquipa);

	/** Aumenta ou diminui a população.
	 * Usar este método em vez de um setPopulacao
	 */
	void transitoHabitantes(int habitantes);

	/** desenha a edificio
	 * @param g onde desenhar
	 */
	void desenhar(Graphics g);

	/** desenhar a bandeira da equipa por cima da edificio
	 * @param g onde desenhar
	 * @param p a posição de desenho
	 */
	void desenharBandeira(Graphics g, Point p);
	
	/**  a indica se uma coordenada está dentro do edificio
	 * @param ptcoordenada a verificar
	 * @return se uma coordenada esta dentro da área do edificio
	 */
	boolean estaDentro(Point pt);

	/** devolve o número de habitantes
	 * @return o número de habitantes
	 */
	int getPopulacao();

	/**
	 * devolve a posição da porta do edificio, isto é, o local de onde saem os soldados
	 * @return a posição da porta do edificio
	 */
	Point getPosicaoPorta();

	/** Devolve a equipa a que pertence
	 * @return  a equipa a que pertence
	 */
	Equipa getEquipa();

	/** devolve o número máximo de habitantes admitidos
	 * @return o número máximo de habitantes admitidos
	 */
	int getMaxPopulacao();

	/** Indica se está cheia, isto é, se tem a população
	 * igual ou maior que o máximo permitido
	 * @return se está cheia
	 */
	boolean estaCheia();

	/** Indica se a edificio está sobrelotada.
	 * Está sobrelotada se tiver uma população
	 * acima do máximo permitido 
	 * @return se está sobrelotada.
	 */
	boolean estaSobrelotada();

	/** indica quanta comida base é produzida
	 * @return quanta comida base é produzida
	 */
	int getComidaProduz();

	/** define a comida base produzida
	 * @param comidaProduz quanta comida de base produz
	 */
	void setComidaProduz(int comidaProduz);

	/** em que mundo está a edificio colocado
	 * @return mundo onde está a edificio colocada
	 */
	Mundo getMundo();

}