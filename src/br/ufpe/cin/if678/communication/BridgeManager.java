package br.ufpe.cin.if678.communication;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import br.ufpe.cin.if678.ServerController;

/**
 * Classe de gerenciamento de novas conexões
 * 
 * @author Ramon
 */
public class BridgeManager implements Runnable {

	private ServerController controller; // Instância do controlador (não pode usar singleton, pois é chamado no construtor)
	private ServerSocket serverSocket;

	/**
	 * Construtor do gerenciador de novas conexões
	 * 
	 * @param controller instância do controlador
	 */
	public BridgeManager(ServerController controller) {
		this.controller = controller;

		// Tenta abrir o servidor do socket na porta 6666 (SAIKAPETTA)
		try {
			this.serverSocket = new ServerSocket(ServerController.MAIN_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Método que será executado pela thread
	 */
	@Override
	public void run() {
		while (true) {
			try {
				// Instrução que bloqueia a thread até o recebimento de alguma conexão
				Socket socket = serverSocket.accept();

				// Inicia os gerenciadores de leitura e escrita
				InetSocketAddress address = (InetSocketAddress) socket.getRemoteSocketAddress();

				System.out.println("[LOG] CONEXÃO INICIADA: " + address.getAddress().getHostAddress() + ":" + address.getPort());

				Reader reader = new Reader(address, socket);
				Writer writer = new Writer(address, socket);

				// Inicia as instâncias das threads de leitura e escrita
				Thread readerThread = new Thread(reader);
				Thread writerThread = new Thread(writer);

				// Passa as informações para que o controlador possa mapeá-las
				controller.setWriterThread(address, writer, writerThread);
				controller.setReaderThread(address, reader, readerThread);

				// Inicia a exeucão das threads de leitura e escrita
				readerThread.start();
				writerThread.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public int getServerPort() {
		return serverSocket.getLocalPort();
	}

}
