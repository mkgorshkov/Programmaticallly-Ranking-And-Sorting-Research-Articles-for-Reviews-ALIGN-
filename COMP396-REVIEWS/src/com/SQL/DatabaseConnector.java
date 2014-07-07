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

	private String dbURL = "jdbc:sqlite:./db/ALIGNED.db";
	private Connection conn;
	private Statement stmt;

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

			conn = DriverManager.getConnection(dbURL);

			if (conn != null) {

				stmt = conn.createStatement();
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
			rs = stmt.executeQuery("Select * from Users");
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
			conn.close();
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
			rs = stmt.executeQuery("Select * from Users");
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
			rs = stmt.executeQuery("Select * from Projects WHERE UserFK='"
					+ user + "'");
			while (rs.next()) {
				String[] temp = new String[3];
				temp[0] = rs.getString(2);
				temp[1] = rs.getString(3);
				temp[2] = "Delete";
				
				toReturn.add(temp);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return toReturn;

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
			stmt.executeQuery("INSERT INTO Projects VALUES (null,'" + projName
					+ "','" + date + "','" + user + "')");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
