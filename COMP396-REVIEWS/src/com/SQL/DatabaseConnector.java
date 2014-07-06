package com.SQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseConnector {

	private String dbURL = "jdbc:sqlite:./db/ALIGNED.db";
	private Connection conn;
	private Statement stmt;

	public DatabaseConnector() {
		makeConnection();
	}

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

	public ResultSet execute(String query) {
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery("Select * from Users");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return rs;
	}

	public void closeConnection() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String[] getUsers(){
		ResultSet rs = null;
		String[] s = null;
		ArrayList<String> rsList = new ArrayList<String>();
		
		try {
			rs = stmt.executeQuery("Select * from Users");
			while(rs.next()){
				rsList.add(rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		s = new String[rsList.size()];
		for(int i = 0; i<s.length; i++){
			s[i] = rsList.get(i);
		}
		return s;
	}
	
	public ArrayList<String[]> getProjects(String user){
		ArrayList<String[]> toReturn = new ArrayList<String[]>();
		ResultSet rs = null;
		
		try {
			rs = stmt.executeQuery("Select * from Projects WHERE UserFK='"+user+"'");
			while(rs.next()){
				String[] temp = new String[2];
				temp[0] = rs.getString(2);
				temp[1] = rs.getString(3);
				
				toReturn.add(temp);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return toReturn;

	}
}
