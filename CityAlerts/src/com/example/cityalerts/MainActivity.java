package com.example.cityalerts;

import android.app.Activity;
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
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener, View.OnFocusChangeListener, TextWatcher {
	
	Button button1, button2, button3, registerCommand;
	EditText login, password, email;
	ScrollView sv;

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
			new Register(login.getText().toString(), password.getText().toString(), email.getText().toString());
		}
		
		switch(v.getId()) {
			case R.id.button1:
				
				LinearLayout rl = (LinearLayout) findViewById(R.id.layout1);
				
				login = new EditText(this);
				password = new EditText(this);
				email = new EditText(this);
				registerCommand = new Button(this);
				login.setGravity(Gravity.CENTER);
				password.setGravity(Gravity.CENTER);
				email.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

				login.setHint(R.string.username);
				login.setOnFocusChangeListener(this);
				password.setOnFocusChangeListener(this);
				password.setHint(R.string.password);
				email.setHint(R.string.email);
				email.setOnFocusChangeListener(this);
				
				login.addTextChangedListener(this);
				
				final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
				
				int pixels = (int) (240 * scale + 0.5f);
				int buttonPixels = (int) (200 * scale + 0.5f);
				
//				button1.setVisibility(View.GONE);
				rl.invalidate();
				params.width=pixels;
				
				LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

				login.setLayoutParams(params);
				password.setLayoutParams(params);
				email.setLayoutParams(params);
				email.setMaxWidth((int) (240 * scale + 0.5f));
				
				buttonParams.width=buttonPixels;
				registerCommand.setLayoutParams(buttonParams);
				registerCommand.setText(R.string.doRegister);
				registerCommand.setOnClickListener(this);
				
				rl.addView(login, 1);
				rl.addView(password, 2);
				rl.addView(email, 3);
				rl.addView(registerCommand, 4);
				
				rl.invalidate();
				
				break;
			case R.id.button2:
				break;
			case R.id.button3:
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
			
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// ASDSADASDASsad
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		Toast.makeText(getApplicationContext(), count+"", Toast.LENGTH_SHORT).show();
	}
}
