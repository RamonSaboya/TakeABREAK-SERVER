package br.ufpe.cin.if678;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class RTTManager extends Thread {

	private ServerController controller;

	private ServerSocket serverSocket;

	private HashMap<Integer, Thread> userRTTThread;

	public RTTManager(ServerController controller) {
		this.controller = controller;

		this.userRTTThread = new HashMap<Integer, Thread>();

		try {
			this.serverSocket = new ServerSocket(4200);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				Socket socket = serverSocket.accept();

				InetSocketAddress address = (InetSocketAddress) socket.getRemoteSocketAddress();

				Thread thread = new RTTThread(socket);

				userRTTThread.put(controller.getAddressToID().get(address), thread);

				thread.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
