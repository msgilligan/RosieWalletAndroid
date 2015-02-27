package com.rosiewallet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends Activity  {

	private WebView webView;
	private static final String TAG = MainActivity.class.getSimpleName();
	private MyWebRequestReceiver receiver;
	String scanResult = "";
	private VirtualCoin[] VCArray = new VirtualCoin[4];
	private boolean MovingWallet = false;
	private static final String SP_UUID = "ca8ebc88-86e0-4f14-91e3-ea66037b3ab3-e8cd2413-a12a-469c-b4af-f61257500662";
	public static final String MIME_TEXT_PLAIN = "text/plain";
	private NfcAdapter mNfcAdapter;
	private String VCStartWith;
	private String NFCMessage;
	
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
			VCStartWith = new String("TBTC");
			LoadWebpage("main.html");
		}
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (mNfcAdapter != null) {
			if (!mNfcAdapter.isEnabled()) {
				Toast.makeText(this, "NFC is disabled.", Toast.LENGTH_LONG).show();
			}
			handleIntent(getIntent());
		}
	}
	public void doGetPointer(String mySPID, String keyName) {
		Intent intent = new Intent(Rivet.RIVET_INTENT)
			.putExtra(Rivet.EXTRA_INSTRUCT, Rivet.INSTRUCT_GETPOINTER)
			.putExtra(Rivet.EXTRA_SPID, SP_UUID);
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(intent,Rivet.INSTRUCT_GETPOINTER);
		}
	}
	protected void resultGetPointer(int resultCode, Intent data) {
		int RivetResultCode = 0; // data.getIntExtra(Rivet.EXTRA_RESULTCODE,-1);
		if (RivetResultCode != 0) {
			ToastIt("GetPointer returned error: "+String.valueOf(RivetResultCode));
			return;
		}
		if (resultCode == RESULT_OK) {
			String devicePointer = data.getStringExtra(Rivet.EXTRA_DEVICEPOINTER);
			ToastIt("Device Pointer is" + devicePointer);
		}
	}
	public void SendCoinRivet(String vc,String TOPUB,String AMT,String FEE,String TRANS) {
		if (!IsValidVC(vc)) return;

/*******************************************************************
********************************************************************
********************************************************************
******
******	INTENT Example: Rivet.INSTRUCT_SIGNTXN
******
******	Description:
******
******		Sign a virtual coin transaction.
******		Returns either ready data to transmit to network or signed transaction for another multisig wallet to sign.
******
******	Required Field(s):
******
******		EXTRA_SPID (String / UUID 32) - Pass your assigned Service Provider ID.
******		EXTRA_KEYNAME (String Max 32) - Keyname RivetAndroid uses to store your private key to be used to sign with.
******		EXTRA_VC (String) - Virtual Coin being used for signing.  BTC = bitcoin, LTC = Litecoin, PPC = Peercoin
******		EXTRA_TOPUB (String) - Public Address to send virtual coin to.
******		EXTRA_AMT (String) - Amount of coin to send.
******		EXTRA_FEE (String) - Network Fee to include with the transaction to be given to the network block reward.
******		EXTRA_TRANS (String) - JSON string of array of input transaction(s) to use to create the spend.
******
******	Optional Field(s):
******
******		EXTRA_CALLID (String) - Service Provider can set a field that gets passed back when the result is done.
******			It contains the same value when the intent call was created.
******			To be used to distinguish results when many results are coming back at the same time.
******
******	EXTRA_TRANS JSON String Format:
******
******		The JSON contains an array of input transactions in a variable called "unspent".
******		The transaction record contains the following fields:
******			n = integer (index of transaction id)
******			amount = amount available for this transaction id.
******			tx = transaction id hash
******			script = redemption script for transaction
******			confirmations = (Optional) number of confirmations old this transaction input is.
******
******		Example JSON of inputs to use:
******		{	"unspent":
******			[
******				{	"n":1,
******					"amount":"0.00250000",
******					"tx":"a8107a65faa2846c756d99de6540f9023fde914c7e9dc7b0ab4ee190afbdddb8",
******					"script":"76a914a41675a2c62d3e44a7f1694a82ec9bba78921b8788ac",
******					"confirmations":1
******				},
******				{	"n":0,
******					"amount":"0.10000000",
******					"tx":"f7b4c70356c81b2d86f8d33533f62b3cd5f7614ba349058f5f9817932b04424f",
******					"script":"76a914a41675a2c62d3e44a7f1694a82ec9bba78921b8788ac",
******					"confirmations":1
******				}
******			]
******		}
******
******	Returns:
******
******		EXTRA_CALLID (String) - Optional string passed without change from calling intent.
******		EXTRA_SIGNED (String) - Signed Transaction Data to transmit to the virtual coin network.
******		EXTRA_SIGNDONE (Boolean) - True = Ready to transmit the data to the virtual coin network.
******		           False = More signing requied for Multi-Sig Only
******		EXTRA_RESULTCODE (int) - If values are blank then ERROR will contain why it was not able to sign.
******
********************************************************************
********************************************************************
*******************************************************************/

		Intent intent = new Intent(Rivet.RIVET_INTENT)
			.putExtra(Rivet.EXTRA_INSTRUCT, Rivet.INSTRUCT_SIGNTXN)
			.putExtra(Rivet.EXTRA_SPID, SP_UUID)
			.putExtra(Rivet.EXTRA_KEYNAME,"VC_"+vc)
			.putExtra(Rivet.EXTRA_CALLID, vc)
			.putExtra(Rivet.EXTRA_VC, vc)
			.putExtra(Rivet.EXTRA_TOPUB, TOPUB)
			.putExtra(Rivet.EXTRA_AMT, AMT)
			.putExtra(Rivet.EXTRA_FEE, FEE)
			.putExtra(Rivet.EXTRA_TRANS, TRANS);
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(intent,Rivet.INSTRUCT_SIGNTXN);
		}
	}
	protected void resultSendCoinRivet(int resultCode, Intent data) {
		String ErrorDesc = "Sign Transaction Error: ";
		int RivetResultCode = 0; // data.getIntExtra(Rivet.EXTRA_RESULTCODE,-1);
		if (RivetResultCode != 0) {
			ToastIt(ErrorDesc+"RivetResultCode = "+String.valueOf(RivetResultCode));
			return;
		}
		if (resultCode != RESULT_OK) {
			ToastIt(ErrorDesc+"ResultCode = "+String.valueOf(resultCode));
			return;
		}
		String CallId = data.getStringExtra(Rivet.EXTRA_CALLID);
		String vc = CallId;
		if (!IsValidVC(vc)) {
			ToastIt(ErrorDesc+"Virtual Coin invalid: "+vc);
			return;
		}
		String SignedTrans = data.getStringExtra(Rivet.EXTRA_SIGNED);
		if (SignedTrans == null) {
			ToastIt(ErrorDesc+"SignedTrans is null");
			VCArray[VCIndex(vc)].spending = new ArrayList<String>();
			return;
		}
		if (SignedTrans.equals("")) {
			// ToastIt(ErrorDesc+"SignedTrans cancelled.");
			ToastIt("Transaction cancelled.");
			VCArray[VCIndex(vc)].spending = new ArrayList<String>();
			return;
		}
		boolean SignedDone = data.getBooleanExtra(Rivet.EXTRA_SIGNDONE,false);
		if (!SignedDone) {
			ToastIt(ErrorDesc+"Multi-sig signing not supported in RosieWallet yet.");
			VCArray[VCIndex(vc)].spending = new ArrayList<String>();
			return;
		}
		ToastIt("Sending signed transaction");
		String urlstr = "http://rosieswallet.com/api-coin-v1/sendtrans.php?coin="+ vc +
				"&trans=" + SignedTrans;
		Intent intentd = new Intent(this, WebGet.class)
			.putExtra(WebGet.CALLID,vc)
			.putExtra(WebGet.TYPE,"send")
			.putExtra(WebGet.URL, urlstr);
		startService(intentd);
	}
	public void CreateWallet(String vc) {
		if (!IsValidVC(vc)) return;
		VCArray[VCIndex(vc)].Loaded=false;

/*******************************************************************
********************************************************************
********************************************************************
******
******	INTENT Example: Rivet.INSTRUCT_CREATEKEY
******
******	Description:
******
******		Create an ECDSA Private and Public Key pair.
******		Returns ECDSA private and public keys.
******
******	Required Field(s):
******
******		EXTRA_SPID (String / UUID 32) - Pass your assigned Service Provider ID.
******		EXTRA_KEYTYPE (String) - ECDSA Curve to use see Rivet.Java for ECC Curves
******
******	Optional Field(s):
******
******		EXTRA_KEYNAME (String) - Keyname RivetAndroid uses to store your private key to be used to sign with.
******			If this is not set then a EXTRA_KEYRECORD will be returned instead and the service provider will
******			have to maintain the key (store it, back it up)
******		EXTRA_CALLID (String) - Service Provider can set a field that gets passed back when the result is done.
******			It contains the same value when the intent call was created.
******			To be used to distinguish results when many results are coming back at the same time.
******
******
******	Returns:
******
******		EXTRA_CALLID (String) - Optional string passed without change from calling intent.
******		EXTRA_KEYNAME (String Max 32) - If this value was not set by the service provider
******			then it returns a generate keyname.
******		EXTRA_RESULTCODE (int) - If values are blank then ERROR will contain why it was not able to sign.
******
********************************************************************
********************************************************************
*******************************************************************/

		Intent intent = new Intent(Rivet.RIVET_INTENT)
			.putExtra(Rivet.EXTRA_INSTRUCT, Rivet.INSTRUCT_CREATEKEY)
			.putExtra(Rivet.EXTRA_SPID, SP_UUID)
			.putExtra(Rivet.EXTRA_CALLID, vc)
			.putExtra(Rivet.EXTRA_KEYNAME,"VC_"+vc)
			.putExtra(Rivet.EXTRA_KEYTYPE, Rivet.KEYTYPE_ECDSA_SECP256K1);
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(intent,Rivet.INSTRUCT_CREATEKEY);
		}
	}
	public void resultECDSACreate(int resultCode, Intent data) {
		String ErrorDesc = "ECDSA Create Error: ";
		int RivetResultCode = 0; // data.getIntExtra(Rivet.EXTRA_RESULTCODE,-1);
		if (RivetResultCode != 0) {
			ToastIt(ErrorDesc+"RivetResultCode = "+String.valueOf(RivetResultCode));
			return;
		}
		if (resultCode != RESULT_OK) {
			ToastIt(ErrorDesc+"ResultCode = "+String.valueOf(resultCode));
			return;
		}
		String CallId = data.getStringExtra(Rivet.EXTRA_CALLID);
		String vc = CallId;
		if (!IsValidVC(vc)) {
			ToastIt(ErrorDesc+"Virtual Coin invalid: "+vc);
			return;
		}
		String PUBLICDATA = data.getStringExtra(Rivet.EXTRA_PUBLICDATA);
		if (PUBLICDATA == null) {
			ToastIt(ErrorDesc+"Public Key is null");
			return;
		}
		if (PUBLICDATA.equals("")) {
			ToastIt(ErrorDesc+"Public Key is blank");
			return;
		}
		VCArray[VCIndex(vc)].PublicKey = PUBLICDATA;
		VCArray[VCIndex(vc)].PublicAddress = "Loading...";
		VCArray[VCIndex(vc)].Signature = "";
		VCArray[VCIndex(vc)].Encrypted = "";
		VCArray[VCIndex(vc)].ToEncrypt = "";
		VCArray[VCIndex(vc)].Decrypted = "";
		VCArray[VCIndex(vc)].ToDecrypt = "";
		VCArray[VCIndex(vc)].GotBalance = false;
		VCArray[VCIndex(vc)].GotBalanceUC = false;
		VCArray[VCIndex(vc)].GotBalanceList = false;
		VCArray[VCIndex(vc)].Balance0Confirm = 0;
		VCArray[VCIndex(vc)].Balance1Confirm = 0;
		VCArray[VCIndex(vc)].Value = 0;
		VCArray[VCIndex(vc)].fee = LoadFee(vc);
		VCArray[VCIndex(vc)].Loaded = false;
		GetPublicAddressFromKeyName(vc);
	}
	private void GetPublicKeyFromPrivate(String vc) {
		if (!IsValidVC(vc)) return;
		if (!MovingWallet) {
			MovingWallet = true;
			ToastIt("Moving Wallet to Rivet");
		}
		VCArray[VCIndex(vc)].PublicKey = "";
		AddKey(vc);
	}
	private void GetPublicAddressFromKeyName(String vc) {
		if (!IsValidVC(vc)) return;
		Intent intent = new Intent(Rivet.RIVET_INTENT)
			.putExtra(Rivet.EXTRA_INSTRUCT, Rivet.INSTRUCT_GETPUBPRV)
			.putExtra(Rivet.EXTRA_SPID, SP_UUID)
			.putExtra(Rivet.EXTRA_CALLID,vc)
			.putExtra(Rivet.EXTRA_VC,vc)
			.putExtra(Rivet.EXTRA_KEYNAME,"VC_"+vc);
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(intent,Rivet.INSTRUCT_GETPUBPRV);
		}
	}
	private void GetKey(String vc) {
		if (!IsValidVC(vc)) return;
		Intent intent = new Intent(Rivet.RIVET_INTENT)
			.putExtra(Rivet.EXTRA_INSTRUCT, Rivet.INSTRUCT_GETKEY)
			.putExtra(Rivet.EXTRA_SPID, SP_UUID)
			.putExtra(Rivet.EXTRA_KEYNAME,"VC_"+vc)
			.putExtra(Rivet.EXTRA_CALLID,vc);
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(intent,Rivet.INSTRUCT_GETKEY);
		}
	}
	private void AddKey(String vc) {
		if (!IsValidVC(vc)) return;
		Intent intent = new Intent(Rivet.RIVET_INTENT)
			.putExtra(Rivet.EXTRA_INSTRUCT, Rivet.REQUEST_ADDKEY)
			.putExtra(Rivet.EXTRA_SPID, SP_UUID)
			.putExtra(Rivet.EXTRA_KEYNAME,"VC_"+vc)
			.putExtra(Rivet.EXTRA_PUBLICDATA,VCArray[VCIndex(vc)].PublicKey)
			.putExtra(Rivet.EXTRA_SECUREDATA,VCArray[VCIndex(vc)].OldPrivateKey);
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(intent,Rivet.REQUEST_ADDKEY);
		}
	}
	public void DeleteWallet(String vc) {
		if (!IsValidVC(vc)) return;
		VCArray[VCIndex(vc)].Loaded=false;
		Intent intent = new Intent(Rivet.RIVET_INTENT)
			.putExtra(Rivet.EXTRA_INSTRUCT, Rivet.INSTRUCT_DELETEKEY)
			.putExtra(Rivet.EXTRA_SPID, SP_UUID)
			.putExtra(Rivet.EXTRA_KEYNAME,"VC_"+vc);
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(intent,Rivet.INSTRUCT_DELETEKEY);
		}
	}
	public void signmessage(String vc,String Message) {
		if (!IsValidVC(vc)) return;
		if (Message == null) return;
		if (Message.equals("")) return;

/*******************************************************************
********************************************************************
********************************************************************
******
******	INTENT Example: Rivet.INSTRUCT_SIGN
******
******	Description:
******
******		Sign data using an ECDSA Private Key
******		Returns signature
******
******	Required Field(s):
******
******		EXTRA_SPID (String / UUID 32) - Pass your assigned Service Provider ID.
******		EXTRA_KEYNAME (String) - Keyname RivetAndroid uses to store your private key to be used to sign with.
******		EXTRA_BYTEDATA (Byte[]) - Data as a raw byte array to be signed
******		  or
******		EXTRA_MESSAGE (String) - Data as a hex string to be signed
******
******	Optional Field(s):
******
******		EXTRA_CALLID (String) - Service Provider can set a field that gets passed back when the result is done.
******			It contains the same value when the intent call was created.
******			To be used to distinguish results when many results are coming back at the same time.
******
******
******	Returns:
******
******		EXTRA_CALLID (String) - Optional string passed without change from calling intent.
******		EXTRA_SIGNATURE (String) - Signature Data in hex string
******		EXTRA_RESULTCODE (int) - If values are blank then ERROR will contain why it was not able to sign.
******
********************************************************************
********************************************************************
*******************************************************************/

		Intent intent = new Intent(Rivet.RIVET_INTENT)
			.putExtra(Rivet.EXTRA_INSTRUCT, Rivet.INSTRUCT_SIGN)
			.putExtra(Rivet.EXTRA_SPID, SP_UUID)
			.putExtra(Rivet.EXTRA_KEYNAME,"VC_"+vc)
			.putExtra(Rivet.EXTRA_CALLID, vc)
			.putExtra(Rivet.EXTRA_MESSAGE, Message);
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(intent,Rivet.INSTRUCT_SIGN);
		}
	}
	public void resultsignmessage(int resultCode, Intent data) {
		String ErrorDesc = "ECDSA Sign Error: ";
		int RivetResultCode = 0; // data.getIntExtra(Rivet.EXTRA_RESULTCODE,-1);
		if (RivetResultCode != 0) {
			ToastIt(ErrorDesc+"RivetResultCode = "+String.valueOf(RivetResultCode));
			return;
		}
		if (resultCode != RESULT_OK) {
			ToastIt(ErrorDesc+"ResultCode = "+String.valueOf(resultCode));
			return;
		}
		String CallId = data.getStringExtra(Rivet.EXTRA_CALLID);
		String vc = CallId;
		if (!IsValidVC(vc)) {
			ToastIt(ErrorDesc+"Virtual Coin invalid: "+vc);
			return;
		}
		String SIG = data.getStringExtra(Rivet.EXTRA_SIGNATURE);
		if (SIG == null) {
			ToastIt(ErrorDesc+"Signature is null");
			return;
		}
		if (SIG.equals("")) {
			ToastIt(ErrorDesc+"Signature is blank");
			return;
		}
		VCArray[VCIndex(vc)].Signature = SIG;
		ToastIt("Message Signed");
	}
	public void verifymessage(String vc, String PUB, String SIG, String Message) {
		if (!IsValidVC(vc)) return;
		if (PUB == null) return;
		if (PUB.equals("")) return;
		if (SIG == null) return;
		if (SIG.equals("")) return;
		if (Message == null) return;
		if (Message.equals("")) return;

/*******************************************************************
********************************************************************
********************************************************************
******
******	INTENT Example: Rivet.INSTRUCT_VERIFY
******
******	Description:
******
******		Verify an ECDSA Signature against data using a public key
******		Returns if the signature verified or failed.
******
******	Required Field(s):
******
******		EXTRA_SPID (String / UUID 32) - Pass your assigned Service Provider ID.
******		EXTRA_KEYNAME (String) - Keyname RivetAndroid uses to store your private key to be used to verify with
******		EXTRA_PUB (String) - Public Key to verify against
******		EXTRA_SIGNATURE (String) - Signature Data in hex string
******		EXTRA_BYTEDATA (Byte[]) - Data as a raw byte array to be verified
******		  or
******		EXTRA_MESSAGE (String) - Data as a hex string to be verified
******
******	Optional Field(s):
******
******		EXTRA_CALLID (String) - Service Provider can set a field that gets passed back when the result is done.
******			It contains the same value when the intent call was created.
******			To be used to distinguish results when many results are coming back at the same time.
******
******
******	Returns:
******
******		EXTRA_CALLID (String) - Optional string passed without change from calling intent.
******		EXTRA_SIGNATURE (String) - Signature Data in hex string
******		EXTRA_RESULTCODE (int) - If values are blank then ERROR will contain why it was not able to sign.
******
********************************************************************
********************************************************************
*******************************************************************/

		Intent intent = new Intent(Rivet.RIVET_INTENT)
			.putExtra(Rivet.EXTRA_INSTRUCT, Rivet.INSTRUCT_VERIFY)
			.putExtra(Rivet.EXTRA_SPID, SP_UUID)
			.putExtra(Rivet.EXTRA_CALLID, vc)
			.putExtra(Rivet.EXTRA_PUB, PUB)
			.putExtra(Rivet.EXTRA_SIGNATURE, SIG)
			.putExtra(Rivet.EXTRA_MESSAGE, Message);
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(intent,Rivet.INSTRUCT_VERIFY);
		}
	}
	public void encryptmessage(String vc, String TOPUB, String Message) {
		// First we will create a shared KEY
		// Then we encrypte the message using shared key
		if (!IsValidVC(vc)) return;
		if (TOPUB == null) return;
		if (TOPUB.equals("")) return;
		if (Message == null) return;
		if (Message.equals("")) return;
		VCArray[VCIndex(vc)].ToEncrypt = Message; // Save For Later
		Intent intent = new Intent(Rivet.RIVET_INTENT)
			.putExtra(Rivet.EXTRA_INSTRUCT, Rivet.REQUEST_ECDH_SHARED)
			.putExtra(Rivet.EXTRA_SPID, SP_UUID) 
			.putExtra(Rivet.EXTRA_CALLID, vc)
			.putExtra(Rivet.EXTRA_KEYNAME,"VC_"+vc)
			.putExtra(Rivet.EXTRA_TOPUB, TOPUB);
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(intent,Rivet.REQUEST_ECDH_SHARED);
		}
	}
	public void decryptmessage(String vc, String TOPUB, String Message) {
		// First we will create a shared KEY
		// Then we encrypte the message using shared key
		if (!IsValidVC(vc)) return;
		if (TOPUB == null) return;
		if (TOPUB.equals("")) return;
		if (Message == null) return;
		if (Message.equals("")) return;
		VCArray[VCIndex(vc)].ToDecrypt = Message; // Save For Later
		Intent intent = new Intent(Rivet.RIVET_INTENT)
			.putExtra(Rivet.EXTRA_INSTRUCT, Rivet.REQUEST_ECDH_SHARED)
			.putExtra(Rivet.EXTRA_SPID, SP_UUID) 
			.putExtra(Rivet.EXTRA_CALLID, vc)
			.putExtra(Rivet.EXTRA_KEYNAME,"VC_"+vc)
			.putExtra(Rivet.EXTRA_TOPUB, TOPUB);
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(intent,Rivet.REQUEST_ECDH_SHARED);
		}
	}
	protected void resultECDHShared(int resultCode, Intent data) {
		String ErrorDesc = "ECDH Shared Key Error: ";
		int RivetResultCode = 0; // data.getIntExtra(Rivet.EXTRA_RESULTCODE,-1);
		if (RivetResultCode != 0) {
			ToastIt(ErrorDesc+"RivetResultCode = "+String.valueOf(RivetResultCode));
			return;
		}
		if (resultCode != RESULT_OK) {
			ToastIt(ErrorDesc+"ResultCode = "+String.valueOf(resultCode));
			return;
		}
		String CallId = data.getStringExtra(Rivet.EXTRA_CALLID);
		String vc = CallId;
		if (!IsValidVC(vc)) {
			ToastIt(ErrorDesc+"Virtual Coin invalid: "+vc);
			return;
		}
		String SHARED = data.getStringExtra(Rivet.EXTRA_SHAREDKEY);
		if (SHARED == null) {
			ToastIt(ErrorDesc+"SHARED is null");
			return;
		}
		if (SHARED.equals("")) {
			ToastIt(ErrorDesc+"SHARED is blank");
			return;
		}
		/*Intent intent = new Intent(Rivet.RIVET_INTENT)
			.putExtra(Rivet.EXTRA_INSTRUCT, Rivet.REQUEST_HASH)
			.putExtra(Rivet.EXTRA_SPID, SP_UUID)
			.putExtra(Rivet.EXTRA_CALLID, vc)
			.putExtra(Rivet.EXTRA_HASH_ALGO, Rivet.HASH_SHA256)
			.putExtra(Rivet.EXTRA_MESSAGE, SHARED);
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(intent,Rivet.REQUEST_HASH);
		}*/
		boolean didIntent = false;
		String Message = VCArray[VCIndex(vc)].ToEncrypt;
		if (Message != null) {
			if (Message.equals("") == false) {
				Intent intent = new Intent(Rivet.RIVET_INTENT)
					.putExtra(Rivet.EXTRA_INSTRUCT, Rivet.REQUEST_AES_ENCRYPT)
					.putExtra(Rivet.EXTRA_SPID, SP_UUID) 
					.putExtra(Rivet.EXTRA_CALLID, vc)
					.putExtra(Rivet.EXTRA_KEY, SHARED)
					.putExtra(Rivet.EXTRA_MESSAGE, Message);
				if (intent.resolveActivity(getPackageManager()) != null) {
					startActivityForResult(intent,Rivet.REQUEST_AES_ENCRYPT);
				}
				didIntent = true;
			}
		}
		if (didIntent == false) {
			Message = VCArray[VCIndex(vc)].ToDecrypt;
			if (Message != null) {
				if (Message.equals("") == false) {
					Intent intent = new Intent(Rivet.RIVET_INTENT)
						.putExtra(Rivet.EXTRA_INSTRUCT, Rivet.REQUEST_AES_DECRYPT)
						.putExtra(Rivet.EXTRA_SPID, SP_UUID) 
						.putExtra(Rivet.EXTRA_CALLID, vc)
						.putExtra(Rivet.EXTRA_KEY, SHARED)
						.putExtra(Rivet.EXTRA_MESSAGE, Message);
					if (intent.resolveActivity(getPackageManager()) != null) {
						startActivityForResult(intent,Rivet.REQUEST_AES_DECRYPT);
					}
					didIntent = true;
				}
			}
		}
		if (didIntent == false) ToastIt("Encrypt/Decrypt Failed Message Blank");
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Rivet.INSTRUCT_GETPOINTER)
			resultGetPointer(resultCode, data);
		if (requestCode == Rivet.INSTRUCT_CREATEKEY && resultCode == RESULT_OK) { // Create Wallet
			resultECDSACreate(resultCode, data);
		}
		else if (requestCode == Rivet.INSTRUCT_SIGN && resultCode == RESULT_OK) { // Sign Message
			resultsignmessage(resultCode, data);
		}
		else if (requestCode == Rivet.INSTRUCT_VERIFY && resultCode == RESULT_OK) { // Verify Message
			String CallId = data.getStringExtra(Rivet.EXTRA_CALLID);
			String vc = CallId;
			String ErrorDesc = "ECDSA Verify Error: ";
			if (IsValidVC(vc)) {
				int ERROR = 0; // data.getIntExtra(Rivet.EXTRA_RESULTCODE,-1);
				if (ERROR == 0) {
					boolean VERIFIED = data.getBooleanExtra(Rivet.EXTRA_VERIFIED,false); // Always default to false
					if (VERIFIED)
						ToastIt("Signature Verified");
					else
						ToastIt("Signature was not verified.");
				}
				else ToastIt(ErrorDesc+String.valueOf(ERROR));
			}
			else ToastIt(ErrorDesc+"Virtual Coin invalid: "+vc);
		}
		else if (requestCode == Rivet.REQUEST_ECDH_SHARED && resultCode == RESULT_OK) { // SharedKey/Agreement
			resultECDHShared(resultCode, data);
		}
		else if (requestCode == Rivet.REQUEST_HASH && resultCode == RESULT_OK) { // HASH Result
			String vc = data.getStringExtra(Rivet.EXTRA_CALLID);
			String HASH = data.getStringExtra(Rivet.EXTRA_MESSAGE);
			if (HASH.equals("") == false ) {
				boolean didIntent = false;
				String Message = VCArray[VCIndex(vc)].ToEncrypt;
				if (Message != null) {
					if (Message.equals("") == false) {
						Intent intent = new Intent(Rivet.RIVET_INTENT)
							.putExtra(Rivet.EXTRA_INSTRUCT, Rivet.REQUEST_AES_ENCRYPT)
							.putExtra(Rivet.EXTRA_SPID, SP_UUID) 
							.putExtra(Rivet.EXTRA_CALLID, vc)
							.putExtra(Rivet.EXTRA_KEY, HASH)
							.putExtra(Rivet.EXTRA_MESSAGE, Message);
						if (intent.resolveActivity(getPackageManager()) != null) {
							startActivityForResult(intent,Rivet.REQUEST_AES_ENCRYPT);
						}
						didIntent = true;
					}
				}
				if (didIntent == false) {
					Message = VCArray[VCIndex(vc)].ToDecrypt;
					if (Message != null) {
						if (Message.equals("") == false) {
							Intent intent = new Intent(Rivet.RIVET_INTENT)
								.putExtra(Rivet.EXTRA_INSTRUCT, Rivet.REQUEST_AES_DECRYPT)
								.putExtra(Rivet.EXTRA_SPID, SP_UUID) 
								.putExtra(Rivet.EXTRA_CALLID, vc)
								.putExtra(Rivet.EXTRA_KEY, HASH)
								.putExtra(Rivet.EXTRA_MESSAGE, Message);
							if (intent.resolveActivity(getPackageManager()) != null) {
								startActivityForResult(intent,Rivet.REQUEST_AES_DECRYPT);
							}
							didIntent = true;
						}
					}
				}
				if (didIntent == false) ToastIt("Encrypt/Decrypt Failed Message Blank");
			}
			else ToastIt("Encrypt/Decrypt Failed while creating Hash. Hash Blank");
		}
		else if (requestCode == Rivet.REQUEST_AES_ENCRYPT && resultCode == RESULT_OK) { // AES Encrypt
			String vc = data.getStringExtra(Rivet.EXTRA_VC); // CALLID);
			String Encrypted = data.getStringExtra(Rivet.EXTRA_MESSAGE);
			if (Encrypted.equals("") == false ) {
				ToastIt("Message Encrypted");
				VCArray[VCIndex(vc)].Encrypted = Encrypted;
				VCArray[VCIndex(vc)].ToEncrypt = "";
			}
			else ToastIt("Encrypt Failed Returned Blank");
		}
		else if (requestCode == Rivet.REQUEST_AES_DECRYPT && resultCode == RESULT_OK) { // AES Encrypt
			String vc = data.getStringExtra(Rivet.EXTRA_VC); // CALLID);
			String Decrypted = data.getStringExtra(Rivet.EXTRA_MESSAGE);
			if (Decrypted.equals("") == false ) {
				ToastIt("Message Decrypted");
				VCArray[VCIndex(vc)].Decrypted = Decrypted;
				VCArray[VCIndex(vc)].ToDecrypt = "";
			}
			else ToastIt("Decrypt Failed Returned Blank");
		}
		else if (requestCode == Rivet.INSTRUCT_GETKEY && resultCode == RESULT_OK) { // Got Wallet
			String vc = data.getStringExtra(Rivet.EXTRA_CALLID);
			String PUBLICDATA = data.getStringExtra(Rivet.EXTRA_PUBLICDATA);
			String ErrorDesc = "Get Key: ";
			if (vc != null && PUBLICDATA != null) {
				int ERROR = 0; // data.getIntExtra(Rivet.EXTRA_RESULTCODE,-1);
				if (ERROR == 0) {
					if (!PUBLICDATA.equals("")) {
						VCArray[VCIndex(vc)].PublicKey = PUBLICDATA;
						VCArray[VCIndex(vc)].PublicAddress = "Loading...";
						VCArray[VCIndex(vc)].Signature = "";
						VCArray[VCIndex(vc)].Encrypted = "";
						VCArray[VCIndex(vc)].ToEncrypt = "";
						VCArray[VCIndex(vc)].Decrypted = "";
						VCArray[VCIndex(vc)].ToDecrypt = "";
						VCArray[VCIndex(vc)].GotBalance = false;
						VCArray[VCIndex(vc)].GotBalanceUC = false;
						VCArray[VCIndex(vc)].GotBalanceList = false;
						VCArray[VCIndex(vc)].Balance0Confirm = 0;
						VCArray[VCIndex(vc)].Balance1Confirm = 0;
						VCArray[VCIndex(vc)].Value = 0;
						VCArray[VCIndex(vc)].fee = LoadFee(vc);
						// ToastIt("Got Public Key now getting address "+vc);
						GetPublicAddressFromKeyName(vc);
					}
					else ToastIt(ErrorDesc+"PublicData is blank");
				}
				else ToastIt(ErrorDesc+ERROR);
			}
			// else ToastIt(ErrorDesc+"returned null values"); can occur if keys are not established yet
		}
		else if (requestCode == Rivet.INSTRUCT_GETPUBPRV && resultCode == RESULT_OK) { // VC Address from Private Key
			String CallId = data.getStringExtra(Rivet.EXTRA_CALLID);
			String vc = CallId;
			String ErrorDesc = "ECDSA Get Pub From Prv Error: ";
			if (IsValidVC(vc)) {
				int ERROR = 0; // data.getIntExtra(Rivet.EXTRA_RESULTCODE,-1);
				if (ERROR == 0) {
					String VCADDRESS = data.getStringExtra(Rivet.EXTRA_VC_PUBADDR);
					if (VCADDRESS.equals("") == false) {
						VCArray[VCIndex(vc)].PublicAddress = VCADDRESS;
						VCArray[VCIndex(vc)].Loaded = true;
						if (VCStartWith.equals(vc)) LoadWebpage("main.html");
					}
					else ToastIt(ErrorDesc+"Public Key Data blank");
				}
				else ToastIt(ErrorDesc+String.valueOf(ERROR));
			}
			else ToastIt(ErrorDesc+"Virtual Coin invalid: "+vc);	
		}
		else if (requestCode == Rivet.REQUEST_VC_GETPUBPRV && resultCode == RESULT_OK) { // Got PublicKey From Private
			String vc = data.getStringExtra(Rivet.EXTRA_CALLID);
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
		else if (requestCode == Rivet.INSTRUCT_SIGNTXN)
			resultSendCoinRivet(resultCode, data);
		else if (requestCode == Rivet.INSTRUCT_DELETEKEY && resultCode == RESULT_OK) { // Got Delete Key
			ToastIt("Wallet Deleted");
		}
		else if (requestCode == 0) {
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
			String result = intent.getStringExtra(WebGet.RESULT);
			String confirms = intent.getStringExtra(WebGet.CONFIRMS);
			String vc = intent.getStringExtra(WebGet.CALLID);
			String type = intent.getStringExtra(WebGet.TYPE);
			// ToastIt("Received: "+result);
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
			else if (type.equals("value")) {
				try {
					String valStr = "";
					JSONObject jObj1 = new JSONObject(result);
					JSONArray jObj2 = jObj1.getJSONArray("data");
					JSONObject jObj3 = (JSONObject)jObj2.get(0);
					JSONObject jObj4 = jObj3.getJSONObject("rates");
					String vcStr = vc;
					if (vcStr.equals("TBTC")) vcStr = "BTC";
					valStr = jObj4.getString(vcStr);
					VCArray[VCIndex(vc)].Value = Double.parseDouble(valStr);
				} catch(Exception e) {
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
	public boolean IsValidVC(String vc) {
		if (vc == null) return false;
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
		String basehref = "file:///android_asset/";
		if (IsArndale()) basehref = "fake://not/needed";
		webView.loadDataWithBaseURL(basehref,html,"text/html", "UTF-8",null);
	}
	private String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
	public void GetBalanceStart(String vc) {
		if (!IsValidVC(vc)) return;
		if (!VCArray[VCIndex(vc)].Loaded) return;
		GetUnspentStart(vc);
		GetValueStart(vc);
	}
	public void GetValueStart(String vc) {
		if (!IsValidVC(vc)) return;
		if (!VCArray[VCIndex(vc)].Loaded) return;
		String urlstr = "http://rosieswallet.com/api-coin-v1/price.php?coin="+ vc;
		Intent intentd = new Intent(this, WebGet.class)
			.putExtra(WebGet.CALLID, vc)
			.putExtra(WebGet.TYPE, "value")
			.putExtra(WebGet.URL, urlstr);
		startService(intentd);
	}
	public void GetUnspentStart(String vc) {
		if (!IsValidVC(vc)) return;
		if (!VCArray[VCIndex(vc)].Loaded) return;
		String urlstr = "http://rosieswallet.com/api-coin-v1/unspent.php?coin="+ vc +
				"&address=" + VCArray[VCIndex(vc)].PublicAddress;
		Intent intentd = new Intent(this, WebGet.class)
			.putExtra(WebGet.CALLID, vc)
			.putExtra(WebGet.TYPE, "unspent")
			.putExtra(WebGet.URL, urlstr);
		startService(intentd);
	}
	public String GetBalanceValue(String vc) {
		if (!IsValidVC(vc)) return "";
		if (!VCArray[VCIndex(vc)].Loaded) return "";
		if (VCArray[VCIndex(vc)].Value == 0) return "";
		if (VCArray[VCIndex(vc)].GotBalance) {
			double USD = VCArray[VCIndex(vc)].Balance1Confirm * ( 1 / VCArray[VCIndex(vc)].Value );
			DecimalFormat df = new DecimalFormat("0.00");
			String ReturnStr = df.format(USD);
			return ReturnStr;
		}
		return "";
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
	public String GetPublicKey(String vc) {
		if (!IsValidVC(vc)) return "";
		if (!VCArray[VCIndex(vc)].Loaded) return "";
		if (VCArray[VCIndex(vc)].PublicKey == null) return "";
		return VCArray[VCIndex(vc)].PublicKey;
	}
	public String GetSignature(String vc) {
		if (!IsValidVC(vc)) return "";
		if (!VCArray[VCIndex(vc)].Loaded) return "";
		if (VCArray[VCIndex(vc)].Signature == null) return "";
		return VCArray[VCIndex(vc)].Signature;
	}
	public String GetEncrypted(String vc) {
		if (!IsValidVC(vc)) return "";
		if (!VCArray[VCIndex(vc)].Loaded) return "";
		if (VCArray[VCIndex(vc)].Encrypted == null) return "";
		return VCArray[VCIndex(vc)].Encrypted;
	}
	public String GetDecrypted(String vc) {
		if (!IsValidVC(vc)) return "";
		if (!VCArray[VCIndex(vc)].Loaded) return "";
		if (VCArray[VCIndex(vc)].Decrypted == null) return "";
		return VCArray[VCIndex(vc)].Decrypted;
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
		VCArray[VCIndex(vc)].Signature = "";
		VCArray[VCIndex(vc)].Encrypted = "";
		VCArray[VCIndex(vc)].ToEncrypt = "";
		VCArray[VCIndex(vc)].Decrypted = "";
		VCArray[VCIndex(vc)].ToDecrypt = "";
		VCArray[VCIndex(vc)].OldPrivateKey = PrivateKey;
		VCArray[VCIndex(vc)].PublicKey = "";
		VCArray[VCIndex(vc)].GotBalance = false;
		VCArray[VCIndex(vc)].GotBalanceUC = false;
		VCArray[VCIndex(vc)].GotBalanceList = false;
		VCArray[VCIndex(vc)].Balance0Confirm = 0;
		VCArray[VCIndex(vc)].Balance1Confirm = 0;
		VCArray[VCIndex(vc)].Value = 0;
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
		if (LocalWalletLoad("TBTC")) GetPublicKeyFromPrivate("TBTC");
		if (LocalWalletLoad("BTC"))  GetPublicKeyFromPrivate("BTC");
		if (LocalWalletLoad("LTC"))  GetPublicKeyFromPrivate("LTC");
		if (LocalWalletLoad("PPC"))  GetPublicKeyFromPrivate("PPC");
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
		if (scanResult == null) return "";
		if (scanResult.equals("") == false) {
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
	private class VirtualCoin {
		public String PublicAddress = null;
		public String PublicKey = null;
		public String OldPrivateKey = null;
		public String Signature = null;
		public String Encrypted = null;
		public String ToEncrypt = null;
		public String Decrypted = null;
		public String ToDecrypt = null;
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
		public double Value = 0;
	}
	private boolean IsArndale() {
		if (android.os.Build.HARDWARE.equals("arndale")) return true;
		return false;
	}
	@Override
	protected void onResume() {
		super.onResume();
		/**
		* It's important, that the activity is in the foreground (resumed). Otherwise
		* an IllegalStateException is thrown.
		*/
		setupForegroundDispatch(this, mNfcAdapter);
	}
     
	@Override
	protected void onPause() {
		/**
		* Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
		*/
		stopForegroundDispatch(this, mNfcAdapter);
		super.onPause();
	}
     
	@Override
	protected void onNewIntent(Intent intent) {
		/**
		* This method gets called, when a new Intent gets associated with the current activity instance.
		* Instead of creating a new activity, onNewIntent will be called. For more information have a look
		* at the documentation.
		*
		* In our case this method gets called, when the user attaches a Tag to the device.
		*/
		if (mNfcAdapter != null) handleIntent(intent);
	}
     
	/**
	* @param activity The corresponding {@link Activity} requesting the foreground dispatch.
	* @param adapter The {@link NfcAdapter} used for the foreground dispatch.
	*/
	public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
		final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

		IntentFilter[] filters = new IntentFilter[1];
		String[][] techList = new String[][]{};

		// Notice that this is the same filter as in our manifest.
		filters[0] = new IntentFilter();
		filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
		filters[0].addCategory(Intent.CATEGORY_DEFAULT);
		try {
			filters[0].addDataType(MIME_TEXT_PLAIN);
		} catch (MalformedMimeTypeException e) {
			throw new RuntimeException("Check your mime type.");
		}
		adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
	}
	
	/**
	* @param activity The corresponding {@link BaseActivity} requesting to stop the foreground dispatch.
	* @param adapter The {@link NfcAdapter} used for the foreground dispatch.
	*/
	public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
		adapter.disableForegroundDispatch(activity);
	}
	private void handleIntent(Intent intent) {
		String action = intent.getAction();
		if (mNfcAdapter == null) return;
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			Ndef ndefTag = Ndef.get(tagFromIntent);
			NdefMessage ndefMesg = ndefTag.getCachedNdefMessage();
			byte[] payload = ndefMesg.toByteArray();
			Log.d(TAG, "NFCMessage: " + ndefMesg.toString());
			int headerLength = 5;
			String message = null;
			try {
				message = new String(payload, headerLength , payload.length - headerLength - 1, "UTF-8");
			}
			catch (UnsupportedEncodingException e) {}
			// ToastIt("Received Message: " + message);
			Log.d(TAG, "MESSAGE: " + message);
			// D/RivetzNFC( 2946): Message: 
			// NdefMessage [NdefRecord tnf=1 type=55 		
			// payload=
			// 00626974636F696E3A6D67356E5876554D4436657279636356586F37534B4439594A68384A
			// 6242683775553F616D6F756E743D302E30303432333326723D68747470733A2F2F74657374
			// 2E6269747061792E636F6D2F692F45694B704D4B32643256764B4A696341626D664B6534
			// ]
			// bitcoin:mpX6L3ZS8wEH6pYVeZ29nyVXxmBx8RGY2A?amount=0.004225&r=https://test.bitpay.com/i/YVNc43MbtCUSd9pAxx3HU
			if (message.startsWith("bitcoin:m") || message.startsWith("bitcoin:n")) {
				VCStartWith = new String("TBTC");
				NFCMessage = message;
				LoadWebpage("main.html");
			}
			else if (message.startsWith("bitcoin:")) {
				VCStartWith = new String("BTC");
				NFCMessage = message;
				LoadWebpage("main.html");
			}
			else if (message.startsWith("litecoin:")) {
				VCStartWith = new String("LTC");
				NFCMessage = message;
				LoadWebpage("main.html");
			}
			else if (message.startsWith("peercoin:")) {
				VCStartWith = new String("PPC");
				NFCMessage = message;
				LoadWebpage("main.html");
			}
		}
	}
	public String GetVCStartWith() {
		if (!IsValidVC(VCStartWith)) return "TBTC";
		return VCStartWith;
	}
	public String GetNFCMessage() {
		if (NFCMessage == null) return "";
		String retVal = NFCMessage;
		NFCMessage = "";
		return retVal;
	}
}
