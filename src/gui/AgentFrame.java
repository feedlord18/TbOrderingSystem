package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.UIManager;

public class AgentFrame extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4071249495128408761L;

	private static final Logger LOGGER = Logger.getGlobal();
	private static String LOGGER_HEADER;

	private JPanel backPanel;
	private JPanel tablePanel;
	private JPanel actionPanel;

	// properties
	private static final Color BACKGROUND_COLOR = new Color(255, 255, 255);
	private static final String TITLE_VALUE = "你的淘宝订单记录";
	private static final String ORDER_TEXT = "淘宝运单号";
	private static final String STATUS_TEXT = "状态： 发货/已签收";
	private static final String ACTION_TEXT = "添加，更新，删除淘宝运单号";
	private static final String ORDER_TEMP_TEXT = "输入你的淘宝运单号, 多个请换行隔开";
	private static final String FONT_STYLE = "Serif";
	
	// session temp
	private static String uname;

	public AgentFrame(String uname) {
		AgentFrame.uname = uname;
		// setup logger header
		LOGGER_HEADER = "[" + this.getClass().getSimpleName() + "]";
		log("create logger header value");
		log("initializing frame");
		// default on close;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// disable resize;
		setResizable(false);
		// initialize frame properties
		setSize(800, 500);
		// built in panel
		log("setup backdrop");
		backPanel = new JPanel();
		backPanel.setBackground(BACKGROUND_COLOR);
		backPanel.setLayout(null);
		backPanel.setSize(getSize());
		log("finished setup backdrop");
		// set components in panel
		setComponents();
		log("setup components");
		log("finished setup components");
		getContentPane().add(backPanel);
		setLocationRelativeTo(null);
		setVisible(true);
		log("frame finished");
	}

	private void setComponents() {
		// set table component
		JTextPane header = new JTextPane();
		header.setFont(new Font(FONT_STYLE, Font.BOLD, 16));
		header.setSize(180, 40);
		int header_x = 30; int header_y = 10;
		header.setLocation(header_x, header_y);
		header.setText(TITLE_VALUE);
		header.setEditable(false);
		
		JTextPane stats = new JTextPane();
		stats.setFont(new Font(FONT_STYLE, Font.PLAIN, 16));
		stats.setSize(180, 40);
		stats.setLocation(backPanel.getWidth() - 180 - 30, 10);
		stats.setText("总共运单号数量: " + getOrders().length);
		stats.setEditable(false);
		backPanel.add(stats);
		
		JTextPane watermark = new JTextPane();
		watermark.setFont(new Font(FONT_STYLE, Font.PLAIN, 10));
		watermark.setSize(180, 40);
		watermark.setLocation(0, backPanel.getHeight() - 40 - 20);
		watermark.setText("版本1.1");
		watermark.setEditable(false);
		backPanel.add(watermark);
		
		tablePanel = new JPanel();
		tablePanel.setSize(535, 380);
		int panel_x = 250; int panel_y = 10 + header.getHeight() + 10;
		tablePanel.setLocation(panel_x, panel_y);
		
		updateTableContainer();
		
		// setup order update component
		JTextPane actionPane = new JTextPane();
		actionPane.setText(ACTION_TEXT);
		actionPane.setSize(200, 40);
		actionPane.setLocation(30, panel_y);
		OrderAction[] opts = OrderAction.values();
		String[] optsValues = new String[opts.length];
		for (int i = 0; i < opts.length; i++) {
			optsValues[i] = opts[i].toString();
		}
		JComboBox<Object> dropdown = new JComboBox<Object>(optsValues);
		dropdown.setSize(100, 30);
		dropdown.setEditable(false);
		dropdown.setLocation(30, panel_y + actionPane.getHeight() + 5);
		dropdown.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				actionPanel.removeAll();
				actionPanel.revalidate();
				actionPanel.repaint();
				handleDynamicComponent(dropdown);
			}
			
			private void handleDynamicComponent(Object obj) {
				@SuppressWarnings("unchecked")
				String action = (String) ((JComboBox<Object>) obj).getSelectedItem();
				if (action.equals(OrderAction.ADD.toString())) {
					renderAdd(action);
				} else if (action.equals(OrderAction.DELETE.toString())) {
					renderDelete(action);
				} else if (action.equals(OrderAction.UPDATE.toString())) {
					renderUpdate(action);
				} else {
					log("unsupported enum type value: " + action);
				}
			}
			
			private void renderAdd(String action) {
				JTextPane orderPane = new JTextPane();
				orderPane.setText(ORDER_TEXT);
				orderPane.setSize(200, 40);
				orderPane.setEditable(false);
				orderPane.setLocation(0, 0);
				actionPanel.add(orderPane);
				
				JTextArea input = new JTextArea();
				input.setText(ORDER_TEMP_TEXT);
				input.setSize(200, 140);
				input.setLineWrap(true);
				input.setLocation(0, orderPane.getHeight());
				input.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
				JScrollPane scrollPanel = new JScrollPane(input);
				scrollPanel.setSize(200, 140);
				scrollPanel.setLocation(0, orderPane.getHeight());
				actionPanel.add(scrollPanel);
				
				JButton button = new JButton(action);
				button.setSize(150, 30);
				button.setLocation((actionPanel.getWidth() - 150) / 2, actionPanel.getHeight() - 40);
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						log(action);
						log("adding " + input.getText());
						try {
							if (RestHandlers.addOrders(uname, input.getText())) {
								input.setText("");
								updateTableContainer();
							} else {
								UIManager.put("OptionPane.background", Color.white);
								UIManager.put("Panel.background", Color.white);
								JOptionPane.showMessageDialog(new JFrame(), "未能成功添加运单号");
							}
						} catch (Exception err) {
							renderError(err);
						}
					}
					
				});
				actionPanel.add(button);
				
				backPanel.remove(actionPanel);
				backPanel.add(actionPanel);
			}
			
			private void renderDelete(String action) {
				JTextPane orderPane = new JTextPane();
				orderPane.setText(ORDER_TEXT);
				orderPane.setSize(200, 40);
				orderPane.setEditable(false);
				orderPane.setLocation(0, 0);
				actionPanel.add(orderPane);
				
				String[] orders = getOrders();
				JComboBox<Object> dropdown = new JComboBox<Object>(orders);
				dropdown.setSize(150, 30);
				dropdown.setEditable(false);
				dropdown.setLocation(0, orderPane.getHeight());
				actionPanel.add(dropdown);
				
				JButton button = new JButton(action);
				button.setSize(150, 30);
				button.setLocation(0, actionPanel.getHeight() - 40);
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						log(action);
						log("deleting " + (String) dropdown.getSelectedItem());
						try {
							if (RestHandlers.deleteOrders(uname, (String) dropdown.getSelectedItem())) {
								updateTableContainer();
								dropdown.removeItem(dropdown.getSelectedItem());
							} else {
								UIManager.put("OptionPane.background", Color.white);
								UIManager.put("Panel.background", Color.white);
								JOptionPane.showMessageDialog(new JFrame(), "未能成功删除运单号");
							}
						} catch (Exception err) {
							renderError(err);
						}
					}
				});
				actionPanel.add(button);
				
				backPanel.remove(actionPanel);
				backPanel.add(actionPanel);
			}
			
			private void renderUpdate(String action) {
				JTextPane orderPane = new JTextPane();
				orderPane.setText(ORDER_TEXT);
				orderPane.setSize(200, 40);
				orderPane.setEditable(false);
				orderPane.setLocation(0, 0);
				actionPanel.add(orderPane);
				
				String[] orders = getOrders();
				JComboBox<Object> dropdown = new JComboBox<Object>(orders);
				dropdown.setSize(150, 30);
				dropdown.setEditable(false);
				dropdown.setLocation(0, orderPane.getHeight());
				actionPanel.add(dropdown);

				JTextPane statusPane = new JTextPane();
				statusPane.setText(STATUS_TEXT);
				statusPane.setSize(200, 40);
				statusPane.setEditable(false);
				statusPane.setLocation(0, orderPane.getHeight() + 5 + dropdown.getHeight() + 5);
				actionPanel.add(statusPane);
				
				JComboBox<Object> status = new JComboBox<Object>(Status.getStatusBeans());
				status.setSize(150, 30);
				status.setEditable(false);
				status.setLocation(0, orderPane.getHeight() + 5 + dropdown.getHeight() + 5 + statusPane.getHeight());
				actionPanel.add(status);
				
				JButton button = new JButton(action);
				button.setSize(150, 30);
				button.setLocation(0, actionPanel.getHeight() - 40);
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						log(action);
						log("deleting " + (String) dropdown.getSelectedItem());
						try {
							if (RestHandlers.updateOrders(uname, (String) dropdown.getSelectedItem(), (String) status.getSelectedItem())) {
								updateTableContainer();
							} else {
								UIManager.put("OptionPane.background", Color.white);
								UIManager.put("Panel.background", Color.white);
								JOptionPane.showMessageDialog(new JFrame(), "未能成功更新运单号");
							}
						} catch (Exception err) {
							renderError(err);
						}
					}
				});
				actionPanel.add(button);
				
				backPanel.remove(actionPanel);
				backPanel.add(actionPanel);
			}
			
			private void renderError(Exception exp) {
				// render request feedback
				log(exp.getMessage());
				UIManager.put("OptionPane.background", Color.white);
				UIManager.put("Panel.background", Color.white);
				JOptionPane.showMessageDialog(new JFrame(), exp.getMessage());
			}
		});
		
		actionPanel = new JPanel();
		actionPanel.setSize(200, 250);
		actionPanel.setLayout(null);
		actionPanel.setLocation(30, panel_y + actionPane.getHeight() + 5 + dropdown.getHeight() + 10);
		actionPanel.setBackground(BACKGROUND_COLOR);
		// add to back panel
		backPanel.add(actionPanel);
		backPanel.add(actionPane);
		backPanel.add(dropdown);
		backPanel.add(header);
	}
	
	private void updateTableContainer() {
		LOGGER.info("updating table container beans.");
		String[][] rec = RestHandlers.getOrders(uname);
		log(Arrays.deepToString(rec));
		String[] thead = { "订单号", "状态", "创建时间" };
		JTable table = new JTable(rec, thead) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 4567250736612889622L;

			public boolean editCellAt(int row, int column, EventObject e) {
				return false;
			}
		};
		table.setFont(new Font("Serif", Font.PLAIN, 14));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getTableHeader().setReorderingAllowed(false);
		table.setRowHeight(30);
		table.getColumnModel().getColumn(0).setMinWidth(200);
		table.getColumnModel().getColumn(1).setMinWidth(125);
		table.getColumnModel().getColumn(2).setMinWidth(175);
		table.setLocation(10, 10);
		JScrollPane scrollPanel = new JScrollPane(table);
		table.setPreferredScrollableViewportSize(new Dimension(500, 350));
		tablePanel.removeAll();
		tablePanel.revalidate();
		tablePanel.repaint();
		tablePanel.add(scrollPanel);
		tablePanel.setVisible(true);
		backPanel.remove(tablePanel);
		backPanel.add(tablePanel);
	}

	private String[] getOrders() {
		// get orders
		String[][] rec = RestHandlers.getOrders(uname);
		String[] orders = new String[rec.length];
		for (int i = 0; i < rec.length; i++) {
			orders[i] = rec[i][0];
		}
		return orders;
	}

	private static void log(String msg) {
		LOGGER.info(LOGGER_HEADER + ": " + msg);
	}
}
