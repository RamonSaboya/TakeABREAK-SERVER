package br.ufpe.cin.if678.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Queue;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;

import br.ufpe.cin.if678.ServerController;
import br.ufpe.cin.if678.util.Pair;
import br.ufpe.cin.if678.util.Tuple;

@SuppressWarnings("serial")
public class TakeABREAKServer extends JFrame implements Runnable {

	public static final Color BACKGROUND_COLOR = new Color(220, 220, 220);

	private JTable table;
	private JScrollPane scrollPane;
	
	private String[] titles = {"Nome", "IP", "Status"};
	private String[][] data;
	private ServerController controller;
	
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

		table = new JTable(data, titles);
		
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
	
	public void run(){
		getInfo();
		
		DefaultTableModel dtm = new DefaultTableModel();
		dtm.addRow(data);
		table.setModel(dtm);
	}
	
	public void getInfo(){
		HashMap<String, Integer> nameToID = controller.getNameToID();
		Set<String> nameSet = nameToID.keySet();
		String[] name = new String[nameSet.size()];
		nameSet.toArray(name);
		HashMap<Integer, Pair<String, InetSocketAddress>> IDtoNameAdress = controller.getIDToNameAddress();

		data = new String[3][name.length];
		for(int i = 0; i < name.length; i++){
			data[i][0] = name[i];
			data[i][1] = IDtoNameAdress.get(nameToID.get(name[i])).getFirst();
			data[i][2] = controller.getQueuedMessages(nameToID.get(name[i])).isEmpty() ? "0" : "1" ;
		}
		
	}
}
