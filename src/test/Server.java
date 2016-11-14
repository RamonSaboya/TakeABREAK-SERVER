package test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server implements Runnable {
	
	public static void main(String[] args) {
		Server server = new Server(6666);
		server.run();
	}

	private ServerSocket server;

	private HashMap<InetAddress, ClientProcess> process;

	public Server(int port) {
		this.process = new HashMap<InetAddress, ClientProcess>();

		try {
			this.server = new ServerSocket(port);

			System.out.println("[SERVIDOR] Servidor iniciado na porta: " + port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while (true) {
			try {
				Socket connection = server.accept();

				process.put(connection.getInetAddress(), new ClientProcess(this, connection));
				
				new Thread(process.get(connection.getInetAddress())).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void sendAll(String message) {
		for(ClientProcess process : this.process.values()) {
			process.send(message);
		}
	}

}
