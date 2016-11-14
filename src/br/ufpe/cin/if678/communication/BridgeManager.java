package br.ufpe.cin.if678.communication;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import br.ufpe.cin.if678.ServerController;

public class BridgeManager implements Runnable {

	private ServerController controller;
	private ServerSocket serverSocket;

	public BridgeManager(ServerController controller) {
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
				Writer writer = new Writer(IP, socket);
				Reader reader = new Reader(IP, socket);

				Thread writerThread = new Thread(writer);
				Thread readerThread = new Thread(reader);

				controller.setWriterThread(IP, writer, writerThread);
				controller.setReaderThread(IP, reader, readerThread);
				
				writerThread.start();
				readerThread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
