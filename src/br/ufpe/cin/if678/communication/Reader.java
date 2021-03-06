package br.ufpe.cin.if678.communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import br.ufpe.cin.if678.ServerController;

/**
 * Gerenciador de leitura de um socket
 * 
 * @author Ramon
 */
public class Reader implements Runnable {

	private ServerController controller;

	private InetSocketAddress address; // Endereço IP do socket
	private Socket socket;

	private ObjectInputStream OIS;

	/**
	 * Construtor do gerenciador de leitura
	 * 
	 * @param IP endereço IP do socket
	 * @param socket instância do socket
	 */
	public Reader(InetSocketAddress address, Socket socket) {
		this.controller = ServerController.getInstance();

		this.address = address;
		this.socket = socket;

		this.OIS = null;
	}

	/**
	 * Método que será executado pela thread
	 */
	@Override
	public void run() {
		while (true) {
			try {
				if (OIS == null) {
					OIS = new ObjectInputStream(socket.getInputStream());
				}

				// Lê a ação e o objecto que esteja relacionado a mesma
				UserAction action = (UserAction) OIS.readObject();
				Object object = OIS.readObject();

				controller.callEvent(address, action, object);
			} catch (SocketException e) {
				// Essa exeção será chamada quando o servidor não conseguir conexão com o cliente
				ServerController.getInstance().clientDisconnect(address); // Avisa ao controlador que o cliente desconectou
				return; // Encerra a execução da thread
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

}
