package br.ufpe.cin.if678;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class RTTThread extends Thread {

	private InputStream inputStream;
	private OutputStream outputStream;

	public RTTThread(Socket socket) {
		try {
			this.inputStream = socket.getInputStream();
			this.outputStream = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				inputStream.read();

				outputStream.write(1);
			} catch (IOException ignore) {
			}
		}
	}

}
