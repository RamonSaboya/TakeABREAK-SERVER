package br.ufpe.cin.if678;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import br.ufpe.cin.if678.communication.BridgeManager;
import br.ufpe.cin.if678.communication.Listener;
import br.ufpe.cin.if678.communication.Reader;
import br.ufpe.cin.if678.communication.UserAction;
import br.ufpe.cin.if678.communication.Writer;
import br.ufpe.cin.if678.util.Pair;
import br.ufpe.cin.if678.util.Tuple;

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

	// Lista de usuários online
	private Set<Integer> onlineIDs;
	private HashMap<Integer, Pair<String, InetSocketAddress>> IDToNameAddress;
	private HashMap<String, Integer> nameToID;
	private HashMap<InetSocketAddress, Integer> addressToID;

	// Mapeamentos dos usuários e seus endereços com suas threads de leitura e escrita
	private HashMap<InetSocketAddress, Pair<Reader, Thread>> mapIDToReader; // Mapeia um endereço para o thread de leitura
	private HashMap<InetSocketAddress, Pair<Writer, Thread>> mapIDToWriter; // Mapeia um endereço para o thread de escrita

	// Gerenciador de novas conexões e sua thread
	private BridgeManager bridgeManager;
	private Thread thread;

	private Listener listener;

	private GroupManager groupManager;

	/**
	 * Construtor para iniciar a instância
	 */
	private ServerController() {
		// Inicia as variáveis dos mapeamentos
		this.mapIDToReader = new HashMap<InetSocketAddress, Pair<Reader, Thread>>();
		this.mapIDToWriter = new HashMap<InetSocketAddress, Pair<Writer, Thread>>();

		// Inicia a thread que administra novas conexões
		this.bridgeManager = new BridgeManager(this);
		this.thread = new Thread(bridgeManager);
		this.thread.start();

		this.listener = new Listener(this);

		this.groupManager = new GroupManager();

		// Lista de usuários online
		this.onlineIDs = new HashSet<Integer>();
		this.IDToNameAddress = new HashMap<Integer, Pair<String, InetSocketAddress>>();
		this.nameToID = new HashMap<String, Integer>();
		this.addressToID = new HashMap<InetSocketAddress, Integer>();
	}

	public Reader getReader(InetSocketAddress address) {
		return mapIDToReader.get(address).getFirst();
	}

	public Reader getReader(int ID) {
		return mapIDToReader.get(IDToNameAddress.get(ID).getSecond()).getFirst();
	}

	public Writer getWriter(InetSocketAddress address) {
		return mapIDToWriter.get(address).getFirst();
	}

	public Writer getWriter(int ID) {
		return mapIDToWriter.get(IDToNameAddress.get(ID).getSecond()).getFirst();
	}

	public Set<Map.Entry<InetSocketAddress, Pair<Writer, Thread>>> getWriters() {
		return mapIDToWriter.entrySet();
	}

	public GroupManager getGroupManager() {
		return groupManager;
	}

	public boolean isOnline(int ID) {
		return onlineIDs.contains(ID);
	}

	public HashMap<Integer, Pair<String, InetSocketAddress>> getIDToNameAddress() {
		return IDToNameAddress;
	}

	public HashMap<String, Integer> getNameToID() {
		return nameToID;
	}

	public HashMap<InetSocketAddress, Integer> getAddressToID() {
		return addressToID;
	}

	public String getAddressPort(InetSocketAddress address) {
		return address.getAddress().getHostAddress() + ":" + address.getPort();
	}

	/**
	 * Associa uma thread de leitura à um endereço address
	 * 
	 * @param address endereço do socket
	 * @param reader gerenciador de leitura
	 * @param readerThread thread do gerenciador
	 */
	public void setReaderThread(InetSocketAddress address, Reader reader, Thread readerThread) {
		mapIDToReader.put(address, new Pair<Reader, Thread>(reader, readerThread));
	}

	/**
	 * Associa uma thread de escrita à um endereço address
	 * 
	 * @param address endereço do socket
	 * @param writer gerenciador de escrita
	 * @param writerThread thread do gerenciador
	 */
	public void setWriterThread(InetSocketAddress address, Writer writer, Thread writerThread) {
		mapIDToWriter.put(address, new Pair<Writer, Thread>(writer, writerThread));
	}

	/**
	 * Avisa à thread de leitura que a conexão do socket foi encerrada
	 * 
	 * @param address endereço do socket
	 */
	public void clientDisconnect(InetSocketAddress address) {
		int ID = addressToID.get(address);

		mapIDToReader.get(address).getSecond().interrupt(); // Interrompe a thread de leitura (apenas segurança, thread já deve estar parada nesse ponto)
		mapIDToWriter.get(address).getFirst().forceStop(); // Força o encerramento da thread de escrita

		System.out.println("[LOG] USUÁRIO DESCONECTOU: <" + ID + ", " + IDToNameAddress.get(ID).getFirst() + ", " + getAddressPort(address) + ">");
	}

	@SuppressWarnings("unchecked")
	public void callEvent(InetSocketAddress address, UserAction action, Object object) {
		switch (action) {
		case REQUEST_USERNAME:
			listener.onUserConnect((String) object, address);
			break;
		case REQUEST_USER_LIST:
			listener.onUserListRequest(address);
			break;
		case GROUP_CREATE:
			listener.onGroupCreate((Pair<Integer, String>) object);
			break;
		case GROUP_ADD_MEMBER:
			listener.onGroupAddMember((Pair<String, Integer>) object);
			break;
		case SEND_MESSAGE:
			Pair<String, Object> pair = (Pair<String, Object>) object;
			listener.onGroupMessage(new Tuple<String, Integer, Object>(pair.getFirst(), addressToID.get(address), pair.getSecond()));
			break;
		}
	}

}
