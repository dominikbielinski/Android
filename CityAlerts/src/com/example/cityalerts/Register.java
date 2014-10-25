package com.example.cityalerts;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import android.app.Activity;
import android.os.AsyncTask;

public class Register {
	
	private Activity activity;
	
	public Register(Activity activity) {
		this.activity = activity;
	}

	public Register(String login, String password, String email) {
		
	}

	public void checkIfFree(String checkIfFree, String type) {
		new AvailabilityCheck().execute(new String[] {checkIfFree, type} );
	}
	
	private class AvailabilityCheck extends AsyncTask<String, String, Void> {

		@Override
		protected Void doInBackground(String... params) {
				Connection connection = DbConnection.getConnection();
				if (params[1].equals("login")) {
					try {
					ResultSet rs = connection.createStatement().executeQuery("SELECT username FROM uzytkownik u where u.username='" +params[0] + "'");
					if (rs.next()) {
						publishProgress(new String[]{"false", "login"});
					}
					else publishProgress(new String[]{"true", "login"});
					}
					catch (Exception e) {}
				}
				else {
					try {
						ResultSet rs = connection.createStatement().executeQuery("SELECT email FROM uzytkownik u where u.email='" +params[0] + "'");
						if (rs.next()) {
							publishProgress(new String[]{"false", "email"});
						}
						else publishProgress(new String[]{"true", "email"});
						}
						catch (Exception e) {}
				}
			return null;
		}
		
		protected void onProgressUpdate(String...params) {
			((MainActivity)activity).updateCheckStatus(params);
	     }
	}

	public void tryToRegister(String login, String email, String password) {
		new RegisterCheck().execute(new String[]{login,email,password});
	}
	
	private class RegisterCheck extends AsyncTask<String, String, Void> {

		@Override
		protected Void doInBackground(String... params) {
			
			String sql = "INSERT INTO haslo (wartosc) VALUES (?)";
			Connection connection = DbConnection.getConnection();

			try {
				final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
				messageDigest.reset();
				try {
					messageDigest.update(params[2].getBytes("UTF8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				final byte[] resultByte = messageDigest.digest();
				
				StringBuffer sb = new StringBuffer();
		        for (int i = 0; i < resultByte.length; i++) {
		          sb.append(Integer.toString((resultByte[i] & 0xff) + 0x100, 16).substring(1));
		        }
		        
				PreparedStatement statement = connection.prepareStatement(sql,
				                                      Statement.RETURN_GENERATED_KEYS);
				statement.setString(1, sb.toString());
				
				int affectedRows = statement.executeUpdate();
				
				if (affectedRows != 0) {
					ResultSet rs = statement.getGeneratedKeys();
					if (rs.next()) {
						long generatedId = rs.getInt(1);
						String sql2 = "INSERT INTO uzytkownik (username, idtyp, email, idHaslo) VALUES (?,?,?,?)";
						PreparedStatement statement2 = connection.prepareStatement(sql2);
						statement2.setString(1, params[0]);
						statement2.setInt(2, 1);
						statement2.setString(3, params[1]);
						statement2.setLong(4, generatedId);
						statement2.executeUpdate();
					}
				}
			}
			catch (SQLException | NoSuchAlgorithmException ex) {ex.printStackTrace();}
			return null;
		}
		
		protected void onProgressUpdate(String...params) {
			((MainActivity)activity).updateCheckStatus(params);
	     }
	}
	
}
