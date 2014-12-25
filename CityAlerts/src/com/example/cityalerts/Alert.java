package com.example.cityalerts;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class Alert extends Activity  {

	ImageView iv;
	Button buttonTest;
	ProgressDialog dialog;
	Bitmap bitmap;
	List<String> categories = new ArrayList<String>();
	List<Integer> categoryIds = new ArrayList<Integer>();
	Spinner spinner;
	ArrayAdapter<String> dataAdapter;
	File photo;
	EditText newCategory;
	EditText city,street,description;
	CheckBox checkBox;
	Button categoryText;
	Location location;
	private UiLifecycleHelper uiHelper;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		uiHelper = new UiLifecycleHelper(this, null);
	    uiHelper.onCreate(savedInstanceState);
	    
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_alert);
		
		iv = (ImageView) findViewById(R.id.imageView1);

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
		
		location = new Location("");
		location.setLatitude(getIntent().getExtras().getDouble("lat"));
		location.setLongitude(getIntent().getExtras().getDouble("lon"));

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
		
		if (location != null) {

			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
			List<Address> addresses = null;

			try {
				addresses = gcd.getFromLocation(latitude, longitude, 1);
				if (addresses.size() > 0) {
					city.setText(addresses.get(0).getLocality());
					street.setText(addresses.get(0).getAddressLine(0));

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		
		uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
	        @Override
	        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
	            System.out.println("blad");
	        }

	        @Override
	        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
	            System.out.println("brawo");
	        }
	    });
		
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
	
	private void uploadAlert() {
		RequestParams params = new RequestParams();
		
		if (bitmap != null) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.PNG, 100, bos);
			byte[] bitmapdata = bos.toByteArray();		
			params.put("image", new ByteArrayInputStream(bitmapdata), "picture.png");
		}
		
		String city = this.city.getText().toString();
		String street = this.street.getText().toString();
		String description = this.description.getText().toString();
		String category = null;
		
		if (!categoryIds.isEmpty()) {
			if (checkBox.isChecked()) {
				category = newCategory.getText().toString();
				params.put("category", category);
			}
			else {
				category = categoryIds.get(spinner.getSelectedItemPosition()).toString();
				params.put("categoryid", category);
			}
		}
		
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

		WebApiClient.getInstance().post("UploadAlert",params, new JsonHttpResponseHandler() {
			@Override
		    public void onStart() {
		    	dialog = new ProgressDialog(Alert.this);
		        dialog.setMessage(getResources().getString(R.string.uploading));
		        dialog.setIndeterminate(false);
		        dialog.setCancelable(true);
		        dialog.show();
		    }
			
			@Override
			public void onSuccess(int arg0, Header[] arg1, final JSONObject response) {
				
				dialog.hide();
		        AlertDialog.Builder builder = new AlertDialog.Builder(Alert.this);
		    	builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Alert.this.finish();
					}
				});
		    	builder.setPositiveButton("Share", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						FacebookDialog shareDialog = null;
						try {
							String alertPath = "http://" + WebApiClient.BASE_IP + "/Home/ShowAlerts&id=" + response.getInt("alertID");
							String imagePath = "http://" + WebApiClient.BASE_IP + response.getString("imagePath").substring(1);
							String alertDescription = response.getString("description");
							shareDialog = new FacebookDialog.ShareDialogBuilder(Alert.this)
							.setLink(alertPath)
							.setPicture(imagePath)
							.setDescription(alertDescription)
							.build();
							uiHelper.trackPendingDialogCall(shareDialog.present());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
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
			public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
				dialog.hide();

				AlertDialog.Builder builder = new AlertDialog.Builder(
						Alert.this);
				builder.setPositiveButton("OK", null);
				AlertDialog dialog = builder.create();

				TextView title = new TextView(Alert.this);
				
				title.setText(R.string.noConnection);

				title.setGravity(Gravity.CENTER);
				title.setTextSize(20);
				dialog.setCustomTitle(title);
				dialog.show();
			}
			
		});
	}
	
	private List<String> getCategories() {
		
		WebApiClient.getInstance().get("Category", new JsonHttpResponseHandler() {
			
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

				Gson gson = new Gson();
				for(int i = 0; i < response.length(); i++){
		            try {
						JSONObject jsonObj = response.getJSONObject(i);
						Category category = gson.fromJson(jsonObj.toString(), Category.class);
						categories.add(category.name);
						categoryIds.add(category.categoryID);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Alert.this,
						R.layout.spinner_item, categories);
				//	dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinner.setAdapter(dataAdapter);
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
				
			}
			
			
		});
		return null;
	}

	@Override
	public void onPause() {
		super.onPause();
		if (dialog != null) {
			dialog.dismiss();
		}
		uiHelper.onPause();
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}
	
}
