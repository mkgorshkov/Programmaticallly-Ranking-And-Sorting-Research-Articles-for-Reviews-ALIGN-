package com.GUI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;

import com.SQL.DatabaseConnector;

/**
 * Main window responsible for holding the container.
 * 
 * @author mkgo
 * 
 */
public class MainFrame extends JFrame {

	JPanel loginScreen = new JPanel();
	JPanel projectsScreen = new JPanel();

	JMenuBar menuBar = new JMenuBar();
	JMenu menuFile;
	JPanel statusPanel = new JPanel();
	JLabel statusLabel = new JLabel("");
	JLabel usersLabel = new JLabel("Select User:");
	JButton connect = new JButton("Connect");
	JTable projects;

	JComboBox users;
	private DatabaseConnector db;

	String crtUser;

	public MainFrame() {
		makeConnection();

		setTitle("Article Quality Sorter");
		setSize(600, 400);
		setLocationRelativeTo(null);

		loginScreen.add(addGrid());
		setMenu();
		this.setJMenuBar(menuBar);
		setStatus();
		this.add(loginScreen);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void makeConnection() {
		db = new DatabaseConnector();
	}

	private void setMenu() {
		menuFile = new JMenu("File");
		JMenuItem exit = new JMenuItem("Exit");
		menuFile.add(exit);
		menuBar.add(menuFile);
	}

	private void setStatus() {
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		this.add(statusPanel, BorderLayout.SOUTH);
		statusPanel.setPreferredSize(new Dimension(this.getWidth(), 16));
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
		statusLabel.setText("Successfully connected to Database");
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		statusPanel.add(statusLabel);
	}

	private Panel addGrid() {
		Panel p = new Panel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

		try {
			BufferedImage image = ImageIO.read(new File("./res/drawing.png"));
			ImageIcon icon = new ImageIcon(image);
			JLabel label = new JLabel(icon);
			p.add(Box.createVerticalStrut(35));
			p.add(label);
			p.add(Box.createVerticalStrut(35));
			label.setAlignmentX(Component.CENTER_ALIGNMENT);
			p.add(usersLabel);
			usersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			addLogin();
			p.add(users);
			p.add(Box.createVerticalStrut(35));
			p.add(connect);
			connect.setAlignmentX(Component.CENTER_ALIGNMENT);
			connect.addActionListener(new addButtonListener());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return p;
	}

	private void addLogin() {
		users = new JComboBox(db.getUsers());
		users.setMaximumSize(new Dimension(300, 20));
		users.setAlignmentX(Component.CENTER_ALIGNMENT);
	}

	public void goToProjects() {

	}

	private void setProjScreen() {
		projectsScreen.setLayout(new BorderLayout());
		
		crtUser = users.getSelectedItem().toString();

		ArrayList<String[]> tableDataFull = db.getProjects(crtUser);

		String[] columnnames = { "Project Name", "Last Updated", "" };

		projects = new JTable();
		
		DefaultTableModel model = (DefaultTableModel) projects.getModel();
		model.addColumn("Project Name");
		model.addColumn("Last Updated");

		for (int i = 0; i < tableDataFull.size(); i++) {
			model.addRow(tableDataFull.get(i));
		}
		
		projectsScreen.add(new JScrollPane(projects));
	}

	class addButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JButton j = (JButton) e.getSource();
			if (j.getText().equals("Connect")) {
				JFrame frame = (JFrame) SwingUtilities.getRoot(j);
				frame.remove(loginScreen);
				statusLabel.setText("Projects Loading");
				setProjScreen();
				frame.add(projectsScreen);
				frame.validate();
				frame.repaint();
				statusLabel.setText("Projects Loaded");
			}
		}
	}
}
