package br.ufpe.cin.if678.gui.panel;

import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import br.ufpe.cin.if678.gui.frame.TakeABREAK;

@SuppressWarnings("serial")
public class StartupPanel extends JPanel {

	private JLabel smallText;
	private JLabel bigText;

	private JButton loginButton;
	private JButton registerButton;

	private JLabel logoLabel;

	/**
	 * Cria o painel de entrada.
	 */
	public StartupPanel() {
		super();

		// Seta as características do painel
		setBounds(0, 0, 1200, 700);
		setBackground(TakeABREAK.BACKGROUND_COLOR);
		setLayout(null);

		// Label contendo a parte menor do slogan
		smallText = new JLabel();
		smallText.setForeground(Color.BLACK);
		smallText.setFont(new Font("Tahoma", Font.PLAIN, 32));
		smallText.setText("Take a");
		smallText.setVerticalAlignment(JLabel.BOTTOM);
		smallText.setBounds(65, 220, 200, 60);

		// Label contendo a parte grande do slogan
		bigText = new JLabel();
		bigText.setForeground(Color.BLACK);
		bigText.setFont(new Font("Tahoma", Font.PLAIN, 220));
		bigText.setText("BREAK;");
		bigText.setBounds(50, 200, 750, 300);

		// Botão de login
		loginButton = new JButton("SIGN IN");
		loginButton.setBounds(925, 50, 100, 25);

		// Botão de logout
		registerButton = new JButton("SIGN UP");
		registerButton.setBounds(1050, 50, 100, 25);

		// Logo
		logoLabel = new JLabel(new ImageIcon("dependencies\\logo.jpg"));
		logoLabel.setBounds(800, 175, 350, 350);

		// Insere todos os elementos no painel
		add(smallText);
		add(bigText);
		add(loginButton);
		add(registerButton);
		add(logoLabel);
	}
}
