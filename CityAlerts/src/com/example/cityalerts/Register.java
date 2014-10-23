package com.example.cityalerts;

import java.sql.Connection;
import java.sql.ResultSet;

import android.os.AsyncTask;
import android.support.v4.content.AsyncTaskLoader;

public class Register {
	
	public Register() {
		
	}

	public Register(String login, String password, String email) {
		
	}

	public boolean checkIfFree(String email) {
		new DbManager().execute("blabla");
		return true;
	}
	
	private class DbManager extends AsyncTask<String, Boolean, Void> {

		@Override
		protected Void doInBackground(String... params) {
				Connection connection = DbConnection.getInstance().getConnection();
				try {
				ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM users");
				System.out.println(rs);
				}
				catch (Exception e) {}
			return null;
		}
		
	}
	
}
