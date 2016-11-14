package br.ufpe.cin.if678;

import java.net.InetAddress;
import java.util.HashMap;

import br.ufpe.cin.if678.communication.BridgeManager;
import br.ufpe.cin.if678.communication.Reader;
import br.ufpe.cin.if678.communication.Writer;
import br.ufpe.cin.if678.util.Pair;

public class ServerController {

	private static ServerController INSTANCE = null;

	public static ServerController getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ServerController();
		}
		return INSTANCE;
	}

	@SuppressWarnings("unused")
	private HashMap<Integer, InetAddress> mapIDToIP;
	private HashMap<InetAddress, Pair<Writer, Thread>> mapIPToWrite;
	private HashMap<InetAddress, Pair<Reader, Thread>> mapIPToRead;

	private BridgeManager bridgeManager;

	private ServerController() {
		mapIDToIP = new HashMap<Integer, InetAddress>();
		mapIPToWrite = new HashMap<InetAddress, Pair<Writer, Thread>>();
		mapIPToRead = new HashMap<InetAddress, Pair<Reader, Thread>>();

		this.bridgeManager = new BridgeManager(this);
		new Thread(bridgeManager).start();
	}

	public void setWriterThread(InetAddress IP, Writer writer, Thread writerThread) {
		mapIPToWrite.put(IP, new Pair<Writer, Thread>(writer, writerThread));
	}

	public void setReaderThread(InetAddress IP, Reader reader, Thread readerThread) {
		mapIPToRead.put(IP, new Pair<Reader, Thread>(reader, readerThread));
	}

	public void clientDisconnect(InetAddress IP) {
		mapIPToRead.get(IP).getSecond().interrupt();
		mapIPToWrite.get(IP).getFirst().forceStop();
		System.out.println("Encerrando IP: " + IP.getHostAddress());
	}

}
