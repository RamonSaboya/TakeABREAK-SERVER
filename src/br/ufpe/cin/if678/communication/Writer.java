package br.ufpe.cin.if678.communication;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import br.ufpe.cin.if678.Controller;
import br.ufpe.cin.if678.util.Pair;

public class Writer implements Runnable {

	private InetAddress IP;
	private Socket socket;

	private BlockingQueue<Pair<Action, Object>> queue;

	private volatile boolean run;

	public Writer(InetAddress IP, Socket socket) {
		this.IP = IP;
		this.socket = socket;

		this.queue = new LinkedBlockingQueue<Pair<Action, Object>>();

		this.run = true;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Pair<Action, Object> pair = queue.poll(100, TimeUnit.MILLISECONDS);

				if (pair == null) {
					if (!run && queue.isEmpty()) {
						return;
					}

					continue;
				}

				Action action = pair.getFirst();
				Object object = pair.getSecond();

				ObjectOutputStream OOS = new ObjectOutputStream(socket.getOutputStream());
				OOS.writeObject(action);
				OOS.writeObject(object);
				OOS.flush();
			} catch (SocketException e) {
				Controller.getInstance().clientDisconnect(IP);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void queueAction(Action action, Object object) {
		try {
			queue.put(new Pair<Action, Object>(action, object));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		run = false;
	}

}
