package com.example.cityalerts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import android.app.Application;
import android.location.LocationManager;
import android.os.Environment;

public class MyApplication extends Application {
	
	LocationManager locationManager;
	
	@Override
	public void onCreate() {
		super.onCreate();
		WebApiClient.getInstance();
		

		StringBuilder text = new StringBuilder();
		try {
			File file = new File(Environment.getExternalStorageDirectory() +
	            "/Documents/ip.txt");
			
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				text.append(line);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    WebApiClient.BASE_URL = "http://" + text.toString() + "/Api/";
	    WebApiClient.BASE_IP = text.toString();
	}
}
