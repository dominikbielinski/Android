package com.example.cityalerts;

import java.util.Locale;

import org.apache.http.Header;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

public class MainActivity extends FragmentActivity implements View.OnClickListener,
		View.OnFocusChangeListener, TextWatcher, GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

	Button button1, button2, button3, registerCommand, loginCommand;
	CheckBox remember;
	EditText username, password, email, username2, password2;
	Drawable drawable3, drawable4;
	ScaleDrawable sd3, sd4;
	FrameLayout fl;
	Spinner language;
	String[] strings = {"Polski", "English"};
	int[] images = {R.drawable.polish , R.drawable.english};
	int currentSelection = 0;
	LocationRequest lr;
	
	private MainFragment mainFragment;

	SharedPreferences sp;
	
	private boolean LOGIN_NOT_VISIBLE = true;
	private boolean REGISTER_NOT_VISIBLE = true;

	ProgressDialog dialog;
	
	LocationClient lc;
	
	PersistentCookieStore cookieStore;

	public boolean USER_IS_LOGGED;
	
	private Location location;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			
	        mainFragment = new MainFragment();
	        getSupportFragmentManager()
	        .beginTransaction()
	        .add(R.id.buttonFragment, mainFragment)
	        .commit();
	        
	    } else {
	    	
	        mainFragment = (MainFragment) getSupportFragmentManager()
	        .findFragmentById(android.R.id.content);
	        
	      //  hideButtons(true, true);
	        
	    }
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_main);

		lc = new LocationClient(this, this, this);
		lr = LocationRequest.create();
		lr.setInterval(1000);
		lr.setFastestInterval(500);
		lr.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		
		
		sp = getSharedPreferences("data",MODE_PRIVATE);

		button1 = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		button3 = (Button) findViewById(R.id.button3);
		registerCommand = (Button) findViewById(R.id.registerCommand);
		loginCommand = (Button) findViewById(R.id.loginCommand);

		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
		button3.setOnClickListener(this);

		username = (EditText) findViewById(R.id.login);
		username2 = (EditText) findViewById(R.id.login2);
		password = (EditText) findViewById(R.id.password);
		password2 = (EditText) findViewById(R.id.password2);
		email = (EditText) findViewById(R.id.email);
		
		username.addTextChangedListener(this);
		email.addTextChangedListener(this);
		password.addTextChangedListener(this);

		remember = (CheckBox) findViewById(R.id.remember);
		
		username.setOnFocusChangeListener(this);
		username2.setOnFocusChangeListener(this);
		email.setOnFocusChangeListener(this);
		password.setOnFocusChangeListener(this);
		password2.setOnFocusChangeListener(this);
		registerCommand.setOnClickListener(this);
		loginCommand.setOnClickListener(this);
		registerCommand.setEnabled(false);

		drawable3 = getResources().getDrawable(R.drawable.green_ok);
		drawable3.setBounds(0, 0, (int) (drawable3.getIntrinsicWidth() * 0.04),
				(int) (drawable3.getIntrinsicHeight() * 0.04));
		sd3 = new ScaleDrawable(drawable3, 0, 0, 0);

		drawable4 = getResources().getDrawable(R.drawable.red_not_ok);
		drawable4.setBounds(0, 0, (int) (drawable4.getIntrinsicWidth() * 0.04),
				(int) (drawable4.getIntrinsicHeight() * 0.04));

		sd4 = new ScaleDrawable(drawable4, 0, 0, 0);

		if (sp.contains("name") && sp.getString("password", null) != null) {
			tryToLogin(sp.getString("name", ""),
					sp.getString("password", ""));
		}
		
		//getSupportFragmentManager().beginTransaction().hide(mainFragment).commit();
		
		cookieStore = new PersistentCookieStore(this);
		WebApiClient.getInstance().getClient().setCookieStore(cookieStore);
		
		fl = (FrameLayout) findViewById(R.id.fl);
		fl.setVisibility(View.GONE);
		if (savedInstanceState != null) {
			hideButtons(true, true);
		}
	}

	private class CustomAdapter extends ArrayAdapter<String> {

		public CustomAdapter(Context context, int textViewResourceId,   String[] objects) {
            super(context, textViewResourceId, objects);
        }
 
        @Override
        public View getDropDownView(int position, View convertView,ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }
 
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }
 
        public View getCustomView(int position, View convertView, ViewGroup parent) {
 
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.row, parent, false);
            TextView label=(TextView) row.findViewById(R.id.languageText);
            label.setText(strings[position]);
 
            ImageView icon=(ImageView)row.findViewById(R.id.languageFlag);
            icon.setImageResource(images[position]);
            return row;
            }
        }
	
	@Override
	protected void onStart() {
		super.onStart();
        lc.connect();
	}
	
	@Override
	protected void onStop() {
        lc.disconnect();
        super.onStop();
	}

	private void tryToLogin(final String username, final String password) {
		RequestParams rp = new RequestParams();
		rp.add("username", username);
		rp.add("password", password);

		WebApiClient.getInstance().post("UserLogin", rp,
				new AsyncHttpResponseHandler() {

					@Override
					public void onStart() {
						dialog = new ProgressDialog(MainActivity.this);
						dialog.setMessage(getResources().getString(
								R.string.logging));
						dialog.setIndeterminate(false);
						dialog.setCancelable(true);
						dialog.show();
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] response) {
						if (remember.isChecked()) {
							setLoginInfo(username, password, null, false);
						}
						dialog.hide();
						USER_IS_LOGGED = true;
						hideButtons(true, false);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] errorResponse, Throwable e) {
						dialog.hide();

						AlertDialog.Builder builder = new AlertDialog.Builder(
								MainActivity.this);
						builder.setPositiveButton("OK", null);
						AlertDialog dialog = builder.create();

						TextView title = new TextView(MainActivity.this);
						if (statusCode == 403) {
							title.setText(R.string.loginFail);
						} else
							title.setText(R.string.noConnection);

						title.setGravity(Gravity.CENTER);
						title.setTextSize(20);
						dialog.setCustomTitle(title);
						dialog.show();
					}
				});
	}

	public void tryToRegister(final String username, final String email,
			final String password) {
		RequestParams rp = new RequestParams();
		rp.add("username", username);
		rp.add("email", email);
		rp.add("password", password);

		WebApiClient.getInstance().post("UserRegistration", rp,
				new AsyncHttpResponseHandler() {

					@Override
					public void onStart() {
						dialog = new ProgressDialog(MainActivity.this);
						dialog.setMessage(getResources().getString(
								R.string.registrating));
						dialog.setIndeterminate(false);
						dialog.setCancelable(true);
						dialog.show();
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] response) {
						dialog.hide();
						AlertDialog.Builder builder = new AlertDialog.Builder(
								MainActivity.this);
						builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								tryToLogin(username, password);
							}
						});
						AlertDialog dialog = builder.create();

						TextView title = new TextView(MainActivity.this);
						title.setText(R.string.registerSuccess);
						title.setGravity(Gravity.CENTER);
						title.setTextSize(20);
						dialog.setCustomTitle(title);
						dialog.show();
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] errorResponse, Throwable e) {
						dialog.hide();

						AlertDialog.Builder builder = new AlertDialog.Builder(
								MainActivity.this);
						builder.setPositiveButton("OK", null);
						AlertDialog dialog = builder.create();

						TextView title = new TextView(MainActivity.this);
						if (statusCode == 409) {
							title.setText(R.string.userOrEmailExists);
						} else
							title.setText(R.string.noConnection);

						title.setGravity(Gravity.CENTER);
						title.setTextSize(20);
						dialog.setCustomTitle(title);
						dialog.show();
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.button1:

			if (REGISTER_NOT_VISIBLE) {
				username.setVisibility(View.VISIBLE);
				password.setVisibility(View.VISIBLE);
				email.setVisibility(View.VISIBLE);
				registerCommand.setVisibility(View.VISIBLE);
				REGISTER_NOT_VISIBLE = false;
			} else {
				username.setVisibility(View.GONE);
				password.setVisibility(View.GONE);
				email.setVisibility(View.GONE);
				registerCommand.setVisibility(View.GONE);
				REGISTER_NOT_VISIBLE = true;
			}
			break;

		case R.id.button2:

			if (!USER_IS_LOGGED) {

				if (LOGIN_NOT_VISIBLE) {
					username2.setVisibility(View.VISIBLE);
					password2.setVisibility(View.VISIBLE);
					loginCommand.setVisibility(View.VISIBLE);
					remember.setVisibility(View.VISIBLE);
					fl.setVisibility(View.VISIBLE);
					LOGIN_NOT_VISIBLE = false;
				} else {

					username2.setVisibility(View.GONE);
					password2.setVisibility(View.GONE);
					loginCommand.setVisibility(View.GONE);
					remember.setVisibility(View.GONE);
					fl.setVisibility(View.GONE);
					LOGIN_NOT_VISIBLE = true;
				}
			} else {
				hideButtons(false, false);
				cookieStore.clear();
				USER_IS_LOGGED = false;
				sp.edit().clear().commit();
			}
			break;
		case R.id.button3:
			Intent intent = new Intent(MainActivity.this, Alert.class);
			if (location != null) {
				intent.putExtra("lat", location.getLatitude());
				intent.putExtra("lon", location.getLongitude());
			}
			startActivity(intent);
			break;
		case R.id.registerCommand:
			tryToRegister(username.getText().toString(), email.getText()
					.toString(), password.getText().toString());
			break;
		case R.id.loginCommand:
			tryToLogin(username2.getText().toString(), password2.getText()
					.toString());
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {

		if (hasFocus) {
			if (v == username) {
				username.setHint("");
			} else if (v == password) {
				password.setHint("");
			} else if (v == email) {
				email.setHint("");
			} else if (v == username2) {
				username2.setHint("");
			} else if (v == password2) {
				password2.setHint("");
			}
		} else {

			if (v == username && username.getText().toString().equals("")) {
				username.setHint(R.string.username);
				username.setCompoundDrawables(null, null, null, null);
			} else if (v == username
					&& !username.getText().toString().equals("")) {
				checkUsername(username.getText().toString());
			}

			if (v == password && password.getText().toString().equals("")) {
				password.setHint(R.string.password);
			}

			if (v == email && email.getText().toString().equals("")) {
				email.setHint(R.string.email);
				email.setCompoundDrawables(null, null, null, null);
			} else if (v == email && !email.getText().toString().equals("")) {
				checkEmail(email.getText().toString());
			}

			if (v == password2 && password2.getText().toString().equals("")) {
				password2.setHint(R.string.password);
			}

			if (v == username2 && username2.getText().toString().equals("")) {
				username2.setHint(R.string.username);
			}
		}
	}

	private void checkEmail(String email) {
		RequestParams params = new RequestParams();
		params.put("email", email);
		WebApiClient.getInstance().post("UserEmailCheck", params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] response) {
						updateEmailStatus(true);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] errorResponse, Throwable e) {
						if (statusCode == 409) {
							updateEmailStatus(false);
						}
					}
				});
	}

	private void checkUsername(String username) {
		RequestParams params = new RequestParams();
		params.put("username", username);
		WebApiClient.getInstance().post("UserLoginCheck", params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] response) {
						updateUsernameStatus(true);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] errorResponse, Throwable e) {
						if (statusCode == 409) {
							updateUsernameStatus(false);
						}
					}
				});
	}

	public void hideButtons(boolean hide, boolean isFacebook) {
		
		if (hide) {
			button1.setVisibility(View.GONE);
			username.setVisibility(View.GONE);
			username2.setVisibility(View.GONE);
			email.setVisibility(View.GONE);
			password.setVisibility(View.GONE);
			password2.setVisibility(View.GONE);
			loginCommand.setVisibility(View.GONE);
			registerCommand.setVisibility(View.GONE);
			remember.setVisibility(View.GONE);

			button2.setText(R.string.logout);
			button2.setCompoundDrawables(null, null, null, null);
			LOGIN_NOT_VISIBLE = true;
			REGISTER_NOT_VISIBLE = true;
			if (isFacebook) {
				button2.setVisibility(View.GONE);
				fl.setVisibility(View.VISIBLE);
				//getSupportFragmentManager().beginTransaction().show(mainFragment).commit();
			}
			else {
				button2.setVisibility(View.VISIBLE);
				fl.setVisibility(View.GONE);
				//getSupportFragmentManager().beginTransaction().hide(mainFragment).commit();
			}
		} else {
			button1.setVisibility(View.VISIBLE);
			button2.setText(R.string.login);
			button2.setVisibility(View.VISIBLE);
			fl.setVisibility(View.GONE);
			//getSupportFragmentManager().beginTransaction().hide(mainFragment).commit();
		}
	}

	public void updateUsernameStatus(boolean bool) {
		if (bool) {
			username.setCompoundDrawables(null, null, sd3.getDrawable(), null);
		} else {
			username.setCompoundDrawables(null, null, sd4.getDrawable(), null);
		}
	}

	public void updateEmailStatus(boolean bool) {
		if (bool) {
			email.setCompoundDrawables(null, null, sd3.getDrawable(), null);
		} else {
			email.setCompoundDrawables(null, null, sd4.getDrawable(), null);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (dialog != null) {
			dialog.dismiss();
		}
	}
	
	@Override
	protected void onDestroy() {
		System.exit(0);
	};

	@Override
	public void afterTextChanged(Editable arg0) {
		if (!username.getText().toString().equals("") && !email.getText().toString().equals("") && !password.getText().toString().equals("")) {
			registerCommand.setEnabled(true);
		}
		else {
			registerCommand.setEnabled(false);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		lc.requestLocationUpdates(lr, this);
	}

	@Override
	public void onDisconnected() {
		
	}

	@Override
	public void onLocationChanged(Location arg0) {
		location = arg0;
	}

	public void setLoginInfo(String name, String password, String link, Boolean isFacebook) {
			Editor editor = sp.edit();
			editor.putString("name", name);
			editor.putString("link", link);
			editor.putString("password", password);
			editor.putBoolean("facebook", isFacebook);
			editor.commit();
	}
}
