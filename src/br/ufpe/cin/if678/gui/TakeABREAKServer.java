package br.ufpe.cin.if678.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;

import br.ufpe.cin.if678.ServerController;

@SuppressWarnings("serial")
public class TakeABREAKServer extends JFrame {

	public static final Color BACKGROUND_COLOR = new Color(220, 220, 220);

	private static JTable table;
	private JScrollPane scrollPane;

	private String[] titles = { "Nome", "IP", "Status" };
	private String[][] data;
	private static ServerController controller;

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

//		table = new JTable(data, titles);

		scrollPane = new JScrollPane(table);
		scrollPane.setBounds(10, 11, 1174, 650);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		getContentPane().add(scrollPane);

		controller = ServerController.getInstance();

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				controller.exit();
			}
		});
	}

}
