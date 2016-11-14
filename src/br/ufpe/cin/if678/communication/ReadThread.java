package br.ufpe.cin.if678.communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ReadThread implements Runnable {

	private Socket socket;

	public ReadThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			while (true) {
				ObjectInputStream OIS = new ObjectInputStream(socket.getInputStream());

				Action action = (Action) OIS.readObject();
				Object object = OIS.readObject();

				System.out.println(action.getID() + ": " + ((String) object));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
