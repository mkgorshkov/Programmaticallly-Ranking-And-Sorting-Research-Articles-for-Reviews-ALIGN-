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

import com.SQL.DatabaseConnector;
import com.Scrape.ConnectPubMedTrends;
import com.Scrape.ImputDataOrganizer;
import com.Scrape.PubmedGraphs;

/**
 * Main window responsible for the main container.
 * Summer 2014
 * @author Maxim Gorshkov
 * 
 */
public class MainFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private JMenuBar pMenuBar = new JMenuBar();
	private JMenu pMenuFile;
	private JMenuItem pNewProjectItem;
	private JMenuItem pExitItem;
	private JMenuItem pExitProjectsItem;
	private JMenuItem pManageFilesItem;
	
	private JPanel pLoginScreen = new JPanel();
	private JPanel pProjectsScreen = new JPanel();
	private JPanel pSortingScreen = new JPanel();
	private JPanel pStatusPanel = new JPanel();
	
	private JDialog pProjectCreate;
	private JDialog pUploadFiles;
	private JDialog pModifyUser;
	
	private JLabel pStatusLabel = new JLabel("");
	private JLabel pUsersLabel = new JLabel("Select User:");
	
	private JButton pConnect = new JButton("Connect");
	private JButton pNewUserCreate = new JButton("Modify User");
	
	private JTextField pNewUserNameInput;

	private JTable pProjects;
	private JTable pSortingTable;

	private JComboBox<String> pUsers;
	
	private String pProjectNameValue;
	private String pSortingXMLFile;
	
	private DatabaseConnector pDatabaseConnect;

	private String pCrtUser;
	private JTextField pProjectName;
	private JTextField pFileDescription;

	/**
	 * Constructor. Creates the main screen and calls to set up connection to database and login screen.
	 */
	public MainFrame() {
		makeConnection();

		setTitle("Article Quality Sorter");
		setSize(600, 400);
		setLocationRelativeTo(null);

		pLoginScreen.add(addGrid());
		// setMenu();
		this.setJMenuBar(pMenuBar);
		setStatus();
		this.add(pLoginScreen);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * Connects to the internal database.
	 */
	private void makeConnection() {
		pDatabaseConnect = new DatabaseConnector();
	}

	/**
	 * Sets the menu on the top bar of the screen.
	 */
	private void setMenu() {		
		pMenuFile = new JMenu("File");
		pNewProjectItem = new JMenuItem("New Project");
		pNewProjectItem.addActionListener(new addButtonListener());
		pMenuFile.add(pNewProjectItem);
		
		pExitItem = new JMenuItem("Exit to Login");
		pExitItem.addActionListener(new addButtonListener());
		pMenuFile.add(pExitItem);
		
		pMenuBar.add(pMenuFile);
	}
	
	/**
	 * Sets the frame that manages the sorted and calculated values.
	 */
	private void setSortingMenu(){
		pMenuFile = new JMenu("File");
		pManageFilesItem = new JMenuItem("Manage Files");
		pManageFilesItem.addActionListener(new addButtonListener());
		pMenuFile.add(pManageFilesItem);
		
		pExitProjectsItem = new JMenuItem("Exit to Projects");
		pExitProjectsItem.addActionListener(new addButtonListener());
		pMenuFile.add(pExitProjectsItem);
		
		pMenuBar.add(pMenuFile);
	}

	/**
	 * Sets the status bar on the bottom of the screen.
	 */
	private void setStatus() {
		pStatusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		this.add(pStatusPanel, BorderLayout.SOUTH);
		pStatusPanel.setPreferredSize(new Dimension(this.getWidth(), 16));
		pStatusPanel.setLayout(new BoxLayout(pStatusPanel, BoxLayout.X_AXIS));
		pStatusLabel.setText("Successfully connected to Database");
		pStatusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		pStatusPanel.add(pStatusLabel);
	}

	/**
	 * Sets the login grid on the front page.
	 * @return Panel.
	 */
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
			p.add(pUsersLabel);
			pUsersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			addLogin();
			p.add(pUsers);
			p.add(Box.createVerticalStrut(15));
			p.add(pConnect);
			p.add(Box.createVerticalStrut(15));
			p.add(pNewUserCreate);
			pNewUserCreate.setAlignmentX(Component.CENTER_ALIGNMENT);
			pNewUserCreate.addActionListener(new addButtonListener());

			pConnect.setAlignmentX(Component.CENTER_ALIGNMENT);
			pConnect.addActionListener(new addButtonListener());

		} catch (IOException e) {
			e.printStackTrace();
		}
		return p;
	}

	/**
	 * Adds the login components to the first frame.
	 */
	private void addLogin() {
		pUsers = new JComboBox<String>(pDatabaseConnect.getUsers());
		pUsers.setMaximumSize(new Dimension(300, 20));
		pUsers.setAlignmentX(Component.CENTER_ALIGNMENT);
	}
	
	/**
	 * Produces dialog to create a new project.
	 */
	private void createNewProject(){
		JButton accept = new JButton("OK");
		accept.addActionListener(new addButtonListener());
		Label newProjLabel = new Label("New Project Name:");
		pProjectCreate = new JDialog();
		pProjectCreate.setLayout(new BorderLayout());
		pProjectCreate.setTitle("Create New Project");
		pProjectCreate.add(newProjLabel);
		pProjectCreate.add(newProjLabel, BorderLayout.NORTH);
		pProjectName = new JTextField("");
		pProjectCreate.add(pProjectName, BorderLayout.CENTER);
		pProjectCreate.add(accept, BorderLayout.SOUTH);
		pProjectCreate.pack();
		pProjectCreate.setSize(200, 100);
		pProjectCreate.setLocationRelativeTo(null);
		pProjectCreate.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		pProjectCreate.setVisible(true);

	}
	
	/**
	 * Produces dialog to upload a new file to a project.
	 */
	private void UploadFile(){
		JButton accept = new JButton("Add File");
		JButton choose = new JButton("Choose File");
		accept.addActionListener(new addButtonListener());
		choose.addActionListener(new addButtonListener());
		Label newProjLabel = new Label("File Description:");
		pUploadFiles = new JDialog();
		pUploadFiles.setLayout(new GridLayout(4,0));
		pUploadFiles.setTitle("Add XML File");
		pUploadFiles.add(choose);
		pUploadFiles.add(newProjLabel);
		pFileDescription = new JTextField("");
		pUploadFiles.add(pFileDescription);
		pUploadFiles.add(accept);
		pUploadFiles.pack();
		pUploadFiles.setSize(200, 100);
		pUploadFiles.setLocationRelativeTo(null);
		pUploadFiles.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		pUploadFiles.setVisible(true);

	}

	/**
	 * Sets the screen holding the projects for each user.
	 */
	private void setProjScreen() {
		pProjectsScreen.setLayout(new BorderLayout());

		pCrtUser = pUsers.getSelectedItem().toString();

		final ArrayList<String[]> tableDataFull = pDatabaseConnect.getProjects(pCrtUser);

		pProjects = new JTable(){
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column){
				return false;
			}
		};
		
		pProjects.addMouseListener(new MouseAdapter() {
			  public void mouseClicked(MouseEvent e) {
			    if (e.getClickCount() == 2) {
			      JTable target = (JTable)e.getSource();
			      int row = target.getSelectedRow();
			      int column = target.getSelectedColumn();
			      
			      if(column == 2){
			    	  pDatabaseConnect.deleteProject(tableDataFull.get(row)[0], pCrtUser);
			    	  
			    	  JFrame frame = (JFrame) SwingUtilities.getRoot(pProjectsScreen.getParent());
						pProjectsScreen.removeAll();
						pStatusLabel.setText("Projects Refreshed");
						setProjScreen();
						frame.add(pProjectsScreen);
						frame.validate();
						frame.repaint();
			      }else{
			    	  goToProject(tableDataFull.get(row)[0]);
			      }
			    }
			  }
			});
		
		DefaultTableModel model = (DefaultTableModel) pProjects.getModel();

		model.addColumn("Project Name");
		model.addColumn("Last Updated");
		model.addColumn(" ");
		
		
		for (int i = 0; i < tableDataFull.size(); i++) {
			model.addRow(tableDataFull.get(i));
		}

		DefaultTableCellRenderer   render = new DefaultTableCellRenderer ();
		render.setHorizontalAlignment (SwingConstants.CENTER);
		pProjects.getColumnModel().getColumn(2).setCellRenderer(render);
		
		pProjects.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		pProjects.getColumnModel().getColumn(0).setPreferredWidth(400);
		pProjects.getColumnModel().getColumn(1).setPreferredWidth(100);
		
		pProjectsScreen.add(new JScrollPane(pProjects));
		
	}
	
	/**
	 * When a specific project is selected, the sorting screen opens for that project.
	 * @param projectName - String.
	 */
	private void goToProject(String projectName) {
		
		JFrame frame = (JFrame) SwingUtilities.getRoot(pProjectsScreen.getParent());
		frame.remove(pProjectsScreen);
		pStatusLabel.setText("Project Page Loading");
		frame.add(pSortingScreen);
		frame.validate();
		frame.repaint();
		pStatusLabel.setText("Sorting Screen Loaded. Double click paper to see keyword trends.");
		pMenuBar.removeAll();
		addProjectDetails(projectName);
		frame.add(pSortingScreen);
		frame.validate();
		frame.repaint();
		
		pMenuBar.removeAll();
		setSortingMenu();
	}
	
	/**
	 * Allows for XML files to be added for a specific project.
	 * @param projectName - String.
	 */
	private void addProjectDetails(String projectName){
		
		pStatusLabel.setText("Loading project details. Please wait.");
		
		pProjectNameValue = projectName;

		
			JLabel NaN = new JLabel("No XML files uploaded. Add file in top menu");
			
			Panel p = new Panel();
		
			pSortingScreen.setLayout(new BorderLayout());

			pCrtUser = pUsers.getSelectedItem().toString();

			String tableDataFull = pDatabaseConnect.getFiles(pCrtUser, projectName);
			
			
			if(tableDataFull.equals("NaN")){
				p.add(NaN);
				NaN.setAlignmentX(SwingConstants.CENTER);
				pSortingScreen.add(p);
			}else{
			

			pSortingTable = new JTable(){
				public boolean isCellEditable(int row, int column){
					return false;
				}
			};
			
			ImputDataOrganizer f = new ImputDataOrganizer(tableDataFull);
			final ArrayList<String[]> populateTable = f.returnRanked();

			
			pSortingTable.addMouseListener(new MouseAdapter() {
				  public void mouseClicked(MouseEvent e) {
				    if (e.getClickCount() == 2) {
				      JTable target = (JTable)e.getSource();
				      int row = target.getSelectedRow();

				      String url = populateTable.get(row)[5].substring(1, populateTable.get(row)[5].length()-1);

				      if(!url.isEmpty()){
				    	pStatusLabel.setText("Loading Graph!");
				    	PubmedGraphs g = new PubmedGraphs(url);
				      }else{
				    	pStatusLabel.setText("Graph could not be loaded.");
				      }
				    }
				  }
				});
			
			
			DefaultTableModel model = (DefaultTableModel) pSortingTable.getModel();

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

			
			pSortingScreen.add(new JScrollPane(pSortingTable));
			
		}
	}
	
	/**
	 * Allows for adding and removing of users on the main login page.
	 */
	private void createNewUser(){
		JButton addUserButton = new JButton("Add User");
		JButton removeUserButton = new JButton("Remove User");

		addUserButton.addActionListener(new addButtonListener());
		removeUserButton.addActionListener(new addButtonListener());
		
		Label addUserLabel = new Label("User Name:");
		pModifyUser = new JDialog();
		pModifyUser.setLayout(new GridLayout(4,0));
		pModifyUser.setTitle("Add/Remove User");
		pModifyUser.add(addUserLabel);
		pNewUserNameInput = new JTextField("");
		pModifyUser.add(pNewUserNameInput);
		pModifyUser.add(addUserButton);
		pModifyUser.add(removeUserButton);
		pModifyUser.pack();
		pModifyUser.setSize(200, 100);
		pModifyUser.setLocationRelativeTo(null);
		pModifyUser.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		pModifyUser.setVisible(true);
	}

	/**
	 * Action listener which controls the buttons that are clickable throughout the program.
	 * @author Maxim Gorshkov
	 *
	 */
	class addButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JButton j = null;
			JMenuItem i = null;

			if (e.getSource().getClass().equals(pConnect.getClass())) {
				j = (JButton) e.getSource();

				if (j.getText().equals("Connect")) {
					JFrame frame = (JFrame) SwingUtilities.getRoot(j);
					frame.remove(pLoginScreen);
					pStatusLabel.setText("Projects Loading");
					pProjectsScreen.removeAll();
					setProjScreen();

					frame.add(pProjectsScreen);
					frame.validate();
					frame.repaint();
					pStatusLabel.setText("Projects Loaded - Select project to continue...");
					
					pMenuBar.removeAll();
					setMenu();
				}
				if(j.getText().equals("OK")){
					pProjectCreate.setVisible(false);
					System.out.println(pProjectName.getText());
					boolean b = pDatabaseConnect.addProject(pProjectName.getText(), pCrtUser);	
					
					JFrame frame = (JFrame) SwingUtilities.getRoot(pProjectsScreen.getParent());
					pProjectsScreen.removeAll();
					pStatusLabel.setText("Projects Refreshed");
					setProjScreen();
					frame.add(pProjectsScreen);
					frame.validate();
					frame.repaint();
				}
				if(j.getText().equals("Add File")){
					pUploadFiles.setVisible(false);
					pDatabaseConnect.addXML(pCrtUser, pFileDescription.getText(), pProjectNameValue, pSortingXMLFile);	
					
					JFrame frame = (JFrame) SwingUtilities.getRoot(pSortingScreen.getParent());
					frame.removeAll();
					pStatusLabel.setText("XML File Added");
					addProjectDetails(pProjectNameValue);
					frame.add(pSortingScreen);
					frame.validate();
					frame.repaint();

				}
				if(j.getText().equals("Choose File")){
					//Create a file chooser
					final JFileChooser fc = new JFileChooser();
					int returnVal = fc.showSaveDialog(pProjectsScreen.getParent());
					if(returnVal == 0){
						pSortingXMLFile = fc.getSelectedFile().toString();
					}
				}
				
				if (j.getText().equals("Modify User")) {
					createNewUser();
				}
				if(j.getText().equals("Add User")){
					pDatabaseConnect.makeUser(pNewUserNameInput.getText());
					pModifyUser.dispose();
					JFrame frame = (JFrame) SwingUtilities.getRoot(pLoginScreen.getParent());
					frame.remove(pLoginScreen);
					pLoginScreen.removeAll();
					pLoginScreen.add(addGrid());
					frame.add(pLoginScreen);
					frame.validate();
					frame.repaint();
				}
				if(j.getText().equals("Remove User")){
					pDatabaseConnect.deleteUser(pNewUserNameInput.getText());
					pModifyUser.dispose();
					JFrame frame = (JFrame) SwingUtilities.getRoot(pLoginScreen.getParent());
					frame.remove(pLoginScreen);
					pLoginScreen.removeAll();
					pLoginScreen.add(addGrid());
					frame.add(pLoginScreen);
					frame.validate();
					frame.repaint();
				}

			} else if (e.getSource().getClass().equals(pExitItem.getClass())) {
				i = (JMenuItem) e.getSource();

				if (i.getText().equals("New Project")) {
					createNewProject();
				}
				if (i.getText().equals("Exit to Login")) {
					JFrame frame = (JFrame) SwingUtilities.getRoot(pProjectsScreen.getParent());
					frame.remove(pProjectsScreen);
					pStatusLabel.setText("Login Screen Loading");
					frame.add(pLoginScreen);
					frame.validate();
					frame.repaint();
					pStatusLabel.setText("Login Screen Loaded");
					pMenuBar.removeAll();
				}
				if(i.getText().equals("Manage Files")){
					UploadFile();
				}
			}
		}
	}
	
	
	
}
