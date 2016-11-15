package br.ufpe.cin.if678;

import java.net.InetAddress;
import java.util.HashMap;

import br.ufpe.cin.if678.communication.BridgeManager;
import br.ufpe.cin.if678.communication.Reader;
import br.ufpe.cin.if678.communication.Writer;
import br.ufpe.cin.if678.util.Pair;

/**
 * Controla todas as threads de leitura e escrita dos sockets de cada cliente
 * 
 * @author Ramon
 */
public class ServerController {

	// Como estamos usando uma classe Singleton, precisamos da variável para salvar a instância
	private static ServerController INSTANCE = null;

	/**
	 * Retorna a instância inicianda da classe
	 * 
	 * @return instância da classe
	 */
	public static ServerController getInstance() {
		// Caso seja o primeiro uso, é necessário iniciar a instância
		if (INSTANCE == null) {
			INSTANCE = new ServerController();
		}

		return INSTANCE;
	}

	// Mapeamentos dos clientes e seus endereços com suas threads de leitura e escrita
	private HashMap<Integer, InetAddress> mapIDToIP; // Mapeia um ID de um cliente para seu IP atual
	private HashMap<InetAddress, Pair<Reader, Thread>> mapIPToRead; // Mapeia um IP Para o thread de leitura
	private HashMap<InetAddress, Pair<Writer, Thread>> mapIPToWrite; // Mapeia um IP para o thread de escrita

	// Gerenciador de novas conexões e sua thread
	private BridgeManager bridgeManager;
	private Thread thread;

	/**
	 * Construtor para iniciar a instância
	 */
	private ServerController() {
		// Inicia as variáveis dos mapeamentos
		mapIDToIP = new HashMap<Integer, InetAddress>();
		mapIPToRead = new HashMap<InetAddress, Pair<Reader, Thread>>();
		mapIPToWrite = new HashMap<InetAddress, Pair<Writer, Thread>>();

		// Inicia a thread que administra novas conexões
		this.bridgeManager = new BridgeManager(this);
		this.thread = new Thread(bridgeManager);
		thread.start();
	}

	/**
	 * Associa uma thread de leitura à um endereço IP
	 * 
	 * @param IP endereço IP do socket
	 * @param reader gerenciador de leitura
	 * @param readerThread thread do gerenciador
	 */
	public void setReaderThread(InetAddress IP, Reader reader, Thread readerThread) {
		mapIPToRead.put(IP, new Pair<Reader, Thread>(reader, readerThread));
	}

	/**
	 * Associa uma thread de escrita à um endereço IP
	 * 
	 * @param IP endereço IP do socket
	 * @param writer gerenciador de escrita
	 * @param writerThread thread do gerenciador
	 */
	public void setWriterThread(InetAddress IP, Writer writer, Thread writerThread) {
		mapIPToWrite.put(IP, new Pair<Writer, Thread>(writer, writerThread));
	}

	/**
	 * Avisa à thread de leitura que a conexão do socket foi encerrada
	 * 
	 * @param IP endereço IP do socket
	 */
	public void clientDisconnect(InetAddress IP) {
		mapIPToRead.get(IP).getSecond().interrupt(); // Interrompe a thread de leitura (apenas segurança, thread já deve estar parada nesse ponto)
		mapIPToWrite.get(IP).getFirst().forceStop(); // Força o encerramento da thread de escrita
	}

}
