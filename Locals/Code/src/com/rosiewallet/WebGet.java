package com.rosiewallet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;
import android.os.Handler;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;


public class WebGet extends IntentService {

	private int result = Activity.RESULT_CANCELED;
	public static final String URL = "urlpath";
	public static final String CALLID = "CallId";
	public static final String TYPE = "type";
	public static final String CONFIRMS = "confirms";
	public static final String RESULT = "result";
	public static final String NOTIFICATION = "com.rosiewallet.WebGet";
	public static final String ACTION_WebGet = "com.rosiewallet.RESPONSE";
	private static final String PENDING_RESULT="com.rosiewallet.PENDING_RESULT";
	private static final String BROADCAST_ACTION="com.rosiewallet.BROADCAST_ACTION";
	private static final String BROADCAST_PACKAGE="com.rosiewallet.BROADCAST_PACKAGE";
	Handler mHandler;
	
	public WebGet() {
		super("com.rosiewallet.WebGet");
		mHandler = new Handler();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Intent data=new Intent();
		data.putExtra(RESULT, 1);
		String urlPath = intent.getStringExtra(URL);
		String confirms = intent.getStringExtra(CONFIRMS);
		String CallId = intent.getStringExtra(CALLID);
		String type = intent.getStringExtra(TYPE);
		String broadcast = intent.getStringExtra(BROADCAST_ACTION);
		InputStream stream = null;
		try {
			URL url = new URL(urlPath);
			stream = url.openConnection().getInputStream();
			String ResultStr = convertStreamToString(stream);
			result = Activity.RESULT_OK;
			data.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
			data.putExtra(RESULT,ResultStr);
			data.putExtra(CONFIRMS,confirms);
			data.putExtra(CALLID,CallId);
			data.putExtra(TYPE,type);
			data.setAction("com.rosiewallet.PRIVATE_BROADCAST_ACTION");
			sendBroadcast(data);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	private static String convertStreamToString(java.io.InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}
}
