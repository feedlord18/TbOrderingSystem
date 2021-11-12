package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class LoginFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2003917871330132958L;

	private static final Logger LOGGER = Logger.getGlobal();
	private static String LOGGER_HEADER;

	private JPanel backPanel;
	private JPanel inputPanel;

	// properties
	private static final Color BACKGROUND_COLOR = new Color(255, 255, 255);
	private static final int MAX_USERNAME_LENGTH = 16;
	private static final String TITLE_VALUE = "淘宝转运拼单记录";
	private static final String USERNAME_TEXT = "淘宝用户名： ";
	private static final String BUTTON_VALUE = "查看运单号";
	private static final String ADD_USER_TEXT = "用户名不存在，是否新添加收货人： ";
	private static final String EMPTY_USERNAME_TEXT = "用户名不能为空";
	private static final String ERR_ADD_USER_TEXT = "无法创建空户";
	private static final String FONT_STYLE = "Serif";

	public LoginFrame() {
		// setup logger header
		LOGGER_HEADER = "[" + this.getClass().getSimpleName() + "]";
		log("create logger header value");
		log("initializing frame");
		// default on close;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// disable resize;
		setResizable(false);
		// initialize frame properties
		setSize(500, 300);
		// built in panel
		log("setup backdrop");
		backPanel = new JPanel();
		backPanel.setBackground(BACKGROUND_COLOR);
		backPanel.setLayout(null);
		backPanel.setSize(getSize());
		log("finished setup backdrop");
		// set components in panel
		log("setup components");
		setComponents();
		log("finished setup components");
		getContentPane().add(backPanel);
		setLocationRelativeTo(null);
		setVisible(true);
		log("frame finished");
	}

	private void setComponents() {
		String specs = "<width, height>: " + backPanel.getWidth() + ", " + backPanel.getHeight();
		log(specs);
		// set header
		JTextPane header = new JTextPane();
		// center text
		StyledDocument doc = header.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);

		header.setText(TITLE_VALUE);		
		header.setFont(new Font(FONT_STYLE, Font.BOLD, 20));
		header.setSize(180, 40);
		int header_x; int header_y;
		header_x = (backPanel.getWidth() - header.getWidth()) / 2;
		header_y = 20;
		header.setLocation(header_x, header_y);
		header.setOpaque(true);
		header.setEditable(false);

		// initialize input pair container
		inputPanel = new JPanel();
		inputPanel.setSize(350, 200);
		inputPanel.setOpaque(false);
		int inputPanel_x; int inputPanel_y;
		int inputPanel_xOffset = -5; int inputPanel_yOffset = 30;
		inputPanel_x = (backPanel.getWidth() - inputPanel.getWidth()) / 2 + inputPanel_xOffset;
		inputPanel_y = header.getHeight() + inputPanel_yOffset;
		inputPanel.setLocation(inputPanel_x, inputPanel_y);
		inputPanel.setLayout(new SpringLayout());
		// set input pairs
		JTextPane userText = new JTextPane();
		userText.setEditable(false);
		userText.setText(USERNAME_TEXT);
		userText.setFont(new Font(FONT_STYLE, Font.PLAIN, 14));
		JTextField userInput = new JTextField(MAX_USERNAME_LENGTH);
		inputPanel.add(userText);
		inputPanel.add(userInput);
		// set order
		SpringLayout layout = (SpringLayout) inputPanel.getLayout();
		layout.putConstraint(SpringLayout.WEST, userText, 40, SpringLayout.WEST, inputPanel);
		layout.putConstraint(SpringLayout.NORTH, userText, 20, SpringLayout.NORTH, inputPanel);
		layout.putConstraint(SpringLayout.NORTH, userInput, 23, SpringLayout.NORTH, inputPanel);
		layout.putConstraint(SpringLayout.WEST, userInput, 0, SpringLayout.EAST, userText);
		// set login button
		backPanel.add(getJButton(userInput));
		// add components
		backPanel.add(inputPanel);
		backPanel.add(header);
	}

	/*
	 * returns the JButton object used to add orders
	 */
	private JButton getJButton(JTextField input) {
		JButton button = new JButton(BUTTON_VALUE);
		button.setSize(150, 40);
		int button_x; int button_y;
		int button_xOffset = 0; int button_yOffset = -100;
		button_x = (backPanel.getWidth() - button.getWidth()) / 2 + button_xOffset;
		button_y = backPanel.getHeight() - button.getHeight() + button_yOffset;
		button.setLocation(button_x, button_y);

		// set action listener
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					log("checking username: " + input.getText());
					if (input.getText().equals("")) {
						log("username is empty");
						UIManager.put("OptionPane.background", Color.white);
						UIManager.put("Panel.background", Color.white);
						JOptionPane.showMessageDialog(new JFrame(), EMPTY_USERNAME_TEXT);
					} else {
						log("send request to server");
						if (RestHandlers.isValidUsername(input.getText())) {
							renderFound(input.getText());
						} else {
							renderNotFound(input.getText());
						}
					}
				} catch (Exception err) {
					renderError(err);
				}
			}

			private void renderFound(String uname) {
				log("username found, login");
				dispose();
				new AgentFrame(uname);
			}

			private void renderNotFound(String uname) {
				// render request feedback
				log("username not found");
				UIManager.put("OptionPane.background", Color.white);
				UIManager.put("Panel.background", Color.white);
				int selection = JOptionPane.showConfirmDialog(new JFrame(), ADD_USER_TEXT + input.getText());
				log("selection " + String.valueOf(selection));
				if (selection == 0) {
					try {
						if (RestHandlers.registerUser(input.getText())) {
							dispose();
							new AgentFrame(uname);
						} else {
							JOptionPane.showMessageDialog(new JFrame(), ERR_ADD_USER_TEXT);
						}
					} catch (Exception err) {
						renderError(err);
					}
				}
			}

			private void renderError(Exception exp) {
				// render request feedback
				exp.printStackTrace();
				log(exp.getMessage());
				UIManager.put("OptionPane.background", Color.white);
				UIManager.put("Panel.background", Color.white);
				JOptionPane.showMessageDialog(new JFrame(), exp.getMessage());
			}

			private void log(String msg) {
				String header = "[ButtonAction]";
				LOGGER.info(header + ": " + msg);
			}
		});
		return button;
	}

	private static void log(String msg) {
		LOGGER.info(LOGGER_HEADER + ": " + msg);
	}
}
