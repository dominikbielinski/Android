package com.example.cityalerts;

import android.app.Application;

public class MyApplication extends Application{
	
	@Override
	  public void onCreate() {
	    super.onCreate();
	    initSingletons();
	  }
	 
	  protected void initSingletons() {
		  DbConnection.getInstance();
	  }
}
