package com.example.cityalerts;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class InternetState extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		if(intent.getExtras()!=null) {
			
	        NetworkInfo ni=(NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
	        if(ni!=null && ni.getState()==NetworkInfo.State.CONNECTED) {
	            Toast.makeText(context, "OK", Toast.LENGTH_SHORT).show();
	        }
	     }
		
	     if(intent.getExtras().getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY,Boolean.FALSE)) {
	    	 Toast.makeText(context, "BRAK", Toast.LENGTH_SHORT).show();
	     }

	}

}
