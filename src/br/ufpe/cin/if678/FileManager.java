package br.ufpe.cin.if678;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import br.ufpe.cin.if678.communication.ServerAction;

public class FileManager extends Thread {

	private ServerController controller;

	private ServerSocket serverSocket;

	private String groupName;
	private int senderID;
	private int tempFileName;
	private byte[] fileName;
	private long offset;
	private long length;

	public FileManager(ServerController controller) {
		this.controller = controller;

		try {
			this.serverSocket = new ServerSocket(4848);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				Socket socket = serverSocket.accept();

				new FileReceiver(socket, groupName, senderID, tempFileName, fileName, offset, length).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void listenFor(String groupName, int senderID, int tempFileName, byte[] fileName, long offset, long length) {
		this.groupName = groupName;
		this.senderID = senderID;
		this.tempFileName = tempFileName;
		this.fileName = fileName;
		this.offset = offset;
		this.length = length;

		controller.getWriter(senderID).queueAction(ServerAction.START_UPLOAD, null);
	}

}
