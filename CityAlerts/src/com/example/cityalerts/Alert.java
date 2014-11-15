package com.example.cityalerts;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class Alert extends Activity implements LocationListener {

	LocationManager locationManager;
	ImageView iv;
	Button buttonTest;
	
	Bitmap bitmap;
	
	List<String> categories = new ArrayList<String>();
	
	Spinner spinner;
	
	ArrayAdapter<String> dataAdapter;
	
	File photo;

	Bitmap scaled;
	
	Uri imageUri;
	
	EditText newCategory;
	
	EditText city,street,description;
	Bitmap bp;
	CheckBox checkBox;
	
	private TextView connectionState;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_alert);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		iv = (ImageView) findViewById(R.id.imageView1);
		connectionState = (TextView) findViewById(R.id.connectionState);

		city = (EditText) findViewById(R.id.city);
		street = (EditText) findViewById(R.id.street);
		description = (EditText) findViewById(R.id.description);
		
		checkBox = (CheckBox) findViewById(R.id.checkbox);

		newCategory = (EditText) findViewById(R.id.newCategory);

		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					newCategory.setVisibility(View.VISIBLE);
				} else {
					newCategory.setVisibility(View.GONE);
				}
			}
		});

		connectionState.setText("asdshadasdl");

		fillGPSInputs();

		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				String fileName = "picture.jpg";
				photo = new File(
						Environment
								.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
						fileName);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
				startActivityForResult(intent, 0);
			}
		});

		buttonTest = (Button) findViewById(R.id.buttonTest);
		buttonTest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				uploadAlert();
			}
		});

		spinner = (Spinner) findViewById(R.id.spinner);
		getCategories();
	}

	private void fillGPSInputs() {

		Location location = getLocation();

		if (location != null) {

			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());

			List<Address> addresses = null;

			try {
				addresses = gcd.getFromLocation(latitude, longitude, 1);
				if (addresses.size() > 0) {
					System.out.println(addresses.get(0).getLocality());

					city.setText(addresses.get(0).getLocality());
					street.setText(addresses.get(0).getAddressLine(0));

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public Location getLocation() {

		Location location = null;

		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0, this);

			location = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (location != null) {
				return location;
			}
		}

		else if (locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, this);

			location = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location != null) {
				return location;
			}
		}
		return location;
	}

	public TextView getConnectionState() {
		return connectionState;
	}

	public void setConnectionState(TextView connectionState) {
		this.connectionState = connectionState;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		bitmap = getResizedBitmap(800, 600, photo.getAbsolutePath());

		ExifInterface exif = null;

		try {
			exif = new ExifInterface(photo.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}

		int orientation = exif
				.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

		if (orientation == 6) {
			Matrix matrix = new Matrix();
			matrix.postRotate(90);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);
			bitmap = Bitmap.createScaledBitmap(bitmap, 600, 800, true);
		} else {
			bitmap = Bitmap.createScaledBitmap(bitmap, 800, 600, true);
		}
		iv.setImageBitmap(bitmap);
	}

	public Bitmap getResizedBitmap(int targetWidth, int targetHeight,
			String imagePath) {

		BitmapFactory.Options bmOptions = new BitmapFactory.Options();

		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagePath, bmOptions);
		int photoWidth = bmOptions.outWidth;
		int photoHeight = bmOptions.outHeight;

		int scaleFactor = Math.min(photoWidth / targetWidth, photoHeight
				/ targetHeight);

		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
		return bitmap;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.alert, menu);
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
	public void onLocationChanged(Location paramLocation) {
		// Toast.makeText(getBaseContext(),
		// Double.toString(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude()),
		// Toast.LENGTH_LONG).show();
	}

	@Override
	public void onProviderDisabled(String paramString) {
		// startActivity(new
		// Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	}

	@Override
	public void onProviderEnabled(String paramString) {
		Toast.makeText(getBaseContext(), "Gps turned on ", Toast.LENGTH_LONG)
				.show();
	}

	@Override
	public void onStatusChanged(String paramString, int paramInt,
			Bundle paramBundle) {

	}
	
	ProgressDialog dialog;
	
	private void uploadAlert() {
		RequestParams params = new RequestParams();
		
		if (bitmap != null) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.PNG, 100, bos);
			byte[] bitmapdata = bos.toByteArray();		
			params.put("image", new ByteArrayInputStream(bitmapdata), "picture.png");
		}
		
		Location location = getLocation();
		String city = this.city.getText().toString();
		String street = this.street.getText().toString();
		String description = this.description.getText().toString();
		String category = (String) this.spinner.getSelectedItem();
		
		if (location != null) {
			params.put("latitude", location.getLatitude());
			params.put("longitude", location.getLongitude());
		}
		if (city != null) {
			params.put("city", city);
		}
		if (street != null) {
			params.put("street", street);
		}
		if (description != null) {
			params.put("description", description);
		}
		if (category != null) {
			params.put("category", category);
		}

		WebApiClient.getInstance().post("UploadFile",params, new AsyncHttpResponseHandler() {
			
			@Override
		    public void onStart() {
		    	dialog = new ProgressDialog(Alert.this);
		        dialog.setMessage(getResources().getString(R.string.uploading));
		        dialog.setIndeterminate(false);
		        dialog.setCancelable(true);
		        dialog.show();
		    }
			
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				
				dialog.hide();
		        AlertDialog.Builder builder = new AlertDialog.Builder(Alert.this);
		    	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Alert.this.finish();
					}
				});
		    	AlertDialog dialog = builder.create();
		    	TextView title = new TextView(Alert.this);
		    	title.setText(R.string.sent);	
		    	title.setGravity(Gravity.CENTER);
		    	title.setTextSize(20);
		    	dialog.setCustomTitle(title);
		    	dialog.show();
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				dialog.hide();
			}
		});
	}
	
	private List<String> getCategories() {
		
		List<String> list = new ArrayList<String>();
		WebApiClient.getInstance().post("Category", new JsonHttpResponseHandler() {
			
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				int b = 7;
				
				for (int i = 0; i < response.length(); i++) {
					try {
						categories.add((String) response.get(i));
					} catch (JSONException e) {
						e.printStackTrace();
					} 
				}
				
				ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Alert.this,
						android.R.layout.simple_spinner_item, categories);
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinner.setAdapter(dataAdapter);
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
				int c = 8;
			}
			
			
		});
		return null;
	}

	@Override
	public void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}
	
}
