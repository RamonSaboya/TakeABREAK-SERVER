package br.ufpe.cin.if678;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class FileSender extends Thread {

	private InetAddress address;
	private int tempFileName;

	public FileSender(InetAddress address, int tempFileName) {
		this.address = address;
		this.tempFileName = tempFileName;
	}

	@Override
	public void run() {
		try {
			Socket socket = new Socket(address.getHostAddress(), 1901);

			File file = new File("data\\files\\" + tempFileName);

			FileInputStream FIS = new FileInputStream(file);
			OutputStream OS = socket.getOutputStream();

			byte[] buffer = new byte[4 * 1024];

			int count;
			while ((count = FIS.read(buffer)) > 0) {
				OS.write(buffer, 0, count);
			}

			OS.close();
			FIS.close();

			socket.close();
		} catch (Exception e) {
		}
	}

}
