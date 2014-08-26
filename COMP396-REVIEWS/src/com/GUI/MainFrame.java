package com.GUI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
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

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

import com.CMD.ImpactFactorFilter;
import com.SQL.DatabaseConnector;
import com.Scrape.ConnectPubMedTrends;
import com.Scrape.PubmedGraphs;

/**
 * Main window responsible for the main container.
 * Summer 2014
 * @author Maxim Gorshkov
 * 
 */
public class MainFrame extends JFrame {

	JPanel loginScreen = new JPanel();
	JPanel projectsScreen = new JPanel();
	JPanel sortingScreen = new JPanel();

	JMenuBar menuBar = new JMenuBar();
	JMenu menuFile;
	JMenuItem newProject;
	JMenuItem exit;
	JMenuItem exitProjects;
	JMenuItem manageFiles;
	JPanel statusPanel = new JPanel();
	JLabel statusLabel = new JLabel("");
	JLabel usersLabel = new JLabel("Select User:");
	JButton connect = new JButton("Connect");
	JButton newUserCreate = new JButton("New User");
	JButton addUserButton = new JButton("Add User");
	
	JTextField newUserNameName;

	JTable projects;
	JTable sortingTable;

	JComboBox<String> users;
	
	String projectNameValue;
	
	JDialog projectCreate;
	JDialog uploadFiles;
	JDialog googletrends;
	JDialog newUser;
	
	String sortingXMLFile;
	
	private DatabaseConnector db;

	String crtUser;
	JTextField projectName;
	JTextField fileDescription;

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
	
