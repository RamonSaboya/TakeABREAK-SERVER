package test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientProcess implements Runnable {

	private Server server;
	private Socket connection;
	private DataOutputStream out;

	public ClientProcess(Server server, Socket connection) {
		this.server = server;
		this.connection = connection;
		try {
			this.out = new DataOutputStream(connection.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			DataInputStream input = new DataInputStream(connection.getInputStream());

			String nickname = input.readUTF();
			System.out.println("[SERVIDOR] Nick do cliente: " + nickname);
			System.out.println();

			String message;
			while (!(message = input.readUTF()).equals("EXIT;")) {
				server.sendAll(nickname + ": " + message);
				System.out.println(nickname + ": " + message);
			}

			System.out.println("[SERVIDOR] Cliente: " + nickname + " desconectou!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void send(String message) {
		try {
			out.writeUTF(message);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}