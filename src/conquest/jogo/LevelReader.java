package conquest.jogo;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;

import javax.swing.JOptionPane;

import conquest.edificio.EdificioDefault;
import conquest.edificio.Forte;
import conquest.edificio.Quinta;
import conquest.edificio.Vila;
import conquest.edificio.Templo;

import conquest.mundo.Equipa;
import conquest.mundo.Mundo;
import prof.jogos2D.image.ComponenteSimples;
import prof.jogos2D.image.ComponenteVisual;

/** Classe responsável por ler os ficheiros de nível
 */
public class LevelReader {

	private String artDir;    // diretório onde estão as imagens dos edifícios
	private String nivelDir;  // diretório onde estão os ficheiros de nível
	
	/**
	 * Cria o leitor de ficheiros
	 * @param nivelDir diretório onde estão os ficheiros de nível
	 * @param artDir diretório onde estão as imagens dos ficheiros dos edifícios
	 */
	public LevelReader( String nivelDir, String artDir ) {
		this.nivelDir = nivelDir;
		this.artDir = artDir;
	}

	public Mundo lerFicheiro( String nivel, Equipa equipas[] ) {
		Mundo mundo = null;
		String file = nivelDir + nivel;     // ficheiro onde está o nível

		try( BufferedReader in = new BufferedReader( new FileReader( file )) ) {
			// abrir o ficheiro do nível			
			try {			
				// ler a informação da imagem do fundo do nível
				// ler a linha que tem o nome da imagem
				String fundoFile = nivelDir + in.readLine(); 
				ComponenteSimples fundo = new ComponenteSimples( fundoFile );

				// criar o mundo
				mundo = new Mundo( fundo ); 
				
				// agora tem-se de ler a informação sobre cada um dos edificios
				// cada linha tem a info:
				// edificio, posicao x, posicao y, população inicial, equipa a que pertence
				String line = in.readLine();
				while( line != null ){
					// ver se é uma linha vazia ou um comentário
					if( line.isBlank() || line.startsWith("%") ){
						line = in.readLine();
						continue;
					}
					// como as informações estão separadas por ',' usamos o split
					String info[] = line.split(",");
					String edif = info[0].toLowerCase();   // nome do edifício
					int x = Integer.parseInt( info[1] );   // posição x
					int y = Integer.parseInt( info[2] );   // posição y
					Point pos = new Point( x, y );
					int pop = Integer.parseInt( info[3] );        // população inicial
					int equipaNum = Integer.parseInt( info[4] );  // índice da equipa
					Equipa equipa = equipas[ equipaNum ];
					String edifArtFile = artDir + edif +".gif";   // ficheiro com a imagem do edifício
					ComponenteSimples img = new ComponenteSimples( pos, edifArtFile );					
					EdificioDefault edificioCriado = null;
					// TODO FEITO suportar os restantes edifícios
					// de acordo com a designação do edifício vai-se criar o edificio correspondente
					switch (edif) {
				    case "aldeia":
				        edificioCriado = criarAldeia(mundo, pop, equipa, img);
				        break;
				    case "vila":
				        edificioCriado = criarVila(mundo, pop, equipa, img);
				        break;
				    case "cidade":
				        edificioCriado = criarCidade(mundo, pop, equipa, img);
				        break;
				    case "campo":
				        edificioCriado = criarCampo(mundo, pop, equipa, img);
				        break;
				    case "quinta":
				        edificioCriado = criarQuinta(mundo, pop, equipa, img);
				        break;
				    case "herdade":
				        edificioCriado = criarHerdade(mundo, pop, equipa, img);
				        break;
				    case "altar":
				        edificioCriado = criarAltar(mundo, pop, equipa, img);
				        break;
				    case "templo":
				        edificioCriado = criarTemplo(mundo, pop, equipa, img);
				        break;
				    case "santuario":
				        edificioCriado = criarSantuario(mundo, pop, equipa, img);
				        break;
				    case "torre":
				        edificioCriado = criarTorre(mundo, pop, equipa, img);
				        break;
				    case "forte":
				        edificioCriado = criarForte(mundo, pop, equipa, img);
				        break;
				    case "castelo":
				        edificioCriado = criarCastelo(mundo, pop, equipa, img);
				        break;
				}
					// TODO Feito suportar os restantes edifícios
					
					// se o edifício existe vai ser adicionado ao mundo
					if( edificioCriado != null )
						mundo.addEdificio( edificioCriado );						
					line = in.readLine();
				}				
			} catch( Exception e ){
				// caso tenha acontecido algo errado ao ler o ficheiro de nível
				e.printStackTrace();
				JOptionPane.showMessageDialog( null, "Erro na leitura do ficheiro " + file, "ERRO", JOptionPane.ERROR_MESSAGE );
				System.exit( 1 );
				return null;
			}
		} catch( Exception e ){
			// caso tenha acontecido algo errado ao abrir o ficheiro de nível
			e.printStackTrace();
			JOptionPane.showMessageDialog( null, "Erro na abertura do ficheiro " + file, "ERRO", JOptionPane.ERROR_MESSAGE );
			System.exit( 1 );
			return null;
		}
		return mundo;
	}

