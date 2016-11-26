package br.ufpe.cin.if678.communication;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import br.ufpe.cin.if678.ServerController;
import javafx.util.Pair;

/**
 * Gerenciador de escrita de um socket
 * 
 * @author Ramon
 */
public class Writer implements Runnable {

	private InetSocketAddress address; // Endereço IP do socket
	private ObjectOutputStream OOS; // Interface de saída de objetos

	/*
	 * Fila bloqueante de ações a serem executadas
	 * A intenção de utilizar uma fila bloqueante
	 * é evitar que as threads passem por espera
	 * ociosa
	 */
	private BlockingQueue<Pair<ServerAction, Object>> queue;

	/*
	 * Boolean para controlar execução da thread
	 * Utilização do modificador volatile para
	 * garantir que, no acesso, o valor seja
	 * buscado diretamente da memória. Necessário,
	 * pois será modificado a partir de chamada
	 * de outra thread
	 */
	private volatile boolean run;

	/**
	 * Construtor do gerenciador de escrita
	 * 
	 * @param IP endereço de IP do socket
	 * @param socket instância do socket
	 */
	public Writer(InetSocketAddress address, Socket socket) {
		this.address = address;

		// Tenta iniciar a interface de saída de objetos
		try {
			this.OOS = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Inicia a fila bloqueante com uma estrutura de LinkedList
		this.queue = new LinkedBlockingQueue<Pair<ServerAction, Object>>();

		// Inicialmente o thread irá rodar normalmente
		this.run = true;
	}

	/**
	 * Método que será executado pela thread
	 */
	@Override
	public void run() {
		while (run) {
			try {
				/*
				 * Tenta pegar a próxima ação da fila e,
				 * caso a mesma esteja vazia, aguarda 100
				 * milisegundos por uma possível inserção
				 */
				Pair<ServerAction, Object> pair = queue.poll(100, TimeUnit.MILLISECONDS);

				/*
				 * Caso, mesmo após a espera, não tenha
				 * ação disponível, é necessário verificar
				 * se a thread recebeu sinal de parada
				 */
				if (pair == null) {
					/*
					 * Caso tenha recebido sinal de parada
					 * e a fila esteja vazia, encerra a
					 * execução da thread
					 */
					if (!run && queue.isEmpty()) {
						return;
					}

					continue;
				}

				// Pega a ação e seu objeto
				ServerAction action = pair.getKey();
				Object object = pair.getValue();

				// Manda os objetos pela stream
				OOS.writeObject(action);
				OOS.writeObject(object);
				OOS.flush();
			} catch (SocketException e) {
				// Essa exeção será chamada quando o servidor não conseguir conexão com o cliente
				ServerController.getInstance().clientDisconnect(address); // Avisa ao controlador que o cliente desconectou
				return; // Encerra a execução da thread
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Tenta inserir uma ação na fila de execução
	 * 
	 * @param action ID da ação que será executada
	 * @param object objeto referente a ação
	 */
	public void queueAction(ServerAction action, Object object) {
		try {
			queue.put(new Pair<ServerAction, Object>(action, object));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// Marca o thread para encerrar a execução
	public void forceStop() {
		run = false;
	}

}
