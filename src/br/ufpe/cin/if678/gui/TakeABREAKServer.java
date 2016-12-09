package br.ufpe.cin.if678.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import br.ufpe.cin.if678.ServerController;

@SuppressWarnings("serial")
public class TakeABREAKServer extends JFrame {

	public static final Color BACKGROUND_COLOR = new Color(220, 220, 220);

	/**
	 * Launch the application.
	 */

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TakeABREAKServer frame = new TakeABREAKServer();
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public TakeABREAKServer() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setBounds(0, 0, 1200, 700);
		setBackground(BACKGROUND_COLOR);
		getContentPane().setLayout(null);

		ServerController controller = ServerController.getInstance();

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				controller.exit();
			}
		});
	}
}
