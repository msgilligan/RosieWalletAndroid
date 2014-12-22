package com.rosiewallet;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.lang.Thread;
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

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends Activity {

	private WebView webView;
	private static final String TAG = MainActivity.class.getSimpleName();
	private MyWebRequestReceiver receiver;
	String scanResult = "";
	private VirtualCoin[] VCArray = new VirtualCoin[4];
	private String CurrentVC = new String("TBTC");
	private boolean MovingWallet = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		InitWallets();
		IntentFilter filter = new IntentFilter(MyWebRequestReceiver.PROCESS_RESPONSE);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
	        receiver = new MyWebRequestReceiver();
	        registerReceiver(receiver, filter);	
		setContentView(R.layout.activity_main);
		webView = (WebView) findViewById(R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebChromeClient(new WebChromeClient());
		webView.addJavascriptInterface(new WebInterface(this), "AndroidHost");
		MoveOldKeys();
		if (!MovingWallet) LoadWallets();
		if (savedInstanceState == null) {
			LoadWebpage("main.html");
		}
	}
	private void InitWallets() {	
		VCArray[VCIndex("TBTC")] = new VirtualCoin();
		VCArray[VCIndex("BTC")] = new VirtualCoin();
		VCArray[VCIndex("LTC")] = new VirtualCoin();
		VCArray[VCIndex("PPC")] = new VirtualCoin();
	}
	private void LoadWallets() {
		ToastIt("Loading Wallets");
		GetKey("TBTC");
		GetKey("BTC");
		GetKey("LTC");
		GetKey("PPC");
	}
	private void MoveOldKeys() {
		if (LocalWalletLoad("TBTC")) GetPublicKey("TBTC");
		if (LocalWalletLoad("BTC"))  GetPublicKey("BTC");
		if (LocalWalletLoad("LTC"))  GetPublicKey("LTC");
		if (LocalWalletLoad("PPC"))  GetPublicKey("PPC");
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
		if (!IsValidVC(vc)) return;
		if (!VCArray[VCIndex(vc)].Loaded) return;
		String SendTrans = "";
		if (new String("").equals(VCArray[VCIndex(vc)].UnspentList)) {
			MessageBox("No inputs to spend.  Your balance is zero.");
			return;
		}
		if (new String("").equals(Address)) {
			MessageBox("No Address specified.");
			return;
		}
		if (VCArray[VCIndex(vc)].spending.size()>0) {
			MessageBox("Spend coin already queued.  Please wait until spend coin result is received.");
			return;
		}
		try {
			Double TotalSpend = round(Double.parseDouble(Amount),8);
			if (TotalSpend<=0.00) {
				MessageBox("Amount must be set correctly.");
				return;
			}
			Double AmtDue = TotalSpend;
			Double AmtFee = round(VCArray[VCIndex(vc)].fee / 100000000.00,8);
			AmtDue = round(AmtDue + AmtFee,8);
			Double change = 0.00;
			Double amtspendable = 0.00;
			try {
				ArrayList<String> ar = new ArrayList<String>();
				JSONObject jObj = new JSONObject(VCArray[VCIndex(vc)].UnspentList);
				JSONArray jArr = jObj.getJSONArray("unspent");
				int transactions = jArr.length();
				JSONObject jsonSendObj = new JSONObject();
				JSONArray jsonSendArr = new JSONArray();
				VCArray[VCIndex(vc)].spending = new ArrayList<String>();
				for (int i = 0; i < transactions; i++) {
					JSONObject trans = (JSONObject)jArr.get(i);
					String tx = (String)trans.get("tx");
					Double amt = round(Double.parseDouble((String)trans.get("amount")),8);
					DecimalFormat df = new DecimalFormat("0.00000000");
					String amtstr = df.format(amt);
					int n = (Integer)trans.get("n");
					int confirmations = (Integer)trans.get("confirmations");
					String script = (String)trans.get("script");
					String txcombo = tx + "-" + Integer.toString(n);
					if (confirmations > 0 && VCArray[VCIndex(vc)].spent.indexOf(txcombo)==-1) {
						if (ar.indexOf(txcombo)==-1) {
							ar.add(txcombo);
							amtspendable += amt;
							if (AmtDue>0) {
								if (round(AmtDue - amt,8) < 0) {
									change = round(amt - AmtDue, 8);
									AmtDue = 0.00;
								}
								else AmtDue = round(AmtDue - amt, 8);
								JSONObject txObj = new JSONObject();
								txObj.put("tx", tx);
								txObj.put("amount", amtstr);
								txObj.put("n", n);
								txObj.put("confirmations", confirmations);
								txObj.put("script", script);
								jsonSendArr.put(txObj);
								if (VCArray[VCIndex(vc)].spending.indexOf(txcombo)==-1)
									VCArray[VCIndex(vc)].spending.add(txcombo);
							}
						}
					}
				}
				jsonSendObj.put("unspent", jsonSendArr);
				SendTrans = jsonSendObj.toString();
			} catch(Exception e) {
			}
			DecimalFormat df = new DecimalFormat("0.00000000");
			String amtspend = df.format(TotalSpend);
			String amtfee = df.format(AmtFee);
			String amttotal = df.format(TotalSpend+AmtFee);
			String amtavail = df.format(amtspendable);
			if (AmtDue>0) {
				MessageBox("Balance too low for transaction.\n"+
						"    Spend Amount: "+amtspend+"\n"+
						"      Fee Amount: "+amtfee+"\n"+
						"    Total Amount: "+amttotal+"\n"+
						"Amount Available: "+amtavail+"\n"+
						(VCArray[VCIndex(vc)].GotBalanceUC && VCArray[VCIndex(vc)].Balance0Confirm != 0.00 ? "You have an unconfirmed balance.  Please wait until this balance is confirmed." : "" ));
			}
			else {
				SendCoinRivet(vc,Address,amtspend,amtfee,SendTrans);
			}
		}
		catch(NumberFormatException e) {}
	}
	public void SendCoinRivet(String vc,String TOPUB,String AMT,String FEE,String TRANS) {
		if (!IsValidVC(vc)) return;
		Intent intent = new Intent(Rivet.RIVET_INTENT)
			.putExtra(Rivet.EXTRA_REQUEST, Rivet.REQUEST_VC_SIGNTRANS)
			.putExtra(Rivet.EXTRA_PROVIDER,1)
			.putExtra(Rivet.EXTRA_KEYNAME,"VC_"+vc)
			.putExtra(Rivet.EXTRA_VC, vc)
			.putExtra(Rivet.EXTRA_TOPUB, TOPUB)
			.putExtra(Rivet.EXTRA_AMT, AMT)
			.putExtra(Rivet.EXTRA_FEE, FEE)
			.putExtra(Rivet.EXTRA_TRANS, TRANS);
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(intent,Rivet.REQUEST_VC_SIGNTRANS);
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Rivet.REQUEST_ECDSA_CREATE && resultCode == RESULT_OK) { // Create Wallet
			String vc = data.getStringExtra(Rivet.EXTRA_VC);
			String PUBLICDATA = data.getStringExtra(Rivet.EXTRA_PUBLICDATA);
			String VCADDRESS = data.getStringExtra(Rivet.EXTRA_VC_PUBADDR);
			if (new String("").equals(PUBLICDATA) == false ) {
				VCArray[VCIndex(vc)].PublicKey = PUBLICDATA;
				VCArray[VCIndex(vc)].PublicAddress = VCADDRESS;
				VCArray[VCIndex(vc)].GotBalance = false;
				VCArray[VCIndex(vc)].GotBalanceUC = false;
				VCArray[VCIndex(vc)].GotBalanceList = false;
				VCArray[VCIndex(vc)].Balance0Confirm = 0;
				VCArray[VCIndex(vc)].Balance1Confirm = 0;
				VCArray[VCIndex(vc)].fee = LoadFee(vc);
				VCArray[VCIndex(vc)].Loaded = true;
				LoadWebpage("main.html");
			}
			else ToastIt("Blank returned from create");
		}
		else if (requestCode == Rivet.REQUEST_GETKEY && resultCode == RESULT_OK) { // Got Wallet
			String vc = data.getStringExtra(Rivet.EXTRA_VC);
			String PUBLICDATA = data.getStringExtra(Rivet.EXTRA_PUBLICDATA);
			String VCADDRESS = data.getStringExtra(Rivet.EXTRA_VC_PUBADDR);
			if (new String("").equals(PUBLICDATA) == false ) {
				VCArray[VCIndex(vc)].PublicKey = PUBLICDATA;
				VCArray[VCIndex(vc)].PublicAddress = VCADDRESS;
				VCArray[VCIndex(vc)].GotBalance = false;
				VCArray[VCIndex(vc)].GotBalanceUC = false;
				VCArray[VCIndex(vc)].GotBalanceList = false;
				VCArray[VCIndex(vc)].Balance0Confirm = 0;
				VCArray[VCIndex(vc)].Balance1Confirm = 0;
				VCArray[VCIndex(vc)].fee = LoadFee(vc);
				VCArray[VCIndex(vc)].Loaded = true;
			}
		}
		else if (requestCode == Rivet.REQUEST_VC_GETPUBPRV && resultCode == RESULT_OK) { // Got PublicKey From Private
			String vc = data.getStringExtra(Rivet.EXTRA_VC);
			VCArray[VCIndex(vc)].PublicKey = data.getStringExtra(Rivet.EXTRA_PUBKEY);
			AddKey(vc);
		}
		else if (requestCode == Rivet.REQUEST_ADDKEY && resultCode == RESULT_OK) { // Added Key to RivetAndroid Delete Local
			String[] keynamelist = data.getStringExtra(Rivet.EXTRA_KEYNAME).split("_");
			if (keynamelist.length == 2) {
				if (new String("VC").equals(keynamelist[0])) {
					DeleteLocalWallet(keynamelist[1]);
				}
			}
		}
		else if (requestCode == Rivet.REQUEST_VC_SIGNTRANS && resultCode == RESULT_OK) { // Got Signed Transaction
			String vc = data.getStringExtra(Rivet.EXTRA_VC);
			String SignedTrans = data.getStringExtra(Rivet.EXTRA_SIGNED);
			String urlstr = "http://rosieswallet.com/api-coin-v1/sendtrans.php?coin="+ vc +"&trans=" + SignedTrans;
			Intent intentd = new Intent(this, GetBalance.class)
				.putExtra(GetBalance.VC,vc)
				.putExtra(GetBalance.TYPE,"send")
				.putExtra(GetBalance.URL, urlstr);
			startService(intentd);
		}
		else if (requestCode == Rivet.REQUEST_DELETEKEY && resultCode == RESULT_OK) { // Got Delete Key
			ToastIt("Wallet Deleted");
		}
		else if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				scanResult = data.getStringExtra("SCAN_RESULT");
			}
			
		}
	}
	public void ToastIt(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
			if (new String("unspent").equals(type)) {
				VCArray[VCIndex(vc)].UnspentList = result;
				try {
					ArrayList<String> ar = new ArrayList<String>();
					ArrayList<String> alltrans = new ArrayList<String>();
					Double amt0 = 0.00;
					Double amt1 = 0.00;
					JSONObject jObj = new JSONObject(result);
					JSONArray jArr = jObj.getJSONArray("unspent");
					int transactions = jArr.length();
					for (int i = 0; i < transactions; i++) {
						JSONObject trans = (JSONObject)jArr.get(i);
						String tx = (String)trans.get("tx");
						Double amt = round(Double.parseDouble((String)trans.get("amount")),8);
						DecimalFormat df = new DecimalFormat("0.00000000");
						String amtstr = df.format(amt);
						int n = (Integer)trans.get("n");
						int confirmations = (Integer)trans.get("confirmations");
						String script = (String)trans.get("script");
						String txcombo = tx + "-" + Integer.toString(n);
						if (alltrans.indexOf(txcombo)==-1) alltrans.add(txcombo);
						if (confirmations > 0) {
							if (ar.indexOf(txcombo)==-1) {
								ar.add(txcombo);
								amt1 += amt;
							}
						}
						else {
							amt0 += amt;
						}
					}
					VCArray[VCIndex(vc)].Balance0Confirm = amt0;
					VCArray[VCIndex(vc)].Balance1Confirm = amt1;
					VCArray[VCIndex(vc)].GotBalanceList = true;
					VCArray[VCIndex(vc)].GotBalanceUC = true;
					VCArray[VCIndex(vc)].GotBalance = true;
					boolean RemovedOne = false;
					for (int j = VCArray[VCIndex(vc)].spent.size()-1; j >= 0; j--) {
						String tx = VCArray[VCIndex(vc)].spent.get(j);
						if (alltrans.indexOf(tx)==-1) {
							VCArray[VCIndex(vc)].spent.remove(tx);
							RemovedOne = true;
						}
					}
					if (RemovedOne) SaveTrans(vc);
				}
				catch(Exception e) {}
			}
			else if (new String("send").equals(type)) {
				if (new String("OK").equals(result)) {
					GetBalanceStart(vc);
					ToastIt("Transaction Sent Successfully");
					for (String tx : VCArray[VCIndex(vc)].spending) {
						if (VCArray[VCIndex(vc)].spent.indexOf(tx)==-1) VCArray[VCIndex(vc)].spent.add(tx);
					}
					SaveTrans(vc);
				}
				else {
					ToastIt("Transaction Returned Error: "+result);
				}
				VCArray[VCIndex(vc)].spending = new ArrayList<String>();
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
	private void GetPublicKey(String vc) {
		if (!IsValidVC(vc)) return;
		if (!MovingWallet) {
			MovingWallet = true;
			ToastIt("Moving Wallet to Rivet");
		}
		Intent intent = new Intent(Rivet.RIVET_INTENT)
			.putExtra(Rivet.EXTRA_REQUEST, Rivet.REQUEST_VC_GETPUBPRV)
			.putExtra(Rivet.EXTRA_VC, vc)
			.putExtra(Rivet.EXTRA_PRVKEY, VCArray[VCIndex(vc)].OldPrivateKey);
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(intent,Rivet.REQUEST_VC_GETPUBPRV);
		}
	}
	private void GetKey(String vc) {
		if (!IsValidVC(vc)) return;
		Intent intent = new Intent(Rivet.RIVET_INTENT)
			.putExtra(Rivet.EXTRA_REQUEST, Rivet.REQUEST_GETKEY)
			.putExtra(Rivet.EXTRA_PROVIDER,1)
			.putExtra(Rivet.EXTRA_KEYNAME,"VC_"+vc)
			.putExtra(Rivet.EXTRA_VC,vc);
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(intent,Rivet.REQUEST_GETKEY);
		}
	}
	private void AddKey(String vc) {
		if (!IsValidVC(vc)) return;
		Intent intent = new Intent(Rivet.RIVET_INTENT)
			.putExtra(Rivet.EXTRA_REQUEST, Rivet.REQUEST_ADDKEY)
			.putExtra(Rivet.EXTRA_PROVIDER,1)
			.putExtra(Rivet.EXTRA_KEYNAME,"VC_"+vc)
			.putExtra(Rivet.EXTRA_PUBLICDATA,VCArray[VCIndex(vc)].PublicKey)
			.putExtra(Rivet.EXTRA_SECUREDATA,VCArray[VCIndex(vc)].OldPrivateKey);
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(intent,Rivet.REQUEST_ADDKEY);
		}
	}
	public void CreateWallet(String vc) {
		if (!IsValidVC(vc)) return;
		VCArray[VCIndex(vc)].Loaded=false;
		Intent intent = new Intent(Rivet.RIVET_INTENT)
			.putExtra(Rivet.EXTRA_REQUEST, Rivet.REQUEST_ECDSA_CREATE)
			.putExtra(Rivet.EXTRA_PROVIDER,1)
			.putExtra(Rivet.EXTRA_VC, vc)
			.putExtra(Rivet.EXTRA_ECC_CURVE, Rivet.CURVE_SECP256K1)
			.putExtra(Rivet.EXTRA_KEYNAME,"VC_"+vc);
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(intent,Rivet.REQUEST_ECDSA_CREATE);
		}
	}
	public void DeleteWallet(String vc) {
		if (!IsValidVC(vc)) return;
		VCArray[VCIndex(vc)].Loaded=false;
		Intent intent = new Intent(Rivet.RIVET_INTENT)
			.putExtra(Rivet.EXTRA_REQUEST, Rivet.REQUEST_DELETEKEY)
			.putExtra(Rivet.EXTRA_PROVIDER,1)
			.putExtra(Rivet.EXTRA_KEYNAME,"VC_"+vc);
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(intent,Rivet.REQUEST_DELETEKEY);
		}
	}
	public boolean IsValidVC(String vc) {
		if (new String("TBTC").equals(vc)) return true;
		if (new String("BTC").equals(vc)) return true;
		if (new String("LTC").equals(vc)) return true;
		if (new String("PPC").equals(vc)) return true;
		return false;
	}
	public void DeleteLocalWallet(String vc) {
		if (!IsValidVC(vc)) return;
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
		editor.remove(Key1);
		editor.remove(Key2);
		editor.apply();
		editor.commit();
	}
	public void LoadWebpage(String htmlfile) {
		String html = LoadData("website/"+htmlfile);
		webView.loadDataWithBaseURL("file:///android_asset/",html,"text/html", "UTF-8",null);
	}
	private String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
	public void GetBalanceStart(String vc) {
		if (!IsValidVC(vc)) return;
		if (!VCArray[VCIndex(vc)].Loaded) return;
		GetUnspentStart(vc);
	}
	public void GetUnspentStart(String vc) {
		if (!IsValidVC(vc)) return;
		if (!VCArray[VCIndex(vc)].Loaded) return;
		String urlstr = "http://rosieswallet.com/api-coin-v1/unspent.php?coin="+ vc +
				"&address=" + VCArray[VCIndex(vc)].PublicAddress;
		Intent intentd = new Intent(this, GetBalance.class)
			.putExtra(GetBalance.VC, vc)
			.putExtra(GetBalance.TYPE, "unspent")
			.putExtra(GetBalance.URL, urlstr);
		startService(intentd);
	}
	public String GetBalanceAddressNew(String vc) {
		if (!IsValidVC(vc)) return "";
		if (!VCArray[VCIndex(vc)].Loaded) return "";
		DecimalFormat df = new DecimalFormat("0.00000000");
		String ReturnStr = df.format(VCArray[VCIndex(vc)].Balance1Confirm);
		if (VCArray[VCIndex(vc)].GotBalance) return ReturnStr;
		return "";
	}
	public String GetUnconfirmedBalanceAddressNew(String vc) {
		if (!IsValidVC(vc)) return "";
		if (!VCArray[VCIndex(vc)].Loaded) return "";
		DecimalFormat df = new DecimalFormat("0.00000000");
		String ReturnStr = df.format(VCArray[VCIndex(vc)].Balance0Confirm);
		if (VCArray[VCIndex(vc)].GotBalanceUC && VCArray[VCIndex(vc)].Balance0Confirm != 0.00) return ReturnStr;
		return "";
	}
	public String GetPrivateKey(String vc) {
		if (!IsValidVC(vc)) return "";
		if (!VCArray[VCIndex(vc)].Loaded) return "";
		if (VCArray[VCIndex(vc)].OldPrivateKey == null) return "";
		return VCArray[VCIndex(vc)].OldPrivateKey;
	}
	public String GetPublicAddress(String vc) {
		if (!IsValidVC(vc)) return "";
		if (!VCArray[VCIndex(vc)].Loaded) return "";
		if (VCArray[VCIndex(vc)].PublicAddress == null) return "";
		return VCArray[VCIndex(vc)].PublicAddress;
	}
	public long LoadFee(String vc) {
		long defaultval=0,retval;
		if (!IsValidVC(vc)) return -1;
		if (new String("TBTC").equals(vc))     defaultval =   100000L;
		else if (new String("BTC").equals(vc)) defaultval =    10000L;
		else if (new String("LTC").equals(vc)) defaultval =   100000L;
		else if (new String("PPC").equals(vc)) defaultval =  1000000L;
		String Key = new StringBuilder()
					.append(vc)
					.append("-Fee")
					.toString();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		retval = settings.getLong(Key, defaultval);
		return retval;
	}
	public void SaveFee(String vc,long newfee) {
		if (!IsValidVC(vc)) return;
		if (newfee<0) return;
		VCArray[VCIndex(vc)].fee = newfee;
		String Key = new StringBuilder()
					.append(vc)
					.append("-Fee")
					.toString();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong(Key, newfee);
		editor.apply();
		editor.commit();
	}
	private void LoadTrans(String vc) {
		if (!IsValidVC(vc)) return;
		String Key = new StringBuilder()
					.append("spent")
					.append(vc)
					.toString();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		String spentlist = settings.getString(Key, "");
		String[] list = spentlist.split(",");
		VCArray[VCIndex(vc)].spent = new ArrayList<String>();
		for (int i = 0; i < list.length; i++) {
			if (new String("").equals(list[i]) == false) {
				VCArray[VCIndex(vc)].spent.add(list[i]);
			}
		}
	}
	private void SaveTrans(String vc) {
		if (!IsValidVC(vc)) return;
		String Key = new StringBuilder()
					.append("spent")
					.append(vc)
					.toString();
		StringBuilder sb = new StringBuilder();
		for (String tx : VCArray[VCIndex(vc)].spent) sb.append(tx).append(",");
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(Key, sb.toString());
		editor.apply();
		editor.commit();
	}
	private Boolean LocalWalletExists(String vc) {
		if (!IsValidVC(vc)) return false;
		String Key1 = new StringBuilder()
					.append("PublicAddress")
					.append(vc)
					.toString();
		String Key2 = new StringBuilder()
					.append("PrivateKey")
					.append(vc)
					.toString();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		String PublicAddress = settings.getString(Key1, "");
		String PrivateKey = settings.getString(Key2, "");
		if (PublicAddress == "" || PrivateKey == "") return false;
		return true;
	}
	private Boolean LocalWalletLoad(String vc) {
		if (!IsValidVC(vc)) return false;
		String Key1 = new StringBuilder()
					.append("PublicAddress")
					.append(vc)
					.toString();
		String Key2 = new StringBuilder()
					.append("PrivateKey")
					.append(vc)
					.toString();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		String PublicAddress = settings.getString(Key1, "");
		String PrivateKey = settings.getString(Key2, "");
		if (PublicAddress == "" || PrivateKey == "") return false;
		VCArray[VCIndex(vc)].PublicAddress = PublicAddress;
		VCArray[VCIndex(vc)].OldPrivateKey = PrivateKey;
		VCArray[VCIndex(vc)].PublicKey = "";
		VCArray[VCIndex(vc)].GotBalance = false;
		VCArray[VCIndex(vc)].GotBalanceUC = false;
		VCArray[VCIndex(vc)].GotBalanceList = false;
		VCArray[VCIndex(vc)].Balance0Confirm = 0;
		VCArray[VCIndex(vc)].Balance1Confirm = 0;
		VCArray[VCIndex(vc)].fee = LoadFee(vc);
		LoadTrans(vc);
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
	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
	private static int VCIndex(String vc) {
		if (new String("BTC").equals(vc)) return 1;
		if (new String("LTC").equals(vc)) return 2;
		if (new String("PPC").equals(vc)) return 3;
		return 0; // default to TBTC
	}
	private class VirtualCoin {
		public String PublicAddress = null;
		public String PublicKey = null;
		public String OldPrivateKey = null;
		public boolean GotBalance = false;
		public boolean GotBalanceUC = false;
		public boolean GotBalanceList = false;
		public double Balance1Confirm = 0;
		public double Balance0Confirm = 0;
		public String UnspentList = "";
		public ArrayList<String> spent = new ArrayList<String>();
		public ArrayList<String> spending = new ArrayList<String>();
		public long fee = 0;
		public boolean Loaded = false;
	}
}
