package com.example.cityalerts;

import java.sql.*;

public class DbConnection {
	
	public static DbConnection Instance;
	
	private DbConnection() {
	}
	
	private static Connection connection;
	
	public static DbConnection getInstance() {
		
		if (Instance == null) {
			
			try {
				   Class.forName("com.mysql.jdbc.Driver");
				   System.out.println("Connecting to database...");
				   connection = DriverManager.getConnection("jdbc:mysql://192.168.1.2/cityalerts","dominik","dominik");
				   System.out.println("success");
				}
			
			catch(Exception ex) {
				   System.out.println("Error: unable to load driver class!");
				   ex.printStackTrace();
//				   System.exit(1);
			}
	
			
			Instance = new DbConnection();
		}
		return Instance;
	}

	public static Connection getConnection() {
		return connection;
	}

	public static void setConnection(Connection connection) {
		DbConnection.connection = connection;
	}

}
