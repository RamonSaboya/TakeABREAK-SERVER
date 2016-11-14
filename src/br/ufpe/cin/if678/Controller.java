package br.ufpe.cin.if678;

import java.net.InetAddress;
import java.util.HashMap;

import br.ufpe.cin.if678.communication.BridgeManager;
import br.ufpe.cin.if678.communication.ReadThread;
import br.ufpe.cin.if678.communication.WriteThread;

public class Controller {

	private static Controller INSTANCE = null;

	public static Controller getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new Controller();
		}
		return INSTANCE;
	}

	private HashMap<Integer, InetAddress> mapIDToIP;
	private HashMap<InetAddress, WriteThread> mapIPToWrite;
	private HashMap<InetAddress, ReadThread> mapIPToRead;

	private BridgeManager bridgeManager;

	private Controller() {
		mapIDToIP = new HashMap<Integer, InetAddress>();
		mapIPToWrite = new HashMap<InetAddress, WriteThread>();
		mapIPToRead = new HashMap<InetAddress, ReadThread>();

		this.bridgeManager = new BridgeManager(this);
	}

	public void setWriteThread(InetAddress IP, WriteThread writeThread) {
		mapIPToWrite.put(IP, writeThread);
	}

	public void setReadThread(InetAddress IP, ReadThread readThread) {
		mapIPToRead.put(IP, readThread);
	}

}
