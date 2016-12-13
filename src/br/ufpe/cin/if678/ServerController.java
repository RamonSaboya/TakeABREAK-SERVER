package br.ufpe.cin.if678;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import br.ufpe.cin.if678.business.Group;
import br.ufpe.cin.if678.communication.BridgeManager;
import br.ufpe.cin.if678.communication.Listener;
import br.ufpe.cin.if678.communication.Reader;
import br.ufpe.cin.if678.communication.ServerAction;
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

	private FileManager fileManager;

	private RTTManager RTTManager;

	private Listener listener;

	private GroupManager groupManager;

	private HashMap<Integer, Queue<Tuple<String, Integer, Object>>> queuedMessages;

	private File serverDirectory;

	/**
	 * Construtor para iniciar a instância
	 */
	@SuppressWarnings("unchecked")
	private ServerController() {
		// Lista de usuários online
		this.onlineIDs = new HashSet<Integer>();
		this.IDToNameAddress = new HashMap<Integer, Pair<String, InetSocketAddress>>();
		this.nameToID = new HashMap<String, Integer>();
		this.addressToID = new HashMap<InetSocketAddress, Integer>();

		// Inicia as variáveis dos mapeamentos
		this.mapIDToReader = new HashMap<InetSocketAddress, Pair<Reader, Thread>>();
		this.mapIDToWriter = new HashMap<InetSocketAddress, Pair<Writer, Thread>>();

		// Inicia a thread que administra novas conexões
		this.bridgeManager = new BridgeManager(this);
		this.thread = new Thread(bridgeManager);
		this.thread.start();

		this.fileManager = new FileManager(this);
		this.fileManager.start();

		this.RTTManager = new RTTManager(this);
		this.RTTManager.start();

		this.listener = new Listener(this);

		this.groupManager = new GroupManager();

		this.queuedMessages = new HashMap<Integer, Queue<Tuple<String, Integer, Object>>>();

		this.serverDirectory = new File("data\\");
		serverDirectory.mkdirs();

		new File("data\\files\\").mkdirs();

		for (File file : serverDirectory.listFiles()) {
			if (file.getName().equals("messages.ser")) {
				try {
					FileInputStream FIS = new FileInputStream(file);
					ObjectInputStream OIS = new ObjectInputStream(FIS);

					queuedMessages = (HashMap<Integer, Queue<Tuple<String, Integer, Object>>>) OIS.readObject();

					OIS.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (file.getName().equals("groups.ser")) {
				try {
					FileInputStream FIS = new FileInputStream(file);
					ObjectInputStream OIS = new ObjectInputStream(FIS);

					groupManager.setGroups((HashMap<String, Group>) OIS.readObject());

					OIS.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (file.getName().equals("names.ser")) {
				try {
					FileInputStream FIS = new FileInputStream(file);
					ObjectInputStream OIS = new ObjectInputStream(FIS);

					nameToID = (HashMap<String, Integer>) OIS.readObject();

					OIS.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
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

	public FileManager getFileManager() {
		return fileManager;
	}

	public GroupManager getGroupManager() {
		return groupManager;
	}

	public boolean isOnline(int ID) {
		return onlineIDs.contains(ID);
	}

	public void setOnline(int ID) {
		onlineIDs.add(ID);
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

	public HashMap<Integer, Queue<Tuple<String, Integer, Object>>> getQueuedMessages() {
		return queuedMessages;
	}

	public Queue<Tuple<String, Integer, Object>> getQueuedMessages(int ID) {
		return queuedMessages.get(ID);
	}

	public void queueMessage(int ID, Tuple<String, Integer, Object> tuple) {
		if (!queuedMessages.containsKey(ID)) {
			queuedMessages.put(ID, new LinkedList<Tuple<String, Integer, Object>>());
		}

		queuedMessages.get(ID).add(tuple);
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
		mapIDToReader.get(address).getSecond().interrupt(); // Interrompe a thread de leitura (apenas segurança, thread já deve estar parada nesse ponto)
		mapIDToWriter.get(address).getFirst().forceStop(); // Força o encerramento da thread de escrita

		mapIDToReader.remove(address);
		mapIDToWriter.remove(address);

		if (!addressToID.containsKey(address)) {
			return;
		}

		int ID = addressToID.get(address);

		System.out.println("[LOG] USUÁRIO DESCONECTOU: <" + ID + ", " + IDToNameAddress.get(ID).getFirst() + ", " + getAddressPort(address) + ">");

		IDToNameAddress.remove(ID);
		addressToID.remove(address);

		onlineIDs.remove(ID);

		new Thread(new Runnable() {
			@Override
			public void run() {
				for (Map.Entry<InetSocketAddress, Pair<Writer, Thread>> entry : getWriters()) {
					getWriter(entry.getKey()).queueAction(ServerAction.USERS_LIST_UPDATE, IDToNameAddress.clone());
				}
			}
		}).start();

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
		case SEND_FILE:
			Tuple<String, Integer, Tuple<byte[], Long, Long>> data = (Tuple<String, Integer, Tuple<byte[], Long, Long>>) object;
			getFileManager().listenFor(data.getFirst(), data.getSecond(), data.getThird().getFirst(), data.getThird().getSecond(), data.getThird().getThird());
			break;
		case RECONNECT:
			listener.onReconnect((Tuple<Integer, String, InetSocketAddress>) object);
			break;
		}
	}

	public void exit() {
		File file;

		try {
			file = new File(serverDirectory, "messages.ser");

			FileOutputStream FOS = new FileOutputStream(file);
			ObjectOutputStream OOS = new ObjectOutputStream(FOS);

			OOS.writeObject(queuedMessages);

			OOS.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			file = new File(serverDirectory, "groups.ser");

			FileOutputStream FOS = new FileOutputStream(file);
			ObjectOutputStream OOS = new ObjectOutputStream(FOS);

			OOS.writeObject(groupManager.getGroups());

			OOS.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			file = new File(serverDirectory, "names.ser");

			FileOutputStream FOS = new FileOutputStream(file);
			ObjectOutputStream OOS = new ObjectOutputStream(FOS);

			OOS.writeObject(nameToID);

			OOS.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
