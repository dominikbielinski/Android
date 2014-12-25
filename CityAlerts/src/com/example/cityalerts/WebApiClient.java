package com.example.cityalerts;

import android.content.SharedPreferences;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class WebApiClient {
	
	SharedPreferences sp;

	public static String BASE_URL = "http://192.168.1.2:53909/Api/";

	private static WebApiClient instance = null;

	public static String BASE_IP = null;

	public static WebApiClient getInstance() {
		
		if (instance == null) {
			instance = new WebApiClient();
			return instance;
		}
		return instance;
	}

	private AsyncHttpClient client = new AsyncHttpClient();

	public AsyncHttpClient getClient() {
		return client;
	}

	public void get(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}

	public void post(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.post(getAbsoluteUrl(url), params, responseHandler);
	}

	private String getAbsoluteUrl(String relativeUrl) {
		return BASE_URL + relativeUrl;
	}
	
	public void post(String url, JsonHttpResponseHandler responseHandler) {
		client.post(getAbsoluteUrl(url), responseHandler);
	}
	
	public void get(String url, JsonHttpResponseHandler responseHandler) {
		client.get(getAbsoluteUrl(url), responseHandler);
	}
}
