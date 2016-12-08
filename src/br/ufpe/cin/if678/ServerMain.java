package br.ufpe.cin.if678;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ServerMain {

	public static void main(String[] args) {
		new ServerMain();
	}

	public ServerMain() {
		initialize();
	}

	private void initialize() {
		try {
			ServerController.getInstance();

			String IP = InetAddress.getLocalHost().getHostAddress();
			System.out.println("Servidor iniciado no IP: " + IP + ":" + ServerController.MAIN_PORT);

			System.out.println("Pronto para receber conexões");

			Scanner scanner = new Scanner(System.in);
			scanner.nextLine();

			ServerController.getInstance().exit();

			scanner.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

}
