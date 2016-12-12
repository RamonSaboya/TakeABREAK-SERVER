package br.ufpe.cin.if678;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class FileReceiver extends Thread {

	private static int TEMP_FILE_ID = 1;

	private Socket socket;
	private String groupName;
	private int senderID;
	private byte[] fileName;
	private long offset;
	private long length;

	public FileReceiver(Socket socket, String groupName, int senderID, byte[] fileName, long offset, long length) {
		this.socket = socket;
		this.groupName = groupName;
		this.senderID = senderID;
		this.fileName = fileName;
		this.offset = offset;
		this.length = length;
	}

	@Override
	public void run() {
		try {
			InputStream IS = socket.getInputStream();
			FileOutputStream FOS = new FileOutputStream("data\\files\\" + groupName + "-" + senderID + "-" + TEMP_FILE_ID);

			byte[] buffer = new byte[4 * 1024];

			int count;
			while ((count = IS.read(buffer)) > 0) {
				FOS.write(buffer, 0, count);
			}

			FOS.close();
			IS.close();

			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
