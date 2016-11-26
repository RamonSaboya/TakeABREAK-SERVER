package br.ufpe.cin.if678;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import br.ufpe.cin.if678.communication.BridgeManager;
import br.ufpe.cin.if678.communication.Reader;
import br.ufpe.cin.if678.communication.ServerAction;
import br.ufpe.cin.if678.communication.Writer;
import br.ufpe.cin.if678.util.Pair;

/**
 * Controla todas as threads de leitura e escrita dos sockets de cada cliente
 * 
 * @author Ramon
 */
public class ServerController {

	public static final int MAIN_PORT = 6666;

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

	// Mapeamentos dos usuários e seus endereços com suas threads de leitura e escrita
	private HashMap<InetSocketAddress, Pair<Reader, Thread>> mapAddressToRead; // Mapeia um endereço para o thread de leitura
	private HashMap<InetSocketAddress, Pair<Writer, Thread>> mapAddressToWrite; // Mapeia um endereço para o thread de escrita

	// Lista de usuários online
	private HashMap<InetSocketAddress, String> userList;

	// Gerenciador de novas conexões e sua thread
	private BridgeManager bridgeManager;
	private Thread thread;

	private GroupManager groupManager;

	/**
	 * Construtor para iniciar a instância
	 */
	private ServerController() {
		// Inicia as variáveis dos mapeamentos
		this.mapAddressToRead = new HashMap<InetSocketAddress, Pair<Reader, Thread>>();
		this.mapAddressToWrite = new HashMap<InetSocketAddress, Pair<Writer, Thread>>();

		// Lista de usuários online
		this.userList = new HashMap<InetSocketAddress, String>();

		// Inicia a thread que administra novas conexões
		this.bridgeManager = new BridgeManager(this);
		this.thread = new Thread(bridgeManager);
		this.thread.start();

		this.groupManager = new GroupManager(this);
	}

	/**
	 * Associa uma thread de leitura à um endereço address
	 * 
	 * @param address endereço do socket
	 * @param reader gerenciador de leitura
	 * @param readerThread thread do gerenciador
	 */
	public void setReaderThread(InetSocketAddress address, Reader reader, Thread readerThread) {
		mapAddressToRead.put(address, new Pair<Reader, Thread>(reader, readerThread));
	}

	/**
	 * Associa uma thread de escrita à um endereço address
	 * 
	 * @param address endereço do socket
	 * @param writer gerenciador de escrita
	 * @param writerThread thread do gerenciador
	 */
	public void setWriterThread(InetSocketAddress address, Writer writer, Thread writerThread) {
		mapAddressToWrite.put(address, new Pair<Writer, Thread>(writer, writerThread));
	}

	/**
	 * Avisa à thread de leitura que a conexão do socket foi encerrada
	 * 
	 * @param address endereço do socket
	 */
	public void clientDisconnect(InetSocketAddress address) {
		mapAddressToRead.get(address).getSecond().interrupt(); // Interrompe a thread de leitura (apenas segurança, thread já deve estar parada nesse ponto)
		mapAddressToWrite.get(address).getFirst().forceStop(); // Força o encerramento da thread de escrita
	}

	public void sendClientList(InetSocketAddress address) {
		mapAddressToWrite.get(address).getFirst().queueAction(ServerAction.SEND_USER_LIST, userList);
	}

	public void clientConnected(InetSocketAddress address, String username) {
		System.out.println("[LOG] USUÁRIO CONECTOU: " + username + " (" + address.getAddress().getHostAddress() + ":" + address.getPort() + ")");

		userList.put(address, username);

		Pair<InetSocketAddress, String> data = new Pair<InetSocketAddress, String>(address, username);
		for (Map.Entry<InetSocketAddress, Pair<Writer, Thread>> entry : mapAddressToWrite.entrySet()) {
			InetSocketAddress userAddress = entry.getKey();
			Writer writer = entry.getValue().getFirst();

			if (userAddress != address) {
				writer.queueAction(ServerAction.SEND_USER_CONNECTED, data);
			}
		}
	}

	public void createGroup(Pair<InetSocketAddress, String> data) {
		InetSocketAddress founder = data.getFirst();
		String name = data.getSecond();
	}

}
