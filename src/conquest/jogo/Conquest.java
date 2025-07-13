
package conquest.jogo;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.*;

import conquest.edificio.*;
import conquest.ia.IAPlayer;
import conquest.mundo.Batalhao;
import conquest.mundo.Equipa;
import conquest.mundo.Mundo;
import prof.jogos2D.util.Vector2D;

/**
 * A classe que controla todo o jogo
 */
public class Conquest extends JFrame {
	
	// elementos do jogo
	private Mundo mundo;  			// o mundo atual
	private int nivel; 				// nível atual do jogo
	
	private Equipa equipas[];      	// as equipas em jogo
	private Equipa equipaJogador;  	// a equipa do jogador

	private IAPlayer iaPlayers[] = new IAPlayer[2];  // a "inteligência artificial" das equipas do computador
	
	// TODO FEITO Suportar todos os tipos de edifícios
	private SeleccaoEdificios sel;  // edificios atualmente selecionadas como origem 
	private Edificio dest;  	        // edificio selecionado como destino
	private Point fim;          	// localização apontada atualmente

	// o leitor de níveis
	// os ficheiros de nível estão do diretório data/niveis
	// os ficheiros dos edificios estão no diretório data/edificios
	private LevelReader lr = new LevelReader( "data/niveis/", "data/edificios/" );
	
	// o gestor dos tempos
	private Timer temporizador;

	// os vários estados possíveis para o jogo
	private static final int JOGANDO = 0;
	private static final int VITORIA = 1;
	private static final int DERROTA = 2;
	private int status;  		         	// estado atual

	// delay entre detetar o fim do jogo e o jogo acabar realmente
	private int delayFinal;
	
	// indica se está em alistamento ou recrutamento
	private boolean alistamento;
	
	// variáveis para os vários elementos visuais do jogo
	private JPanel zonaJogo = null;
	
	// imagem usada para melhorar as animações
	private Image ecran;
	
	// estilo de fonte a usar nas informações
	private Font fonteInfos = new Font("Roman", Font.BOLD, 14 ); 
	
