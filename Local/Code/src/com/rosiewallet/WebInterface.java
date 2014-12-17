package com.rosiewallet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.webkit.JavascriptInterface;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.ClipboardManager;

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
			try {
				ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(mContext.CLIPBOARD_SERVICE);
				clipboard.setText(text);
				((MainActivity)mContext).ToastIt("Copied to clipboard");
			} catch (Exception e) {
				
			}
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
	public String bitcoinShowAddress(String vc) {
		return ((MainActivity)mContext).GetPublicAddress(vc);
	}
	@JavascriptInterface
	public String bitcoinPrivateKey(String vc) {
		return ((MainActivity)mContext).GetPrivateKey(vc);
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
}
