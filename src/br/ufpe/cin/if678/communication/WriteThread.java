package br.ufpe.cin.if678.communication;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import br.ufpe.cin.if678.util.Pair;

public class WriteThread implements Runnable {

	private Socket socket;

	private BlockingQueue<Pair<Action, Object>> queue;

	public WriteThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			while (true) {
				Pair<Action, Object> pair = queue.take();

				Action action = pair.getFirst();
				Object object = pair.getSecond();

				new ObjectOutputStream(socket.getOutputStream()).writeObject(action);
				new ObjectOutputStream(socket.getOutputStream()).writeObject(object);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void queueAction(Action action, Object object) {
		try {
			queue.put(new Pair<Action, Object>(action, object));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