	private void setSortingMenu(){
		menuFile = new JMenu("File");
		manageFiles = new JMenuItem("Manage Files");
		manageFiles.addActionListener(new addButtonListener());
		menuFile.add(manageFiles);
		
		exitProjects = new JMenuItem("Exit to Projects");
		exitProjects.addActionListener(new addButtonListener());
		menuFile.add(exitProjects);
		
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
			p.add(Box.createVerticalStrut(15));
			p.add(connect);
			p.add(Box.createVerticalStrut(15));
			p.add(newUserCreate);
			newUserCreate.setAlignmentX(Component.CENTER_ALIGNMENT);
			newUserCreate.addActionListener(new addButtonListener());

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
	
	private void UploadFile(){
		JButton accept = new JButton("Add File");
		JButton choose = new JButton("Choose File");
		accept.addActionListener(new addButtonListener());
		choose.addActionListener(new addButtonListener());
		Label newProjLabel = new Label("File Description:");
		uploadFiles = new JDialog();
		uploadFiles.setLayout(new GridLayout(4,0));
		uploadFiles.setTitle("Add XML File");
		uploadFiles.add(choose);
		uploadFiles.add(newProjLabel);
		fileDescription = new JTextField("");
		uploadFiles.add(fileDescription);
		uploadFiles.add(accept);
		uploadFiles.pack();
		uploadFiles.setSize(200, 100);
		uploadFiles.setLocationRelativeTo(null);
		uploadFiles.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		uploadFiles.setVisible(true);

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
		
		JFrame frame = (JFrame) SwingUtilities.getRoot(projectsScreen.getParent());
		frame.remove(projectsScreen);
		statusLabel.setText("Project Page Loading");
		frame.add(sortingScreen);
		frame.validate();
		frame.repaint();
		statusLabel.setText("Sorting Screen Loaded. Double click paper to see keyword trends.");
		menuBar.removeAll();
		addProjectDetails(projectName);
		frame.add(sortingScreen);
		frame.validate();
		frame.repaint();
		
		menuBar.removeAll();
		setSortingMenu();
	}
	
	private void addProjectDetails(String projectName){
		
		statusLabel.setText("Loading project details. Please wait.");
		
		projectNameValue = projectName;

		
			JLabel NaN = new JLabel("No XML files uploaded. Add file in top menu");
			
			Panel p = new Panel();
		
			sortingScreen.setLayout(new BorderLayout());

			crtUser = users.getSelectedItem().toString();

			String tableDataFull = db.getFiles(crtUser, projectName);
			
			
			if(tableDataFull.equals("NaN")){
				p.add(NaN);
				NaN.setAlignmentX(SwingConstants.CENTER);
				sortingScreen.add(p);
			}else{
			

			sortingTable = new JTable(){
				public boolean isCellEditable(int row, int column){
					return false;
				}
			};
			
			ImpactFactorFilter f = new ImpactFactorFilter(tableDataFull);
			final ArrayList<String[]> populateTable = f.returnRanked();

			
			sortingTable.addMouseListener(new MouseAdapter() {
				  public void mouseClicked(MouseEvent e) {
				    if (e.getClickCount() == 2) {
				      JTable target = (JTable)e.getSource();
				      int row = target.getSelectedRow();

				      String url = populateTable.get(row)[5].substring(1, populateTable.get(row)[5].length()-1);

				      if(!url.isEmpty()){
				    	statusLabel.setText("Loading Graph!");
				    	PubmedGraphs g = new PubmedGraphs(url);
				      }else{
				    	statusLabel.setText("Graph could not be loaded.");
				      }
				    }
				  }
				});
			
			
			DefaultTableModel model = (DefaultTableModel) sortingTable.getModel();

			model.addColumn("Title");
			model.addColumn("Impact Factor (Current)");
			model.addColumn("Current Ranking");
			model.addColumn("Current HIndex");
			model.addColumn("Impact Factor (Publication)");
			model.addColumn("Publication Ranking");
			model.addColumn("Publication HIndex");
			model.addColumn("Impact Factor (Change)");
			model.addColumn("Citations");
			model.addColumn("Citations/Year Avg.");
			model.addColumn("Citations Perc.");
			model.addColumn("H-Index 1st Author");
			model.addColumn("H-Index Last Author");			
			
			for (int i = 0; i < populateTable.size(); i++) {
				String[] temp = {populateTable.get(i)[1], populateTable.get(i)[3], populateTable.get(i)[15],
						populateTable.get(i)[8], populateTable.get(i)[2], populateTable.get(i)[14],
						populateTable.get(i)[9], populateTable.get(i)[4], 
						populateTable.get(i)[6], populateTable.get(i)[12], 
						populateTable.get(i)[10], populateTable.get(i)[7], 
						populateTable.get(i)[13]};
				model.addRow(temp);
			}

			
			sortingScreen.add(new JScrollPane(sortingTable));
			
		}
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
				if(j.getText().equals("Add File")){
					uploadFiles.setVisible(false);
					boolean b = db.addXML(crtUser, fileDescription.getText(), projectNameValue, sortingXMLFile);	
					
					JFrame frame = (JFrame) SwingUtilities.getRoot(sortingScreen.getParent());
					frame.removeAll();
					statusLabel.setText("XML File Added");
					addProjectDetails(projectNameValue);
					frame.add(sortingScreen);
					frame.validate();
					frame.repaint();

				}
				if(j.getText().equals("Choose File")){
					//Create a file chooser
					final JFileChooser fc = new JFileChooser();
					int returnVal = fc.showSaveDialog(projectsScreen.getParent());
					if(returnVal == 0){
						sortingXMLFile = fc.getSelectedFile().toString();
					}
				}
				
				if (j.getText().equals("New User")) {
					createNewUser();
				}
				if(j.getText().equals("Add User")){
					db.makeUser(newUserNameName.getText());
					newUser.dispose();
					JFrame frame = (JFrame) SwingUtilities.getRoot(j);
					frame.removeAll();
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
				if(i.getText().equals("Manage Files")){
					UploadFile();
				}
			}
		}
	}
	
	private void createNewUser(){
		JButton addUserButton = new JButton("Add User");
		addUserButton.addActionListener(new addButtonListener());
		Label addUserLabel = new Label("User Name:");
		newUser = new JDialog();
		newUser.setLayout(new GridLayout(3,0));
		newUser.setTitle("Add New User");
		newUser.add(addUserLabel);
		newUserNameName = new JTextField("");
		newUser.add(newUserNameName);
		newUser.add(addUserButton);
		newUser.pack();
		newUser.setSize(200, 100);
		newUser.setLocationRelativeTo(null);
		newUser.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		newUser.setVisible(true);
	}
	
}
