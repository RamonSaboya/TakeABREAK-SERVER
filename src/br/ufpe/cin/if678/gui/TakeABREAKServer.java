package br.ufpe.cin.if678.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;


import javax.swing.JLabel;

@SuppressWarnings("serial")
public class TakeABREAKServer extends JFrame {
	
	public static final Color BACKGROUND_COLOR = new Color(220, 220, 220);
	
	private JScrollPane scrollPane;
	private JPanel container;

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
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setBounds(0, 0, 1200, 700);
		setBackground(BACKGROUND_COLOR);
		getContentPane().setLayout(null);
	
		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 1164, 640);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		container = new JPanel();
		container.setLayout(null);
		container.setLocation(0, 0);
		container.setMinimumSize(new Dimension(869, 550));
		container.setPreferredSize(container.getMinimumSize());
		container.setBackground(BACKGROUND_COLOR);
		
		getContentPane().add(scrollPane);
	}
}
