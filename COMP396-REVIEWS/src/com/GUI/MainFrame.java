package com.GUI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.SQL.DatabaseConnector;

/**
 * Main window responsible for being the main container.
 * Summer 2014
 * @author Maxim Gorshkov
 * 
 */
public class MainFrame extends JFrame {

	JPanel loginScreen = new JPanel();
	JPanel projectsScreen = new JPanel();

	JMenuBar menuBar = new JMenuBar();
	JMenu menuFile;
	JMenuItem newProject;
	JMenuItem exit;
	JPanel statusPanel = new JPanel();
	JLabel statusLabel = new JLabel("");
	JLabel usersLabel = new JLabel("Select User:");
	JButton connect = new JButton("Connect");

	JTable projects;

	JComboBox users;
	
	JDialog projectCreate;
	private DatabaseConnector db;

	String crtUser;
	JTextField projectName;

	public MainFrame() {
		makeConnection();

		setTitle("Article Quality Sorter");
		setSize(600, 400);
		setLocationRelativeTo(null);

		loginScreen.add(addGrid());
		// setMenu();
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
		newProject = new JMenuItem("New Project");
		newProject.addActionListener(new addButtonListener());
		menuFile.add(newProject);
		
		exit = new JMenuItem("Exit to Login");
		exit.addActionListener(new addButtonListener());
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
			e.printStackTrace();
		}
		return p;
	}

	private void addLogin() {
		users = new JComboBox(db.getUsers());
		users.setMaximumSize(new Dimension(300, 20));
		users.setAlignmentX(Component.CENTER_ALIGNMENT);
	}
	
	private void createNewProject(){
		JButton accept = new JButton("OK");
		accept.addActionListener(new addButtonListener());
		Label newProjLabel = new Label("New Project Name:");
		projectCreate = new JDialog();
		projectCreate.setLayout(new BorderLayout());
		projectCreate.setTitle("Create New Project");
		projectCreate.add(newProjLabel);
		projectCreate.add(newProjLabel, BorderLayout.NORTH);
		projectName = new JTextField("");
		projectCreate.add(projectName, BorderLayout.CENTER);
		projectCreate.add(accept, BorderLayout.SOUTH);
		projectCreate.pack();
		projectCreate.setSize(200, 100);
		projectCreate.setLocationRelativeTo(null);
		projectCreate.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		projectCreate.setVisible(true);

	}

	private void setProjScreen() {
		projectsScreen.setLayout(new BorderLayout());

		crtUser = users.getSelectedItem().toString();

		final ArrayList<String[]> tableDataFull = db.getProjects(crtUser);

		String[] columnnames = { "Project Name", "Last Updated", " "};

		projects = new JTable(){
			public boolean isCellEditable(int row, int column){
				return false;
			}
		};
		
		projects.addMouseListener(new MouseAdapter() {
			  public void mouseClicked(MouseEvent e) {
			    if (e.getClickCount() == 2) {
			      JTable target = (JTable)e.getSource();
			      int row = target.getSelectedRow();
			      int column = target.getSelectedColumn();
			      
			      if(column == 2){
			    	  db.deleteProject(tableDataFull.get(row)[0], crtUser);
			    	  
			    	  JFrame frame = (JFrame) SwingUtilities.getRoot(projectsScreen.getParent());
						projectsScreen.removeAll();
						statusLabel.setText("Projects Refreshed");
						setProjScreen();
						frame.add(projectsScreen);
						frame.validate();
						frame.repaint();
			      }else{
			    	  goToProject(tableDataFull.get(row)[0]);
			      }
			    }
			  }
			});
		
		DefaultTableModel model = (DefaultTableModel) projects.getModel();

		model.addColumn("Project Name");
		model.addColumn("Last Updated");
		model.addColumn(" ");
		
		
		for (int i = 0; i < tableDataFull.size(); i++) {
			model.addRow(tableDataFull.get(i));
		}

		DefaultTableCellRenderer   render = new DefaultTableCellRenderer ();
		render.setHorizontalAlignment (SwingConstants.CENTER);
		projects.getColumnModel().getColumn(2).setCellRenderer(render);
		
		projects.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		projects.getColumnModel().getColumn(0).setPreferredWidth(400);
		projects.getColumnModel().getColumn(1).setPreferredWidth(100);
		
		projectsScreen.add(new JScrollPane(projects));
		
	}
	
	private void goToProject(String projectName) {
		System.out.println(projectName);
	}

	class addButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JButton j = null;
			JMenuItem i = null;

			if (e.getSource().getClass().equals(connect.getClass())) {
				j = (JButton) e.getSource();

				if (j.getText().equals("Connect")) {
					JFrame frame = (JFrame) SwingUtilities.getRoot(j);
					frame.remove(loginScreen);
					statusLabel.setText("Projects Loading");
					projectsScreen.removeAll();
					setProjScreen();

					frame.add(projectsScreen);
					frame.validate();
					frame.repaint();
					statusLabel.setText("Projects Loaded - Select project to continue...");
					
					menuBar.removeAll();
					setMenu();
				}
				if(j.getText().equals("OK")){
					projectCreate.setVisible(false);
					System.out.println(projectName.getText());
					boolean b = db.addProject(projectName.getText(), crtUser);	
					
					JFrame frame = (JFrame) SwingUtilities.getRoot(projectsScreen.getParent());
					projectsScreen.removeAll();
					statusLabel.setText("Projects Refreshed");
					setProjScreen();
					frame.add(projectsScreen);
					frame.validate();
					frame.repaint();
				}

			} else if (e.getSource().getClass().equals(exit.getClass())) {
				i = (JMenuItem) e.getSource();

				if (i.getText().equals("New Project")) {
					createNewProject();
				}
				if (i.getText().equals("Exit to Login")) {
					JFrame frame = (JFrame) SwingUtilities.getRoot(projectsScreen.getParent());
					frame.remove(projectsScreen);
					statusLabel.setText("Login Screen Loading");
					frame.add(loginScreen);
					frame.validate();
					frame.repaint();
					statusLabel.setText("Login Screen Loaded");
					menuBar.removeAll();
				}
			}

		}
	}
}
