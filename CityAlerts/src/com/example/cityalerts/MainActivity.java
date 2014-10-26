package com.example.cityalerts;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener, View.OnFocusChangeListener {
	
	Button button1, button2, button3, registerCommand, loginCommand;
	EditText login, password, email, login2, password2;
	ScrollView sv;
	Register register;
	Drawable drawable,drawable2,drawable3,drawable4;
	ScaleDrawable sd,sd2,sd3,sd4;
	
	
	private boolean LOGIN_NOT_VISIBLE = true;
	private boolean REGISTER_NOT_VISIBLE = true;
	private String loggedUser;
	private String loggedPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		SharedPreferences sp = getPreferences(MODE_PRIVATE);
		
		if (sp.contains("email") && sp.contains("password")) {
			setUser(sp.getString("email", null),sp.getString("password", null));
		}
		
		setContentView(R.layout.activity_main);
		
		button1 = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		button3 = (Button) findViewById(R.id.button3);
		
		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
		button3.setOnClickListener(this);
		
		login = (EditText) findViewById(R.id.login);
		login2 = (EditText) findViewById(R.id.login2);
		password = (EditText) findViewById(R.id.password);
		password2 = (EditText) findViewById(R.id.password2);
		email = (EditText) findViewById(R.id.email);
		registerCommand = (Button) findViewById(R.id.registerCommand);
		loginCommand = (Button) findViewById(R.id.loginCommand);
		
		login.setOnFocusChangeListener(this);
		login2.setOnFocusChangeListener(this);
		email.setOnFocusChangeListener(this);
		password.setOnFocusChangeListener(this);
		password2.setOnFocusChangeListener(this);
		
		registerCommand.setOnClickListener(this);
		loginCommand.setOnClickListener(this);
		
		login.addTextChangedListener(new MultiTextWatcher(login));
		email.addTextChangedListener(new MultiTextWatcher(email));
		
		register = new Register(this);
		
		drawable = getResources().getDrawable(R.drawable.blue_arrow_down);
		drawable.setBounds(0, 0, (int)(drawable.getIntrinsicWidth()*0.1), 
		                         (int)(drawable.getIntrinsicHeight()*0.1));
		sd = new ScaleDrawable(drawable, 0, 0, 0);
		
		drawable2 = getResources().getDrawable(R.drawable.blue_arrow_up);
		drawable2.setBounds(0, 0, (int)(drawable.getIntrinsicWidth()*0.1), 
		                         (int)(drawable.getIntrinsicHeight()*0.1));
		
		sd2 = new ScaleDrawable(drawable2, 0,0,0);
		
		
		drawable3 = getResources().getDrawable(R.drawable.green_ok);
		drawable3.setBounds(0, 0, (int)(drawable3.getIntrinsicWidth()*0.04), 
		                         (int)(drawable3.getIntrinsicHeight()*0.04));
		sd3 = new ScaleDrawable(drawable3, 0, 0, 0);
		
		drawable4 = getResources().getDrawable(R.drawable.red_not_ok);
		drawable4.setBounds(0, 0, (int)(drawable4.getIntrinsicWidth()*0.04), 
		                         (int)(drawable4.getIntrinsicHeight()*0.04));
		sd4 = new ScaleDrawable(drawable4, 0, 0, 0);
		
		button1.setCompoundDrawables(null, null, sd.getDrawable(), null); 
		button2.setCompoundDrawables(null, null, sd.getDrawable(), null); 
		
//		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//
//		StrictMode.setThreadPolicy(policy); 

	
	}

	private void setUser(String string, String string2) {
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		
		switch(v.getId()) {
			case R.id.button1:
				if (REGISTER_NOT_VISIBLE) {
					
					login.setVisibility(View.VISIBLE);
					password.setVisibility(View.VISIBLE);
					email.setVisibility(View.VISIBLE);
					registerCommand.setVisibility(View.VISIBLE);
					
					button1.setCompoundDrawables(null, null, sd2.getDrawable(), null); 
					
					REGISTER_NOT_VISIBLE = false;
				}
				else {
				
					login.setVisibility(View.GONE);
					password.setVisibility(View.GONE);
					email.setVisibility(View.GONE);
					registerCommand.setVisibility(View.GONE);
					button1.setCompoundDrawables(null, null, sd.getDrawable(), null); 
					REGISTER_NOT_VISIBLE   = true;	
				}
				
				break;
			case R.id.button2:
				if (LOGIN_NOT_VISIBLE) {
					
					login2.setVisibility(View.VISIBLE);
					password2.setVisibility(View.VISIBLE);
					loginCommand.setVisibility(View.VISIBLE);
					
					button2.setCompoundDrawables(null, null, sd2.getDrawable(), null); 
					
					LOGIN_NOT_VISIBLE = false;
				}
				else {
					
					login2.setVisibility(View.GONE);
					password2.setVisibility(View.GONE);
					loginCommand.setVisibility(View.GONE);
					LOGIN_NOT_VISIBLE = true;
					
				}
				break;
			case R.id.button3:
				Intent intent = new Intent(MainActivity.this, Alert.class);
				intent.putExtra("login", loggedUser);
				intent.putExtra("haslo", loggedPassword);
				startActivity(intent);
				break;
			case R.id.registerCommand:
				register.tryToRegister(login.getText().toString(),
										email.getText().toString(),
										password.getText().toString());
				break;
			case R.id.loginCommand:
				register.tryToLogin(login2.getText().toString(),
									password2.getText().toString());
		}
		
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		
		if (hasFocus) {
			if (v == login) {
				login.setHint("");
			}
			else if (v == password) {
				password.setHint("");
			}
			else if (v == email) {
				email.setHint("");
			}
			else if (v == login2) {
				login2.setHint("");
			}
			else if (v == password2) {
				password2.setHint("");
			}
		}
		else {
			if (v == login && ((EditText)v).getText().toString().equals("")) {
				login.setHint(R.string.username);
				login.setCompoundDrawables(null, null, null, null);
			}
			else if (v == password && ((EditText)v).getText().toString().equals("")) {
				password.setHint(R.string.password);
			}
			else if (v == email && ((EditText)v).getText().toString().equals("")) {
				email.setHint(R.string.email);
				email.setCompoundDrawables(null, null, null, null);
			}	
			else if (v == login2 && ((EditText)v).getText().toString().equals("")) {
				login2.setHint(R.string.username);
			}
			else if (v == password2 && ((EditText)v).getText().toString().equals("")) {
				password2.setHint(R.string.password);
			}
		}
	}

	public void updateCheckStatus(String...params) {
		if (params[1].equals("login")) {
			if (params[0].equals("true")) {
				login.setCompoundDrawables(null, null, sd3.getDrawable(), null);
			}
			else login.setCompoundDrawables(null, null, sd4.getDrawable(), null);
		}
		else {
			if (params[0].equals("true")) {
				email.setCompoundDrawables(null, null, sd3.getDrawable(), null);
			}
			else email.setCompoundDrawables(null, null, sd4.getDrawable(), null);
		}
	}
	
	private class MultiTextWatcher implements TextWatcher{

	    private View view;
	    private MultiTextWatcher(View view) {
	        this.view = view;
	    }

	    public void afterTextChanged(Editable editable) {
	    	
	    }

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			switch(view.getId()) {
			case R.id.login:
				register.checkIfFree(s.toString() , "login");
				break;
			case R.id.email:
				register.checkIfFree(s.toString() , "email");
				break;
			}
			
		}
	}

	public void confirmRegister(int string) {
	
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setPositiveButton("OK", null);
		AlertDialog dialog = builder.create();
		
		TextView title = new TextView(this);
		title.setText(string);
		title.setGravity(Gravity.CENTER);
		title.setTextSize(20);
		dialog.setCustomTitle(title);
		
		dialog.show();
		
	}

	public void setLoggedUser(final Boolean bool, String login, String password) {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (bool) {
						button2.setText(R.string.logout);
						login2.setVisibility(View.GONE);
						password2.setVisibility(View.GONE);
						loginCommand.setVisibility(View.GONE);
						button1.setVisibility(View.GONE);
					}
				}
			});
			
			AlertDialog dialog = builder.create();
			if (bool) {
				dialog.setTitle(R.string.loginSuccess);
				loggedUser = login;
				loggedPassword = password;
			}
			else dialog.setTitle(R.string.loginFail);
			
			dialog.show();
	}
}
