package com.SQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Database connector. Main purpose is to connect the application to the
 * embedded SQLite database.
 * 
 * Summer 2014
 * 
 * @author Maxim Gorshkov
 * 
 */
public class DatabaseConnector {

	private String pDatabaseURL = "jdbc:sqlite:./db/ALIGNED.db";
	private Connection pConnection;
	private Statement pStatement;

	/**
	 * Constructor.
	 */
	public DatabaseConnector() {
		makeConnection();
	}

	/**
	 * Establish connection to the database.
	 */
	public void makeConnection() {

		try {
			Class.forName("org.sqlite.JDBC");

			pConnection = DriverManager.getConnection(pDatabaseURL);

			if (pConnection != null) {

				pStatement = pConnection.createStatement();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Returns a result set for a given query. If not possible, throws a
	 * SQLException.
	 * 
	 * @param query
	 *            - String representing query to execute.
	 * @return ResultSet - If possible, returns ResultSet.
	 */
	public ResultSet execute(String query) {
		ResultSet rs = null;
		try {
			rs = pStatement.executeQuery("Select * from Users");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return rs;
	}

	/**
	 * Close the connection to avoid memory leaking.
	 */
	public void closeConnection() {
		try {
			pConnection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks for all users that are currently in the database.
	 * 
	 * @return String[] of users.
	 */
	public String[] getUsers() {
		ResultSet rs = null;
		String[] s = null;
		ArrayList<String> rsList = new ArrayList<String>();

		try {
			rs = pStatement.executeQuery("Select * from Users");
			while (rs.next()) {
				rsList.add(rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		s = new String[rsList.size()];
		for (int i = 0; i < s.length; i++) {
			s[i] = rsList.get(i);
		}
		return s;
	}

	/**
	 * Get parameters related to all projects for a specific user.
	 * 
	 * @param user
	 *            - Specific user to check for.
	 * @return ArrayList<String[]> - Returns the project name, and last updated.
	 */
	public ArrayList<String[]> getProjects(String user) {
		ArrayList<String[]> toReturn = new ArrayList<String[]>();
		ResultSet rs = null;

		try {
			rs = pStatement.executeQuery("Select * from Projects WHERE UserFK='"
					+ user + "'");
			while (rs.next()) {
				String[] temp = new String[3];
				temp[0] = rs.getString(2);
				temp[1] = rs.getString(3);
				temp[2] = "< DELETE >";
				
				toReturn.add(temp);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return toReturn;

	}
	
	/**
	 * Get the impact factor of a specific Journal.
	 * 
	 * @param user - Specific journal to check for.
	 * @param year - Specific year to check for
	 * @return double - Impact factor of the paper.
	 */
	public double getImpactFactor(String s, String y){
		s = s.toUpperCase();
		Double d = -1.0;
		ResultSet rs = null;
		
		try {
			rs = pStatement.executeQuery("Select ImpactFactor FROM ImpactFactor WHERE JournalAbbr = '"+s+"' AND JournalYear = '"+y+"'");
			while (rs.next()) {
				d = rs.getDouble(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return d;
	}
	
	/**
	 * Add individual impact factors to the database.
	 * @param s - Short form of the journal name.
	 * @param year - Impact factor year.
	 * @param d - Impact factor.
	 * @return boolean - whether the addition was successful.
	 */
	public boolean addImpactFactors(String s, String year, Double d){
		try {
			pStatement.execute("INSERT INTO ImpactFactor VALUES (null,'" + s + "','" + year + "',"+d+")");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Add a project for a single user.
	 * 
	 * @param projName
	 *            - New project name.
	 * @param user
	 *            - Which user the project was created for.
	 * @return boolean - whether the addition was successful.
	 */
	public boolean addProject(String projName, String user) {
		String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

		try {
			pStatement.execute("INSERT INTO Projects VALUES (null,'" + projName
					+ "','" + date + "','" + user + "')");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Remove a project for a single user.
	 * 
	 * @param projName - Project name to delete.
	 * @param user - Which user the project was deleted from.
	 * @return boolean - whether the deletion was successful.
	 */
	public boolean deleteProject(String projName, String user) {
		
		try {
			pStatement.execute("DELETE from Projects WHERE ProjectName = '" + projName + "' AND UserFK = '"+user+"'");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Returns String representing location of file of the XML.
	 * 
	 * @param user
	 * @param project
	 * @return
	 */
	public String getFiles(String user, String project){
		String filename = "NaN";
		ResultSet rs;
		
		try {
			rs = pStatement.executeQuery("Select Path FROM Files WHERE User = '"+user+"' AND Project = '"+project+"'");
			while (rs.next()) {
				filename = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return filename;
	}
	
	/**
	 * Add XML file to database.
	 * @param user
	 * @param description
	 * @param project
	 * @param file
	 * @return
	 */
	public boolean addXML(String user, String description, String project, String file) {

		try {
			pStatement.execute("INSERT INTO FILES VALUES (null,'" + file
					+ "','" + description +"','" + user + "','" + project + "')");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
