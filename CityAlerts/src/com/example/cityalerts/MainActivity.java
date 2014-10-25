package com.example.cityalerts;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.StrictMode;
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
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener, View.OnFocusChangeListener, TextWatcher {
	
	Button button1, button2, button3, registerCommand;
	EditText login, password, email;
	ScrollView sv;
	Register register;
	LinearLayout ll , llmain;
	Button loginCommand;
	View login2;
	Drawable drawable,drawable2;
	ScaleDrawable sd,sd2;
	
	
	private boolean LOGIN_NOT_VISIBLE = true;
	private boolean REGISTER_NOT_VISIBLE = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_main);
		
		button1 = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		button3 = (Button) findViewById(R.id.button3);
		
		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
		button3.setOnClickListener(this);
		
		login2 = (View) findViewById(R.id.login2);
		login = (EditText) findViewById(R.id.login);
		password = (EditText) findViewById(R.id.password);
		email = (EditText) findViewById(R.id.email);
		registerCommand = (Button) findViewById(R.id.registerCommand);

		login.setOnFocusChangeListener(this);
		email.setOnFocusChangeListener(this);
		password.setOnFocusChangeListener(this);
		registerCommand.setOnClickListener(this);
		login.addTextChangedListener(this);
		
		ll = (LinearLayout) findViewById(R.id.layout2);
		llmain = (LinearLayout) findViewById(R.id.layout1);
		
		register = new Register(this);
		
		drawable = getResources().getDrawable(R.drawable.blue_arrow_down);
		drawable.setBounds(0, 0, (int)(drawable.getIntrinsicWidth()*0.1), 
		                         (int)(drawable.getIntrinsicHeight()*0.1));
		sd = new ScaleDrawable(drawable, 0, 0, 0);
		
		drawable2 = getResources().getDrawable(R.drawable.blue_arrow_up);
		drawable2.setBounds(0, 0, (int)(drawable.getIntrinsicWidth()*0.1), 
		                         (int)(drawable.getIntrinsicHeight()*0.1));
		
		sd2 = new ScaleDrawable(drawable2, 0,0,0);
		
		button1.setCompoundDrawables(null, null, sd.getDrawable(), null); 
		
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

		StrictMode.setThreadPolicy(policy); 

	
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
		
		if (v == registerCommand) {
//			new Register(login.getText().toString(), password.getText().toString(), email.getText().toString());
			DbConnection instance = DbConnection.getInstance();
			System.out.println("hahaha");
		}	
		
		switch(v.getId()) {
			case R.id.button1:
				if (REGISTER_NOT_VISIBLE) {
					
					
					
					ll.setVisibility(View.VISIBLE);
					password.setVisibility(View.VISIBLE);
					email.setVisibility(View.VISIBLE);
					registerCommand.setVisibility(View.VISIBLE);
					
					button1.setCompoundDrawables(null, null, sd2.getDrawable(), null); 
					
					REGISTER_NOT_VISIBLE = false;
					
					llmain.invalidate();
				
				}
				else {
					
					ll.setVisibility(View.GONE);
//					login.setVisibility(View.GONE);
					password.setVisibility(View.GONE);
					email.setVisibility(View.GONE);
					registerCommand.setVisibility(View.GONE);
					button1.setCompoundDrawables(null, null, sd.getDrawable(), null); 
					REGISTER_NOT_VISIBLE   = true;
					
				}
				
				break;
			case R.id.button2:
				if (LOGIN_NOT_VISIBLE) {
					
					login.setVisibility(View.VISIBLE);
					password.setVisibility(View.VISIBLE);
					email.setVisibility(View.VISIBLE);
					registerCommand.setVisibility(View.VISIBLE);
					
					login.setOnFocusChangeListener(this);
					password.setOnFocusChangeListener(this);
					email.setOnFocusChangeListener(this);
					registerCommand.setOnClickListener(this);
					
					LOGIN_NOT_VISIBLE = false;
				
				}
				else {
					
					login.setVisibility(View.GONE);
					password.setVisibility(View.GONE);
					email.setVisibility(View.GONE);
					registerCommand.setVisibility(View.GONE);
					LOGIN_NOT_VISIBLE = true;
					
				}
				break;
			case R.id.button3:
				startActivity(new Intent(MainActivity.this, Alert.class));
				break;
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
		}
		else {
			if (v == login && ((EditText)v).getText().toString().equals("")) {
				login.setHint(R.string.username);
			}
			else if (v == password && ((EditText)v).getText().toString().equals("")) {
				password.setHint(R.string.password);
			}
			else if (v == email && ((EditText)v).getText().toString().equals("")) {
				email.setHint(R.string.email);
			}	
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		Toast.makeText(getApplicationContext(), count+"", Toast.LENGTH_SHORT).show();
		register.checkIfFree(s.toString());
	}

	public void updateCheckStatus(Boolean[] bool) {
		if (bool[0] == true) {
			login2.setBackgroundResource(R.drawable.green_ok);
		}
		else login2.setBackgroundResource(R.drawable.red_not_ok);
	}
}
