package com.example.cityalerts;

import java.sql.Connection;
import java.sql.ResultSet;

import android.app.Activity;
import android.os.AsyncTask;

public class Register {
	
	private Activity activity;
	
	public Register(Activity activity) {
		this.activity = activity;
	}

	public Register(String login, String password, String email) {
		
	}

	public boolean checkIfFree(String email) {
		new DbManager().execute(email);
		return true;
	}
	
	private class DbManager extends AsyncTask<String, Boolean, Void> {

		@Override
		protected Void doInBackground(String... params) {
				Connection connection = DbConnection.getInstance().getConnection();
				try {
				ResultSet rs = connection.createStatement().executeQuery("SELECT username FROM uzytkownik u where u.username='" +params[0] + "'");
				if (rs.next()) {
					publishProgress(false);
				}
				else publishProgress(true);
				}
				catch (Exception e) {}
			return null;
		}
		
		protected void onProgressUpdate(Boolean... bool) {
			((MainActivity)activity).updateCheckStatus(bool);
	     }
	}
	
}
