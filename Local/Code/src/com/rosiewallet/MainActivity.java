package com.rosiewallet;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.Menu;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private WebView webView;
	private static final String TAG = MainActivity.class.getSimpleName();
	private Activity activity=this;
	private String mainhtml = null;
	private String PublicAddress = null;
	private String PrivateKey = null;
	private String oldvc = new String("TBTC");
	private static final String PREFS_BTC = "BTC_PREFS";
	private boolean GotBalance = false;
	private boolean GotBalanceUC = false;
	private boolean GotBalanceList = false;
	private double Balance1Confirm = 0;
	private double Balance0Confirm = 0;
	private String UnspentList = "";
	private long fee = 0;
	private MyWebRequestReceiver receiver;
	String scanResult = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		IntentFilter filter = new IntentFilter(MyWebRequestReceiver.PROCESS_RESPONSE);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
	        receiver = new MyWebRequestReceiver();
	        registerReceiver(receiver, filter);	
		setContentView(R.layout.activity_main);
		webView = (WebView) findViewById(R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebChromeClient(new WebChromeClient());
		webView.addJavascriptInterface(new WebInterface(this), "AndroidHost");
		if (!LoadWallet("TBTC")) {
			
		}
		if (savedInstanceState == null) {
			LoadWebpage("main.html");
		}
	}
	public void ScanQRCode() {
		try {
			Intent intent = new Intent("com.google.zxing.client.android.SCAN")
				.putExtra("SCAN_MODE", "QR_CODE_MODE");
			if (intent.resolveActivity(getPackageManager()) != null) {
				startActivityForResult(intent, 0);
			}
		} catch (Exception e) {    
			Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
			Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
			startActivity(marketIntent);
		}
	}
	public String ScanResult() {
		String Retval = "";
		if (new String("").equals(scanResult) == false) {
			Retval = scanResult;
			scanResult = "";
		}
		return Retval;
	}
	public void sendcoin(String vc, String Address, String Amount) {
		String SendTrans = "";
		boolean FirstTime = true;
		if (UnspentList=="") return;
		if (!IsValidVC(vc)) return;
		if ((new String(oldvc).equals(vc)) == false) {
			LoadWallet(vc);
		}
		try {
			Double TotalSpend = Double.parseDouble(Amount);
			Double AmtDue = TotalSpend;
			if (new String("TBTC").equals(vc)) {
			}
			else {
				AmtDue += fee;
			}
			Double change = 0.00;
			ArrayList<String> ar = new ArrayList<String>();
			String[] UnSpentArr = UnspentList.split(",");
			for(int i=0; i<UnSpentArr.length; i++) {
				String[] UnSpentRow = UnSpentArr[i].split("&");
				Double b = 0.00;
				int txconfirms = -1;
				boolean isduplicate = false;
				int n = -1;
				String script = "";
				String tx = "";
				for(int y=0; y<UnSpentRow.length; y++) {
					String[] UnSpentField = UnSpentRow[y].split("=");
					if (UnSpentField.length==2) {
						if (new String("tx").equals(UnSpentField[0])) {
							tx = UnSpentField[1];
							if (ar.indexOf(UnSpentField[1])==-1)
								ar.add(UnSpentField[1]);
							else
								isduplicate=true;
						}
						else if (new String("amt").equals(UnSpentField[0])) {
							b = Double.parseDouble(UnSpentField[1]);
						}
						else if (new String("n").equals(UnSpentField[0])) {
							n = Integer.parseInt(UnSpentField[1]);
						}
						else if (new String("confirms").equals(UnSpentField[0])) {
							txconfirms = Integer.parseInt(UnSpentField[1]);
						}
						else if (new String("script").equals(UnSpentField[0])) {
							script = UnSpentField[1];
						}
						else {
						}
					}
				}
				if (isduplicate==false && txconfirms>0 && AmtDue>0) {
					if (AmtDue - b < 0) {
						change = b - AmtDue;
						AmtDue = 0.00;
					}
					else AmtDue -= b;
					if (FirstTime) FirstTime = false;
					else SendTrans+=",";
					SendTrans += UnSpentArr[i];
				}
			}
			if (AmtDue>0) MessageBox("Balance too low for transaction.");
			else {
				DecimalFormat df = new DecimalFormat("0.00000000");
				String ToSpendStr = df.format(TotalSpend);
				SendCoinRivet(vc,PublicAddress,PrivateKey,Address,ToSpendStr,SendTrans);
			}
		}
		catch(NumberFormatException e) {}
	}
	public void SendCoinRivet(String vc,String PUB,String PRV,String TOPUB,String AMT,String TRANS) {
		if (!IsValidVC(vc)) return;
		Intent intent = new Intent(Rivet.RIVET_INTENT)
			.putExtra(Rivet.EXTRA_REQUEST, Rivet.REQUEST_SIGNTRANS)
			.putExtra(Rivet.EXTRA_VC, vc)
			.putExtra(Rivet.EXTRA_PUB, PUB)
			.putExtra(Rivet.EXTRA_PRV, PRV)
			.putExtra(Rivet.EXTRA_TOPUB, TOPUB)
			.putExtra(Rivet.EXTRA_AMT, AMT)
			.putExtra(Rivet.EXTRA_FEE,fee)
			.putExtra(Rivet.EXTRA_TRANS, TRANS);
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(intent,Rivet.REQUEST_SIGNTRANS);
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Rivet.REQUEST_DELETEWALLET && resultCode == RESULT_OK) { // Delete Wallet
			if (LoadWallet("TBTC")) LoadWebpage("main.html");
		}
		if (requestCode == Rivet.REQUEST_SIGNTRANS && resultCode == RESULT_OK) { // Get New Wallet
			String SignedTrans = data.getStringExtra("SignedTrans");
			String vc = data.getStringExtra(Rivet.EXTRA_VC);
			String AMT = data.getStringExtra(Rivet.EXTRA_AMT);
			String TOPUB = data.getStringExtra(Rivet.EXTRA_TOPUB);
			String urlstr = "http://rosieswallet.com/api-coin-v1/sendtrans.cgi.php?coin="+ vc +"&trans=" + SignedTrans;
			Intent intentd = new Intent(this, GetBalance.class)
				.putExtra(GetBalance.VC,vc)
				.putExtra(GetBalance.TYPE,"send")
				.putExtra(GetBalance.URL, urlstr);
			startService(intentd);
			
		}
		if (requestCode == Rivet.REQUEST_GETWALLET && resultCode == RESULT_OK) {
			PublicAddress = data.getStringExtra("PublicAddress");
			PrivateKey = data.getStringExtra("PrivateKey");
			String vc = data.getStringExtra("vc");
			SaveWallet(vc);
			if (LoadWallet("TBTC")) LoadWebpage("main.html");
		}
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				scanResult = data.getStringExtra("SCAN_RESULT");
			}
			
		}
	}
	public void ToastIt(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		
	}
	@Override
	public void onDestroy() {
		this.unregisterReceiver(receiver);
		super.onDestroy();
	}
	public class MyWebRequestReceiver extends BroadcastReceiver {
		public static final String PROCESS_RESPONSE = "com.rosiewallet.PRIVATE_BROADCAST_ACTION";
		@Override
		public void onReceive(Context context, Intent intent) {
			String result = intent.getStringExtra(GetBalance.RESULT);
			String confirms = intent.getStringExtra(GetBalance.CONFIRMS);
			String vc = intent.getStringExtra(GetBalance.VC);
			String type = intent.getStringExtra(GetBalance.TYPE);
			if (new String("balance").equals(type)) {
				if (new String(vc).equals(oldvc)) {
					try {
						if (new String("-1").equals(confirms)) {
							Double amt0 = 0.00;
							Double amt1 = 0.00;
							ArrayList<String> ar = new ArrayList<String>();
							UnspentList = result;
							String[] UnSpentArr = UnspentList.split(",");
							for(int i=0; i<UnSpentArr.length; i++) {
								String[] UnSpentRow = UnSpentArr[i].split("&");
								Double b = 0.00;
								int txconfirms = -1;
								boolean isduplicate = false;
								for(int y=0; y<UnSpentRow.length; y++) {
									String[] UnSpentField = UnSpentRow[y].split("=");
									if (UnSpentField.length==2) {
										if (new String("tx").equals(UnSpentField[0])) {
											if (ar.indexOf(UnSpentField[1])==-1)
												ar.add(UnSpentField[1]);
											else
												isduplicate=true;
										}
										else if (new String("amt").equals(UnSpentField[0])) {
											b = Double.parseDouble(UnSpentField[1]);
										}
										else if (new String("n").equals(UnSpentField[0])) {
										}
										else if (new String("confirms").equals(UnSpentField[0])) {
											txconfirms = Integer.parseInt(UnSpentField[1]);
										}
										else if (new String("script").equals(UnSpentField[0])) {
										}
										else {
										}
									}
								}
								if (isduplicate==false) {
									if (txconfirms==0) amt0 += b;
									else if (txconfirms>0) amt1 += b;
								}
							}
							Balance0Confirm = amt0;
							Balance1Confirm = amt1;
							GotBalanceList = true;
							GotBalanceUC = true;
							GotBalance = true;
						}
						if (new String("0").equals(confirms)) {
							Balance0Confirm = Double.parseDouble(result) - Balance1Confirm;
							GotBalanceUC = true;
						}
						else {
							Balance1Confirm = Double.parseDouble(result);
							GotBalance = true;
						}
					}
					catch(NumberFormatException e) {
						
					}
				}
			}
			else if (new String("send").equals(type)) {
				if (new String("OK").equals(result)) {
					if (new String(vc).equals(oldvc)) GetBalanceStart(vc);
					ToastIt("Transaction Sent Successfully");
				}
				else {
					ToastIt("Transaction Returned Error: "+result);
				}
			}
		}
	}
	public void MessageBox(String Message) {
		AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
		dlgAlert.setMessage(Message);
		dlgAlert.setTitle("RosieWallet Test App");
		dlgAlert.setPositiveButton("OK", null);
		dlgAlert.setCancelable(true);
		dlgAlert.create().show();
		dlgAlert.setPositiveButton("Ok",
		    new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//dismiss the dialog  
			}
    		});
	}
	public void CreateWallet(String vc) {
		if (!IsValidVC(vc)) return;
		Intent intent = new Intent(Rivet.RIVET_INTENT)
			.putExtra(Rivet.EXTRA_REQUEST, Rivet.REQUEST_GETWALLET)
			.putExtra(Rivet.EXTRA_VC, vc);
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(intent,Rivet.REQUEST_GETWALLET);
		}
	}
	public boolean IsValidVC(String vc) {
		if (new String("TBTC").equals(vc)) return true;
		if (new String("BTC").equals(vc)) return true;
		if (new String("LTC").equals(vc)) return true;
		if (new String("PPC").equals(vc)) return true;
		return false;
	}
	public void DeleteWallet(String vc) {
		if (!IsValidVC(vc)) return;
		String Key1 = new StringBuilder()
					.append("PublicAddress")
					.append(vc)
					.toString();
		String Key2 = new StringBuilder()
					.append("PrivateKey")
					.append(vc)
					.toString();
		PublicAddress = null;
		PrivateKey = null;
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = settings.edit();
		editor.remove(Key1);
		editor.remove(Key2);
		editor.apply();
		editor.commit();
		oldvc = new String(vc);
	}
	public void LoadWebpage(String htmlfile) {
		mainhtml = LoadData("website/"+htmlfile);
		webView.loadDataWithBaseURL("fake://not/needed",
				mainhtml,"text/html", "UTF-8",null);
	}
	private String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
	public void GetBalanceStart(String vc) {
		if (!IsValidVC(vc)) return;
		GetBalanceStartList(vc);
	}
	public void GetBalanceStartList(String vc) {
		if (!IsValidVC(vc)) return;
		Intent intentd = new Intent(this, GetBalance.class);
		intentd.putExtra("confirms", "-1");
		intentd.putExtra("vc",vc);
		intentd.putExtra("type","balance");
		String urlstr = "http://rosieswallet.com/api-coin-v1/getbalance.cgi.php?coin="+ vc +"&listall=true&address=" + PublicAddress + "&confirmations=0";
		intentd.putExtra(GetBalance.URL, urlstr);
		startService(intentd);
		
	}
	public void GetBalanceStartConfirm(String vc,int confirms) {
		if (!IsValidVC(vc)) return;
		String confirm = Integer.toString(confirms);
		String urlstr = "http://rosieswallet.com/api-coin-v1/getbalance.cgi.php?coin="+ vc +"&address=" + PublicAddress + "&confirmations=" + confirm ;
		Intent intentd = new Intent(this, GetBalance.class)
			.putExtra(GetBalance.CONFIRMS, confirm)
			.putExtra(GetBalance.VC,vc)
			.putExtra(GetBalance.TYPE,"balanceonly")
			.putExtra(GetBalance.URL, urlstr);
		startService(intentd);
		
	}
	public String GetBalanceAddressNew(String vc) {
		DecimalFormat df = new DecimalFormat("0.00000000");
		String ReturnStr = df.format(Balance1Confirm);
		if (GotBalance) return ReturnStr;
		return "";
	}
	public String GetUnconfirmedBalanceAddressNew(String vc) {
		DecimalFormat df = new DecimalFormat("0.00000000");
		String ReturnStr = df.format(Balance0Confirm);
		if (GotBalanceUC && Balance0Confirm != 0.00) return ReturnStr;
		return "";
	}
	public String GetPrivateKey(String vc) {
		if (!IsValidVC(vc)) return "";
		if ((new String(oldvc).equals(vc)) == false) {
			LoadWallet(vc);
		}
		if (PrivateKey == null) return "";
		return PrivateKey;
	}
	public String GetPublicAddress(String vc) {
		if (!IsValidVC(vc)) return "";
		if ((new String(oldvc).equals(vc)) == false) {
			LoadWallet(vc);
		}
		if (PublicAddress == null) return "";
		return PublicAddress;
	}
	public long LoadFee(String vc) {
		long defaultval=0,retval;
		if (!IsValidVC(vc)) return -1;
		if (new String("TBTC").equals(vc))     defaultval =   100000L;
		else if (new String("BTC").equals(vc)) defaultval =    10000L;
		else if (new String("LTC").equals(vc)) defaultval =   100000L;
		else if (new String("PPC").equals(vc)) defaultval =  1000000L;
		String Key1 = new StringBuilder()
					.append(vc)
					.append("-Fee")
					.toString();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		retval = settings.getLong(Key1, defaultval);
		return retval;
	}
	public void SaveFee(String vc,long newfee) {
		if (!IsValidVC(vc)) return;
		if (newfee<0) return;
		if (oldvc == vc) fee = newfee;
		String Key1 = new StringBuilder()
					.append(vc)
					.append("-Fee")
					.toString();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong(Key1, newfee);
		editor.apply();
		editor.commit();
	}
	private void SaveWallet(String vc) {
		if (!IsValidVC(vc)) return;
		oldvc = new String(vc);
		String Key1 = new StringBuilder()
					.append("PublicAddress")
					.append(vc)
					.toString();
		String Key2 = new StringBuilder()
					.append("PrivateKey")
					.append(vc)
					.toString();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(Key1, PublicAddress);
		editor.putString(Key2, PrivateKey);
		if (PublicAddress=="") PublicAddress=null;
		if (PrivateKey=="") PrivateKey=null;
		editor.apply();
		editor.commit();
	}
	private boolean SaveOldWallet(String vc,String pair) {
		if (!IsValidVC(vc)) return false;
		String[] splited = pair.split("\\s+");
		if (splited.length != 2) return false;
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		String Key1 = new StringBuilder()
					.append("PublicAddress")
					.append(vc)
					.toString();
		String Key2 = new StringBuilder()
					.append("PrivateKey")
					.append(vc)
					.toString();
		String CurKey1 = settings.getString(Key1, "");
		String CurKey2 = settings.getString(Key2, "");
		if (CurKey1=="") {
			SharedPreferences.Editor editor = settings.edit();
		        String TempPub = splited[0];
			String TempPrv = splited[1];
			if (TempPub != "" && TempPrv != "") {
				editor.putString(Key1, TempPub);
				editor.putString(Key2, TempPrv);
				editor.apply();
				editor.commit();
				return true;
			}
		}
		return false;
	}
	private void MoveOldWalletEx(String vc) {
		if (!IsValidVC(vc)) return;
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		String Key1 = new StringBuilder()
					.append("PublicAddress")
					.append(vc)
					.toString();
		String Key2 = new StringBuilder()
					.append("PrivateKey")
					.append(vc)
					.toString();
		if (!settings.contains(Key1)) {
			SharedPreferences.Editor editor = settings.edit();
		        String TempPub = "";
			String TempPrv = "";
			if (TempPub != "" && TempPrv != "") {
				editor.putString(Key1, TempPub);
				editor.putString(Key2, TempPrv);
				editor.apply();
				editor.commit();
			}
		}
	}
	private void MoveOldWallet() {
		MoveOldWalletEx("TBTC");
		MoveOldWalletEx("BTC");
		MoveOldWalletEx("LTC");
		MoveOldWalletEx("PPC");
	}
	private Boolean LoadWallet(String vc) {
		if (!IsValidVC(vc)) return false;
		MoveOldWallet();
		String Key1 = new StringBuilder()
					.append("PublicAddress")
					.append(vc)
					.toString();
		String Key2 = new StringBuilder()
					.append("PrivateKey")
					.append(vc)
					.toString();
		oldvc = new String(vc);
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		PublicAddress = settings.getString(Key1, "");
		PrivateKey = settings.getString(Key2, "");
		GotBalance = false;
		GotBalanceUC = false;
		GotBalanceList = false;
		Balance0Confirm = 0;
		Balance1Confirm = 0;
		if (PublicAddress == "" || PrivateKey == "") return false;
		fee = LoadFee(vc);
		return true;
	}
	public String LoadRawData(int resID) {
		String Result = null;
		try {
			Resources res = getResources();
			InputStream in_s = res.openRawResource(resID);

			byte[] b = new byte[in_s.available()];
			in_s.read(b);
			Result = new String(b);
		} catch (Exception e) {
			Result = "Error: can't load raw data.";
		}
		return Result;
	}
	public String LoadData(String inFile) {
		String tContents = "No Data";
		try {
			InputStream stream = getResources().getAssets().open(inFile);
			int size = stream.available();
			byte[] buffer = new byte[size];
			stream.read(buffer);
			stream.close();
			tContents = new String(buffer);
		} catch (IOException e) {
			
		}
		return tContents;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	protected void onSaveInstanceState(Bundle outState )
	{
		super.onSaveInstanceState(outState);
		webView.saveState(outState);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		webView.restoreState(savedInstanceState);
	}
	public String taName(String uuid) {
		return uuid + ".tlbin";
	}
	public void LoadHTML(String message)
	{
		webView.loadData(message,"text/html","UTF-8");
	}
	private String GenerateNWd(String vc) {
		if (!IsValidVC(vc)) return "";
                Log.d(Constants.LOG_TAG, "GenerateNWd "+vc);
		String cipherText = "";
		return  cipherText;
	}
}
