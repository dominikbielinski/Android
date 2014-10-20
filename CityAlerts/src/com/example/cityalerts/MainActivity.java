package com.example.cityalerts;

import android.app.Activity;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class MainActivity extends Activity implements View.OnClickListener {
	
	Button button1,button2,button3;

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
		switch(v.getId()) {
			case R.id.button1:
				break;
			case R.id.button2:
				break;
			case R.id.button3:
				RelativeLayout rl = (RelativeLayout) findViewById(R.id.layout1);
				EditText login = new EditText(this);
				EditText password = new EditText(this);
				login.setGravity(Gravity.CENTER);
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(button3.getLayoutParams());
				params.addRule(RelativeLayout.CENTER_HORIZONTAL);
				params.addRule(Gravity.CENTER);
				params.leftMargin=(int) button3.getX();
				params.topMargin=(int) button3.getY();
				login.setHint("U¿ytkownik");
				login.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							((EditText)v).setHint("");
						}	
					}
				});
				
				button3.setVisibility(View.GONE);
				login.setLayoutParams(params);
				rl.addView(login);
				break;
		}
		
	}
}
