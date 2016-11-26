package br.ufpe.cin.if678;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerMain {

	public static void main(String[] args) {
		try {
			ServerController.getInstance();

			String IP = InetAddress.getLocalHost().getHostAddress();
			System.out.println("Servidor iniciado no IP: " + IP + ":" + ServerController.MAIN_PORT);

			System.out.println("Pronto para receber conexões");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

}