	// estilos de linha e efeito alfa para desenhar as linhas de deslocação 
	private Stroke estiloLinhaExterior = new BasicStroke(12.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
	private Stroke estiloLinhaInterior = new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
	private Composite alphaMeio = AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 0.3f );
	private Composite alphaFull = AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 0.6f );
	
	/** versão da aplicação (para não dar warnings) */
	private static final long serialVersionUID = 1L;

	/**
	 * construtor da aplicação
	 */
	public Conquest( ) {
		setTitle("ConquEST");
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		initialize();  // inicializações da janela
		comecarJogo();
	}	

	/** Método que começa o jogo
	 */
	private void comecarJogo(){
		// alterar o nível se quiserem testar um nível específico
		nivel = 1;     
		// jogar o nível
		jogarNivel();
	}
	

	/**
	 * Jogar um dado nível, inicializa as equipas e a "inteligência artificial".
	 * É melhor criar novamente as equipas para nao ter de as reconstruir
	 * depois do último nível jogado
	 */
	private void jogarNivel(){
		Equipa []es = { new Equipa("Romanos", 12,9,3,40,0),
				        new Equipa("Neutros", 0,7,0,0,0),
				        new Equipa("Astecas", 10,10,5,45,2),
				        new Equipa("Chineses", 9,12,4,35,4) };
		equipas = es;
		equipaJogador = equipas[0];
		mundo = lr.lerFicheiro( "nivel" + nivel + ".txt", equipas);
		// para testes podem usar usar o nível abaixo
//		mundo = lr.lerFicheiro( "teste.txt", equipas);
		
		// criar as IAs para as equipas controladas pelo PC
		for( int i=0; i < iaPlayers.length; i++ )
			iaPlayers[ i ] = new IAPlayer( mundo, equipas[ i+2 ] );

		status = JOGANDO;           // define que se está a jogar
		delayFinal = 30;
		sel = new SeleccaoEdificios( equipaJogador );
		
		// arrancar com o temporizador que vai atualizar o mundo 30 vezes por segundo
		temporizador.start();
	}

	/** 
	 * método chamado automaticamente para atualizar o jogo.
	 * Atenção! Este método NÃO desenha nada. Usar o método desenharJogo para isso
	 */
	private void actualizarJogo() {
		for( Equipa e : equipas )
			e.comecarTurno();		
		for( IAPlayer ia : iaPlayers ) 
			ia.jogar();		
		mundo.atualiza();
		testaFim(); // verificar se já acabou
	}
	
	/**
	 * Testa se o jogo chegou ao fim.
	 * Este método apenas muda o estado do jogo,
	 * NÃO apresenta nada no écran, nem termina o jogo
	 */
	private void testaFim(){
		// se já testou que acabou, ver se já fez o delay final
		if( status != JOGANDO ) {
			delayFinal--;
			if( delayFinal <= 0 ) {
				temporizador.stop();
				fimNivel();
			}
		}
		// TODO FEITO suportar os restantes edifícios
//		 se o jogador não tem edifícios vai sofrer uma derrota
		else if( equipaJogador.getNumEdificios() == 0 ){
			status = DERROTA; 
		}
		else {
			// se nenhuma equipa da IA tem edifícios o jogador é vencedor
			// os Neutros podem ficar com edifícios que conta na mesma como vitória
			// TODO FEITO suportar os restantes edifícios
			for( IAPlayer ia : iaPlayers )
				if( ia.getEquipa().getNumEdificios() != 0 )
					return;
			status = VITORIA;
		}
	}
	
	/**
	 * Método chamado quando se termina o nível	 
	 */
	private void fimNivel(){
		// se perdeu vai mostrar a mensagem de derrota e perguntar o que deseja fazer
		if( status == DERROTA) {
			String escolhas[] = {"Voltar a Jogar este Nível", "Voltar ao 1º nível", "Terminar Jogo" };
			int resposta = JOptionPane.showOptionDialog(  null, "Derrota estrondosa! Que deseja fazer?", "DERROTA", JOptionPane.YES_NO_OPTION,
					                                       JOptionPane.PLAIN_MESSAGE, null, escolhas, escolhas[0]  );
			switch( resposta ){
			case 0:
				jogarNivel();
				break;
			case 1:
				comecarJogo();
				break;
			case 2:
				System.exit( 0 );
			}
		}
		else {
			// se ganhou vai mostrar a mensagem de vitória e perguntar o que deseja fazer
			String escolhas[] = {"Voltar a Jogar este Nível", "Passar ao próximo nível", "Terminar Jogo" };
			int resposta = JOptionPane.showOptionDialog(  null, "Magnífica Vitória! Que deseja fazer?", "VITÓRIA", JOptionPane.YES_NO_OPTION,
					                                      JOptionPane.PLAIN_MESSAGE, null, escolhas, escolhas[1]  );
			switch( resposta ){
			case 0:
				jogarNivel();
				break;
			case 1:
				nivel++;
				jogarNivel();
				break;
			case 2:
				System.exit( 0 );
			}
		}
	}	
	
	/**
	 * Método chamado sempre que se pressiona o rato em cima do terreno de jogo 
	 * @param me evento associado ao rato
	 */
	private void ratoPremido( MouseEvent me ){
		fim = me.getPoint();
		alistamento = (me.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0;
		// TODO FEITO suportar os restantes edifícios
		// ver qual a edificio em que se clicou (se há alguma)
		sel.addEdificio( mundo.getEdificioAt( fim ) );
	}
	
	/**
	 * Método chamado quando o rato é arrastado no terreno de jogo
	 * @param me evento associado ao rato
	 */
	private void ratoArrastado( MouseEvent me ){
		// se não tem seleção é porque não quer fazer nada
		if( sel.estaVazia() )
			return;
		
		alistamento = (me.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0;		
		fim = me.getPoint();
		// TODO FEITO suportar os restantes edifícios
		// ver se selecionou mais algum edificio
		EdificioDefault e = mundo.getEdificioAt( fim );
		if( e!= null && e.getEquipa() == equipaJogador )
			sel.addEdificio( e );
		dest = e;
	}
	
	/**
	 * Método chamado quando o rato é libertado em cima do terreno de jogo
	 * @param me evento associado ao rato
	 */
	private void ratoLibertado( MouseEvent me ){
		// se não tem seleção é porque não quer fazer nada
		if( sel.estaVazia() )
			return;
		
		alistamento = (me.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0;

		// TODO FEITO suportar os restantes edifícios
		// ver qual o edificio de destino
		dest = mundo.getEdificioAt( me.getPoint() );
		// se o destino existe, é preciso mover os batalhões
		if( dest != null ) {
			for( Edificio e : sel.getEdificios() ) {
				// Criar um batalhão no edificio de origem
				Batalhao b;
				if( alistamento )
					b = e.alistaBatalhao( dest );
				else
					b = e.recrutaBatalhao( dest );
				// se o batalhão foi criado é adicionado ao mundo
				if( b != null )
					mundo.addBatalhao( b );
			}
		}
		// desselecionar origem(ns) e destino
		sel.limpar();
		dest = null;
	}

	/**
	 * Método que vai ser usado para desenhar os elementos do jogo
	 * QUALQUER DESENHO DEVE SER FEITO AQUI
	 * @param g ambiente gráfico onde se vai desenhar
	 */
	private void desenharJogo( Graphics2D g ){
		// Usar um graphics2D da imagem auxiliar
		Graphics2D ge = (Graphics2D )ecran.getGraphics();

		// desenhar o mundo
		mundo.desenhar( ge );
		desenharDeslocamento( ge );
		
		// desenhar a info da equipa do jogador
		ge.setColor( Color.black );
		ge.setFont( fonteInfos );
		
		ge.drawString("C:" + equipas[0].getComidaDisponivel(), 10, 15 );
		ge.drawString("P:" + equipas[0].getPopulacao(), 10, 30 );
		ge.drawString("R:" + equipas[0].getRegeneracao(), 10, 45 );
		ge.drawString("A:" + equipas[0].getAtaque(), 70, 15 );
		ge.drawString("D:" + equipas[0].getDefesa(), 70, 30 );
		ge.drawString("V:" + equipas[0].getVelocidade(), 70, 45 );
		
		// agora que está tudo desenhado na imagem auxiliar, desenhar no ecrán
		g.drawImage( ecran, 0, 0, null );		
	}

	/**
	 * método auxiliar para desenhar a linha de deslocamento
	 * @param g onde desenhar
	 */
	private void desenharDeslocamento( Graphics2D g ) {
		// se a seleção está vazia não faz nada
		if( sel.estaVazia() )
			return;
		
		// como se vai mudar os estilos de linha é preciso usar um ambiente alternativo
		Graphics2D ge = (Graphics2D)g.create();
		
		// TODO FEITO suportar os restantes edifícios
		// vai desenhar desde a porta do edificio seleccionada até ao destino
		for( Edificio e : sel.getEdificios() ) {
			Point ini = e.getPosicaoPorta();
			Point fim = dest == null? this.fim: dest.getPosicaoPorta();
			
			Vector2D dir = new Vector2D( ini, fim );
			dir.normalizar();
			Vector2D dirD = dir.getOrtogonalDireita();
			dirD.escalar( 4 );
			Vector2D dirE = dir.getOrtogonalEsquerda();
			dirE.escalar( 4 );
			Point2D.Double i = new Point2D.Double( ini.x, ini.y );
			Point2D.Double i1 = dirD.aplicaPonto( i );
			Point2D.Double i2 = dirE.aplicaPonto( i );
			
			// cria as linhas e desenha em duas fases, uma mais grossa mas meio transparente
			// e outra mais fina e mais opaca, para dar um efeito "bonito"
			ge.setComposite( alphaMeio );
			ge.setStroke( estiloLinhaExterior );											
			Line2D.Double line1 = new Line2D.Double( i1, fim );
			Line2D.Double line2 = new Line2D.Double( i2, fim );
			// se está a alistar a linha de fora é vermelha, se
			// é recrutamento é amarela
			if( alistamento )
				ge.setPaint( Color.RED );
			else
				ge.setPaint( Color.YELLOW );
			ge.draw( line1 );
			ge.draw( line2 );
			ge.setComposite( alphaFull );
			ge.setStroke( estiloLinhaInterior );
			// se tem destino válido a linha de dentro é verde
			// senão é amarela
			if( dest != null )
				ge.setPaint(Color.GREEN);
			else 
				ge.setPaint(Color.YELLOW);
			ge.draw( line1 );
			ge.draw( line2 );
		}
		
		ge.dispose();
	}	

	
	/**
	 * Este método inicializa a zonaJogo, AQUI NÃO DEVEM ALTERAR NADA 	
	 */
	private JPanel getZonaJogo() {
		if (zonaJogo == null) {
			zonaJogo = new JPanel(){
				public void paintComponent(Graphics g) {
					desenharJogo( (Graphics2D)g );
				}
			};
			Dimension d = new Dimension(1000, 700);			
			zonaJogo.setPreferredSize( d );
			zonaJogo.setSize( d );
			zonaJogo.setMinimumSize( d );
			zonaJogo.setBackground(Color.pink);
			zonaJogo.addMouseListener( new MouseAdapter(){
				public void mousePressed(MouseEvent e) {
					ratoPremido( e );
				}
				public void mouseReleased(MouseEvent e) {
					ratoLibertado( e );
				}				
			});
			zonaJogo.addMouseMotionListener( new MouseMotionAdapter(){
				public void mouseDragged(MouseEvent e) {
					ratoArrastado( e );
				}
			});						
		}
		return zonaJogo;
	}

	/**
	 *  Inicializa a interface da aplicação, AQUI NÃO DEVEM ALTERAR NADA
	 */
	private void initialize() {
		// características da janela
		this.setLocationRelativeTo( null );
		this.setTitle("ConquEST");
		getContentPane().add(getZonaJogo(),BorderLayout.CENTER);
	    this.pack(); 	    
	    this.setResizable( false );
	    this.setLocationRelativeTo( null );
	    
	    // criar o temporizador
	    temporizador = new Timer( 33, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actualizarJogo();
				zonaJogo.repaint();
			}
		} );
		
		// criar a imagem para melhorar as animações e configurá-la para isso mesmo
		ecran = new BufferedImage( 1000,700, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D ge = (Graphics2D )ecran.getGraphics();		
		ge.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    ge.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);			    
	}
	
	public static void main( String args[] ){
		Conquest ce = new Conquest();
		ce.setVisible( true );
	}
}
