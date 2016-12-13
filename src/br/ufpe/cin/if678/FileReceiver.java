package br.ufpe.cin.if678;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import br.ufpe.cin.if678.util.Tuple;

public class FileReceiver extends Thread {

	private static int TEMP_FILE_ID = 1;

	private Socket socket;
	private String groupName;
	private int senderID;
	private int tempFileName;
	private byte[] fileName;
	private long offset;
	private long length;

	public FileReceiver(Socket socket, String groupName, int senderID, int tempFileName, byte[] fileName, long offset, long length) {
		this.socket = socket;
		this.groupName = groupName;
		this.senderID = senderID;
		this.tempFileName = tempFileName == -1 ? TEMP_FILE_ID++ : -1;
		this.fileName = fileName;
		this.offset = offset;
		this.length = length;
	}

	@Override
	public void run() {
		try {
			InputStream IS = socket.getInputStream();
			DataOutputStream DOS = new DataOutputStream(socket.getOutputStream());

			FileOutputStream FOS = new FileOutputStream("data\\files\\" + tempFileName, offset != 0 ? true : false);

			DOS.writeInt(tempFileName);
			DOS.flush();

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
		
		Tuple<Integer, byte[], Long> fileInfo = new Tuple<Integer, byte[], Long>(tempFileName, fileName, length);
		ServerController.getInstance().queueFile(new Tuple<String, Integer, Object>(groupName, senderID, fileInfo));
	}

}
