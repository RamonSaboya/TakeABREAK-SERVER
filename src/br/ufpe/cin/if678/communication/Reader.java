package br.ufpe.cin.if678.communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import br.ufpe.cin.if678.Controller;

public class Reader implements Runnable {

	private InetAddress IP;
	private Socket socket;

	public Reader(InetAddress IP, Socket socket) {
		this.IP = IP;
		this.socket = socket;
	}

	@Override
	public void run() {
		while (true) {
			try {
				ObjectInputStream OIS = new ObjectInputStream(socket.getInputStream());

				Action action = (Action) OIS.readObject();
				Object object = OIS.readObject();

				System.out.println(action.getID() + ": " + ((String) object));
			} catch (SocketException e) {
				Controller.getInstance().clientDisconnect(IP);
				return;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

}
