package br.ufpe.cin.if678.communication;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import br.ufpe.cin.if678.Controller;

public class BridgeManager implements Runnable {
	
	private Controller controller;
	private ServerSocket serverSocket;

	public BridgeManager(Controller controller) {
		this.controller = controller;
		
		try {
			this.serverSocket = new ServerSocket(6666);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			while (true) {
				Socket socket = serverSocket.accept();

				InetAddress IP = socket.getInetAddress();
				WriteThread writeThread = new WriteThread(socket);
				ReadThread readThread = new ReadThread(socket);
				
				controller.setWriteThread(IP, writeThread);
				controller.setReadThread(IP, readThread);

				new Thread(writeThread).start();
				new Thread(readThread).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
