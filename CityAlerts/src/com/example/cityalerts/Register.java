package com.example.cityalerts;

import java.io.UnsupportedEncodingException;
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
	
	private class RegisterCheck extends AsyncTask<String, Integer, Void> {

		@Override
		protected Void doInBackground(String... params) {
			
			String sql = "INSERT INTO haslo (wartosc) VALUES (?)";
			Connection connection = DbConnection.getConnection();
			
			ResultSet rs,rs2;
			
			try {	
				rs = connection.createStatement().executeQuery("SELECT username FROM uzytkownik u where u.username='" + params[0] + "'");
				rs2 = connection.createStatement().executeQuery("SELECT email FROM uzytkownik u where u.email='" + params[1] + "'");
				
				if (rs.next()){
					publishProgress(R.string.loginExists);
					return null;
				}
				else if (rs2.next()) {
					publishProgress(R.string.emailExists);
					return null;
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
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
					rs = statement.getGeneratedKeys();
					if (rs.next()) {
						long generatedId = rs.getInt(1);
						String sql2 = "INSERT INTO uzytkownik (username, idtyp, email, idHaslo) VALUES (?,?,?,?)";
						PreparedStatement statement2 = connection.prepareStatement(sql2);
						statement2.setString(1, params[0]);
						statement2.setInt(2, 1);
						statement2.setString(3, params[1]);
						statement2.setLong(4, generatedId);
						int rows = statement2.executeUpdate();
						if (rows != 0) publishProgress(R.string.registerSuccess);
						else publishProgress(R.string.registerFail);
					}
				}
			}
			catch (SQLException | NoSuchAlgorithmException ex) {
				ex.printStackTrace();
				publishProgress(R.string.registerFail);
			}
			return null;
		}
		
		protected void onProgressUpdate(Integer...params) {
				((MainActivity)activity).confirmRegister(params[0]);
	     }
	}

	public void tryToLogin(String login, String password) {
		new Login().execute(new String[]{login, password});
	}
		
	private class Login extends AsyncTask<String, Boolean, Void> {

		String login;
		String password;
		
		@Override
		protected Void doInBackground(String... params) {
			Connection connection = DbConnection.getConnection();
			try {
				String sql = "SELECT username, h.wartosc FROM "
						+ "uzytkownik u inner join haslo h on h.idhaslo=u.idhaslo "
						+ "where u.username=? " + "and h.wartosc=?";

				PreparedStatement ps = connection.prepareStatement(sql);

				final MessageDigest messageDigest = MessageDigest
						.getInstance("MD5");
				messageDigest.reset();
				try {
					messageDigest.update(params[1].getBytes("UTF8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				final byte[] resultByte = messageDigest.digest();

				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < resultByte.length; i++) {
					sb.append(Integer.toString((resultByte[i] & 0xff) + 0x100,
							16).substring(1));
				}

				ps.setString(1, params[0]);
				ps.setString(2, sb.toString());

				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					publishProgress(true);
					login = params[0];
					password = sb.toString();
				} else
					publishProgress(false);
			}

			catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onProgressUpdate(Boolean... params) {
			((MainActivity) activity).setLoggedUser(params[0], login, password);
		}
	}
}
