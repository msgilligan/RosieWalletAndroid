package com.rosiewallet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.webkit.JavascriptInterface;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.ClipboardManager;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;

public class WebInterface {
	Context mContext;
	
	WebInterface(Context c) {
		mContext = c;
	}
	
	@JavascriptInterface
	public void bitcoinReadQRCode() {
		((MainActivity)mContext).ScanQRCode();
		return;
	}
	@JavascriptInterface
	public String bitcoinScanResult() {		
		return ((MainActivity)mContext).ScanResult();
	}
	@JavascriptInterface
	public void copyToClipBoard(String text) {
		if (text != null && !text.isEmpty()) {
			((MainActivity)mContext).CopyStringToClipboard(text);
		}
	}
	@JavascriptInterface
	public long bitcoinGetFee(String vc) {
		return ((MainActivity)mContext).LoadFee(vc);
	}
	@JavascriptInterface
	public void bitcoinSaveFee(String vc,long fee) {
		((MainActivity)mContext).SaveFee(vc,fee);
		return;
	}
	@JavascriptInterface
	public void bitcoinSendCoin(String vc, String Address, String Amount) {
		((MainActivity)mContext).sendcoin(vc,Address,Amount);
		return;
	}
	@JavascriptInterface
	public void bitcoinSignMessage(String vc, String Message) {
		((MainActivity)mContext).signmessage(vc,Message);
		return;
	}
	@JavascriptInterface
	public void bitcoinVerifyMessage(String vc, String PUB, String SIG, String Message) {
		((MainActivity)mContext).verifymessage(vc,PUB,SIG,Message);
		return;
	}
	@JavascriptInterface
	public void bitcoinEncryptMessage(String vc, String TOPUB, String Message) {
		((MainActivity)mContext).encryptmessage(vc,TOPUB,Message);
		return;
	}
	@JavascriptInterface
	public void bitcoinDecryptMessage(String vc, String FROMPUB, String Message) {
		((MainActivity)mContext).decryptmessage(vc,FROMPUB,Message);
		return;
	}
	@JavascriptInterface
	public String bitcoinShowAddress(String vc) {
		return ((MainActivity)mContext).GetPublicAddress(vc);
	}
	@JavascriptInterface
	public String bitcoinShowPublicKey(String vc) {
		return ((MainActivity)mContext).GetPublicKey(vc);
	}
	@JavascriptInterface
	public String bitcoinPrivateKey(String vc) {
		return ((MainActivity)mContext).GetPrivateKey(vc);
	}
	@JavascriptInterface
	public String bitcoinGetSignature(String vc) {
		return ((MainActivity)mContext).GetSignature(vc);
	}
	@JavascriptInterface
	public String bitcoinGetEncrypted(String vc) {
		return ((MainActivity)mContext).GetEncrypted(vc);
	}
	@JavascriptInterface
	public String bitcoinGetDecrypted(String vc) {
		return ((MainActivity)mContext).GetDecrypted(vc);
	}
	@JavascriptInterface
	public void bitcoinGetBalanceStart(String vc) {
		((MainActivity)mContext).GetBalanceStart(vc);
		return;
	}
	@JavascriptInterface
	public String bitcoinGetBalance(String vc) {
		return ((MainActivity)mContext).GetBalanceAddressNew(vc);
	}
	@JavascriptInterface
	public String GetBalanceValue(String vc) {
		return ((MainActivity)mContext).GetBalanceValue(vc);
	}
	@JavascriptInterface
	public String GetValueCoin(String vc) {
		return ((MainActivity)mContext).GetValueCoin(vc);
	}
	@JavascriptInterface
	public String bitcoinGetZeroBalance(String vc) {
		return ((MainActivity)mContext).GetUnconfirmedBalanceAddressNew(vc);
	}
	@JavascriptInterface
	public void bitcoinDeleteAccount(String vc) {
		((MainActivity)mContext).DeleteWallet(vc);
	}
	@JavascriptInterface
	public void bitcoinNewAccount(String vc) {
		((MainActivity)mContext).CreateWallet(vc);
	}
	@JavascriptInterface
	public String getAppVersion() {
		try {
			PackageManager pm = mContext.getPackageManager();
			String pn = mContext.getPackageName();
			PackageInfo pi = pm.getPackageInfo(pn,0);
			return pi.versionName;	
		} catch(PackageManager.NameNotFoundException e) {
			return "unknown";
		}
	}
	@JavascriptInterface
	public String GetVCStartWith() {
		return ((MainActivity)mContext).GetVCStartWith();
	}
	@JavascriptInterface
	public String GetNFCMessage() {
		return ((MainActivity)mContext).GetNFCMessage();
	}
}