	private Vila criarAldeia(Mundo mundo, int pop, Equipa equipa, ComponenteVisual img) {
		Vila v = new Vila( img, equipa, mundo, pop, 20 );
		v.setComidaProduz( 20 );
		return v;
	}
	
	private Vila criarVila(Mundo mundo, int pop, Equipa equipa, ComponenteVisual img) {
		Vila v = new Vila( img, equipa, mundo, pop, 40 );
		v.setComidaProduz( 10 );
		return v;
	}
	
	private Vila criarCidade(Mundo mundo, int pop, Equipa equipa, ComponenteVisual img) {
		Vila v = new Vila( img, equipa, mundo, pop, 60 );
		v.setComidaProduz( 0 );
		return v;
	}

	private Quinta criarCampo(Mundo mundo, int pop, Equipa equipa, ComponenteVisual img) {
		Quinta q = new Quinta( img, equipa, mundo, pop, 20 );
		q.setComidaProduz( 10 );
		return q;
	}
	
	private Quinta criarQuinta(Mundo mundo, int pop, Equipa equipa, ComponenteVisual img) {
		Quinta q = new Quinta( img, equipa, mundo, pop, 40 );
		q.setComidaProduz( 20 );
		return q;
	}
	
	private Quinta criarHerdade(Mundo mundo, int pop, Equipa equipa, ComponenteVisual img) {
		Quinta q = new Quinta( img, equipa, mundo, pop, 60 );
		q.setComidaProduz( 30 );
		return q;
	}
	
	private Templo criarAltar(Mundo mundo, int pop, Equipa equipa, ComponenteVisual img) {
		Templo t = new Templo( img, equipa, mundo, pop, 20 );
		t.setComidaProduz( 0 );
		return t;
	}
	
	private Templo criarTemplo(Mundo mundo, int pop, Equipa equipa, ComponenteVisual img) {
		Templo t = new Templo( img, equipa, mundo, pop, 40 );
		t.setComidaProduz( 10 );
		return t;
	}
	
	private Templo criarSantuario(Mundo mundo, int pop, Equipa equipa, ComponenteVisual img) {
		Templo t = new Templo( img, equipa, mundo, pop, 60 );
		t.setComidaProduz( 20 );
		return t;
	}
	
	private Forte criarTorre(Mundo mundo, int pop, Equipa equipa, ComponenteVisual img) {
		Forte f = new Forte( img, equipa, mundo, pop, 20 );
		f.setComidaProduz( 0 );
		return f;
	}
	
	private Forte criarForte(Mundo mundo, int pop, Equipa equipa, ComponenteVisual img) {
		Forte f = new Forte( img, equipa, mundo, pop, 40 );
		f.setComidaProduz( 0 );
		return f;
	}
	
	private Forte criarCastelo(Mundo mundo, int pop, Equipa equipa, ComponenteVisual img) {
		Forte f = new Forte( img, equipa, mundo, pop, 60 );
		f.setComidaProduz( 0 );
		return f;
	}
}
