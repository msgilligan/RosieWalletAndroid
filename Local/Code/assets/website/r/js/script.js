$(document).ready(function() {
	$("#nav-home-page").click(function() {
		try {
			$("#send-page").hide();
			$("#receive-page").hide();
			$("#settings-page").hide();
			$("#about-page").hide();
			DisplayWallet();
			$("#home-page").show();
			$("#tools-message-sign").hide();
			$("#tools-message-verify").hide();
			$("#tools-message-encrypt").hide();
			$("#tools-message-decrypt").hide();
			
		} catch(e) {}
	});
	$("#nav-send-page").click(function() {
		try {
			$("#home-page").hide();
			$("#receive-page").hide();
			$("#settings-page").hide();
			$("#about-page").hide();
			$("#send-page").show();
			$("#txt-send-btcamount").val('0.0');
			
		} catch(e) {}
	});
	$("#nav-receive-page").click(function() {
		try {
			$("#home-page").hide();
			$("#send-page").hide();
			$("#settings-page").hide();
			$("#about-page").hide();
			$("#receive-page").show();
			
		} catch(e) {}
	});
	$("#btn-home-sign").click(function() {
		try {
			ToolsMenuOn(1);
			
		} catch(e) {}
	});
	$("#btn-home-verify").click(function() {
		try {
			ToolsMenuOn(2);
			
		} catch(e) {}
	});
	$("#btn-home-encrypt").click(function() {
		try {
			ToolsMenuOn(3);
			
		} catch(e) {}
	});
	$("#btn-home-decrypt").click(function() {
		try {
			ToolsMenuOn(4);
			
		} catch(e) {}
	});
	$("#nav-settings-page").click(function() {
		try {
			$("#home-page").hide();
			$("#send-page").hide();
			$("#receive-page").hide();
			$("#settings-page").show();
			$("#about-page").hide();
			$('#select-choice-currency').val(filterTEST(vc)).selectmenu('refresh');
			
		} catch(e) {}
	});
	$("#select-choice-currency").change(function() {
		try {
			switch($(this).val()) {
				case "BTC": // Bitcoin
					vc = "BTC";
					break;
				case "LTC": // Litecoin
					vc = "LTC";
					break;
				case "PPC": // Peercoin
					vc = "PPC";
					break;
				case "NMC": // Namecoin
					vc = "NMC";
					break;
				default:
					vc = "BTC";
					break;
				
			}
			if (vc == "BTC" && usetestnet) vc = "TBTC";
			if (vc != oldvc) { // selection changed
				ChangeVC();
			}
			
		} catch(e) {}
	});
	$("#checkbox-usetestnet").change(function() {
		try {
			usetestnet = $(this).is(':checked');
			if (vc == "BTC" && usetestnet)  vc = "TBTC";
			else if (vc == "TBTC" && usetestnet==false) vc = "BTC";
			if (vc != oldvc) { // selection changed
				ChangeVC();
			}
			
		} catch(e) {}
	});
	$("#txt-fee").change(function() {
		try {
			//alert("text-fee changed");
			newfee = parseFloat($(this).val());
			//alert("newfee "+newfee);
			bigfee = newfee*100000000;
			//alert("bigfee="+bigfee);
			AndroidHost.bitcoinSaveFee(vc,bigfee);
			LoadFee();
		} catch(e) {}
	});
	$("#nav-about-page").click(function() {
		try {
			$("#home-page").hide();
			$("#send-page").hide();
			$("#receive-page").hide();
			$("#settings-page").hide();
			$("#about-page").show();
			
		} catch(e) {}
	});
	$("#btn-bitcoin-newaccount").click(function() {
		try {
			AndroidHost.bitcoinNewAccount(vc);
			bitcoinaddress = AndroidHost.bitcoinShowAddress(vc);
			bitcoinprivate = AndroidHost.bitcoinPrivateKey(vc);
			bitcoinbalanceStr = "";
			bitcoinunbalanceStr = "";
			displayAddress();
			buildQRCode();
			DisplayWallet();
			
		} catch(e) {}
	});
	$("#btn-bitcoin-no").click(function() {
		try {
			DisplayWallet();
		} catch(e) {}
	});
	$("#btn-bitcoin-yes").click(function() {
		try {
			AndroidHost.bitcoinDeleteAccount(vc);
			bitcoinaddress = "";
			bitcoinbalanceStr = "";
			bitcoinunbalanceStr = "";
			$("#receive-qrcode-public").text('');
			$("#receive-qrcode-private").text('');
			displayAddress();
			DisplayWallet();
			
		} catch(e) {}
	});
	$("#btn-send-qr").click(function() {
		try {
			var result = AndroidHost.bitcoinReadQRCode();
			
		} catch(e) {}
	});
	$("#btn-send-send").click(function() {
		try {
			var ToAddress = $("#txt-send-toaddress").val();
			var AmountStr = $("#txt-send-btcamount").val();
			AndroidHost.bitcoinSendCoin(vc,ToAddress,AmountStr);
			$("#txt-send-toaddress").val('');
			$("#txt-send-btcamount").val('0.0');
			
		} catch(e) {}
	});
	$("#btn-home-delete").click(function() {
		try {
			$("#bitcoin-delete").html("Delete Wallet Are You Sure?<br><br>" + 
							GetVCName(vc) + " (" + filterTEST(vc) + ") Address: " + bitcoinaddress);
			$("#bitcoin-delete").show();
			$("#bitcoin-address").hide();
			$("#div-newaccount").hide();
			$("#div-home-delete").hide();
			$("#div-yes").show();
			$("#div-no").show();
			$("#div-home-sign").hide();
			$("#div-home-verify").hide();
			$("#div-home-encrypt").hide();
			$("#div-home-decrypt").hide();
			
		} catch(e) {}
	});
	//  
	$("#btn-copy-clipboard").click(function() {
		try {
			AndroidHost.copyToClipBoard(bitcoinaddress); // new String(bitcoinaddress));
			
		} catch(e) {}
	});
	
	vc = "TBTC";
	oldvc = vc;
	usetestnet = true;
	$("#checkbox-usetestnet").attr("checked",usetestnet).checkboxradio("refresh");
	bitcoinaddress = AndroidHost.bitcoinShowAddress(vc);
	bitcoinprivate = AndroidHost.bitcoinPrivateKey(vc);
	bitcoinbalanceStr = "";
	bitcoinunbalanceStr = "";
	displayAddress();
	buildQRCode();
	DisplayWallet();
	toolsMenu = 0;
	var fee = 0.00;
	var oldfee = -1.00;
	LoadFee();
	var id1 = setInterval(checkBalance,     1 * 1000);
	var id2 = setInterval(checkBalanceExe,  1 * 1000);
	var id3 = setInterval(checkBalance,    60 * 1000);
	var id4 = setInterval(ScanResult,       1 * 1000);
	
	function ScanResult() {
		var scanres = AndroidHost.bitcoinScanResult();
		if (scanres != "") {
			parsed = parseBitcoinURL(scanres);
			$("#txt-send-toaddress").val(parsed.address);
			$("#txt-send-btcamount").val(parsed["amount"]);
		}
	}
	function LoadFee() {
		bfee = AndroidHost.bitcoinGetFee(vc);
		fee =  bfee / 100000000.00;
		if (oldfee != fee) {
			oldfee = fee;
			$("#txt-fee").val(fee.toFixed(8));
		}
	}
	function checkBalance() {
		AndroidHost.bitcoinGetBalanceStart(vc);
		if (id1 != "") {
			clearTimeout(id1);
			id1="";
		}
	}
	function checkBalanceExe() {
		bitcoinbalanceStr = AndroidHost.bitcoinGetBalance(vc);
		bitcoinunbalanceStr = AndroidHost.bitcoinGetZeroBalance(vc);
		DisplayBalance();
	}
	function filterTEST(instr) {
		outstr = instr;
		if (outstr == "TBTC") outstr = "BTC";
		return outstr;
	}
	function ChangeVC() {
		$("#div-image-"+oldvc.toLowerCase()).hide();
		$("#div-image-"+vc.toLowerCase()).show();
		oldvc = vc;
		bitcoinaddress = AndroidHost.bitcoinShowAddress(vc);
		bitcoinprivate = AndroidHost.bitcoinPrivateKey(vc);
		id1 = setInterval(checkBalance, 1 * 1000);
		bitcoinbalanceStr = "";
		bitcoinunbalanceStr = "";
		LoadFee();
		displayAddress();
		buildQRCode();
		DisplayWallet();
		$("#send-content-header").html('<h3>Send ' + GetVCName(vc) + ' (' + filterTEST(vc) + ')</h3>');
		$("#receive-content-header").html('<h3>Receive ' + GetVCName(vc) + ' (' + filterTEST(vc) + ')</h3>');
		$("#home-content-header").html('<h3>Rosie Wallet ' + GetVCName(vc) + ' Test App</h3>');
		if (filterTEST(vc) == "BTC") {
			$("#checkbox-usetestnet").attr("checked",usetestnet).checkboxradio("refresh");
			$("#div-checkbox-usetestnet").show();
		}
		else {
			$("#div-checkbox-usetestnet").hide();
		}
		$('#div-send-amount-html').html(filterTEST(vc) + ' Amount: ');
	}
	function ToolsMenuOn(tooltype) {
		$("#bitcoin-address").hide();
		$("#bitcoin-delete").hide();
		$("#bitcoin-balance").hide();
		$("#bitcoin-unconfirmbalance").hide();
		$("#div-newaccount").hide();
		$("#div-home-delete").hide();
		$("#div-yes").hide();
		$("#div-no").hide();
		switch(tooltype) {
		    case 1:
			$("#tools-message-sign").hide();
			$("#tools-message-verify").hide();
			$("#tools-message-encrypt").hide();
			$("#tools-message-decrypt").show();
			$("#div-home-sign").hide();
			$("#div-home-verify").hide();
			$("#div-home-encrypt").hide();
			$("#div-home-decrypt").hide();
			break;
		    case 2:
			$("#tools-message-sign").hide();
			$("#tools-message-verify").show();
			$("#tools-message-encrypt").hide();
			$("#tools-message-decrypt").hide();
			$("#div-home-sign").hide();
			$("#div-home-verify").hide();
			$("#div-home-encrypt").hide();
			$("#div-home-decrypt").hide();
			break;
		    case 3:
			$("#tools-message-sign").hide();
			$("#tools-message-verify").hide();
			$("#tools-message-encrypt").show();
			$("#tools-message-decrypt").hide();
			$("#div-home-sign").hide();
			$("#div-home-verify").hide();
			$("#div-home-encrypt").hide();
			$("#div-home-decrypt").hide();
			break;
		    case 4:
			$("#tools-message-sign").hide();
			$("#tools-message-verify").hide();
			$("#tools-message-encrypt").hide();
			$("#tools-message-decrypt").show();
			$("#div-home-sign").hide();
			$("#div-home-verify").hide();
			$("#div-home-encrypt").hide();
			$("#div-home-decrypt").hide();
			break;
		    default:
			$("#tools-message-sign").hide();
			$("#tools-message-verify").hide();
			$("#tools-message-encrypt").hide();
			$("#tools-message-decrypt").hide();
			$("#div-home-sign").show();
			$("#div-home-verify").show();
			$("#div-home-encrypt").show();
			$("#div-home-decrypt").show();
		}
		
	}
	function DisplayBalance() {
		if (bitcoinbalanceStr!="") {
			$("#bitcoin-balance").html('<center>Balance: '+bitcoinbalanceStr+' ' + filterTEST(vc) + '</center>');
			if (bitcoinunbalanceStr!="") {
				$("#bitcoin-unconfirmbalance").html('<center>Unconfirmed Balance: '+
									bitcoinunbalanceStr+' ' + filterTEST(vc) + '</center>');
			}
			else {
				$("#bitcoin-unconfirmbalance").html('');
			}
		}
		else {
			$("#bitcoin-balance").html('<center>Balance: undetermined</center>');
			$("#bitcoin-unconfirmbalance").html('');
		}
	}
	function DisplayWallet() {
		if (bitcoinaddress != "") {
			WalletOn();
		}
		else {
			WalletOff();
		}
	}
	function displayAddress() {
		if (bitcoinaddress != "") {
			$("#bitcoin-address").html('<center>' + 
					GetVCName(vc) + ' (' + filterTEST(vc) + ') Address:<br><font size=-1>' +
							bitcoinaddress + '</font></center>');
			$("#receive-address").html('<center><font size=-1>' +
							bitcoinaddress + '</font></center>');
			$("#receive-private").html('<center><font size=-1>' +
							bitcoinprivate + '</font></center>');
			$("#send-message").html('');
			DisplayBalance();
		}
		else {
			$("#bitcoin-address").html('<center>' + 
					GetVCName(vc) + ' (' + filterTEST(vc) + ') Address: not loaded</center>');
			$("#send-message").html('<center>' + 
					GetVCName(vc) + ' (' + filterTEST(vc) + ') Address: not loaded</center>');
			$("#receive-address").html('<center>' + 
					GetVCName(vc) + ' (' + filterTEST(vc) + ') Address: not loaded</center>');
			$("#receive-private").html('');
			$("#bitcoin-balance").html('');
			$("#bitcoin-unconfirmbalance").html('');
			
		}
	}
	function buildQRCode() {
		if (bitcoinaddress != "") {
			$("#receive-qrcode-public").text('');
			$("#receive-qrcode-public").qrcode({
	    			"width": 100,
	    			"height": 100,
	    			"color": "#3a3",
	    			"text": GetVCName(filterTEST(vc)).toLowerCase() + ":" + bitcoinaddress
			});
			
		}
		else {
			$("#receive-qrcode-public").text('');
		}
		if (bitcoinprivate != "") {
			$("#receive-qrcode-private").text('');
			$("#receive-qrcode-private").qrcode({
	    			"width": 100,
	    			"height": 100,
	    			"color": "#3a3",
	    			"text": bitcoinprivate
			});
			
		}
		else {
			$("#receive-qrcode-private").text('');
		}
	}
	function GetVCName(vccheck) {
		switch(vccheck) {
			case "TBTC": // Bitcoin
				vcname = "Testnet Bitcoin";
				break;
			case "BTC": // Bitcoin
				vcname = "Bitcoin";
				break;
			case "LTC": // Litecoin
				vcname = "Litecoin";
				break;
			case "PPC": // Peercoin
				vcname = "Peercoin";
				break;
			case "NMC": // Namecoin
				vcname = "Namecoin";
				break;
			default:
				vcname = "BTC";
				break;
		
		}
		return vcname;
	}
	function WalletOn() {
		$("#bitcoin-address").show();
		$("#bitcoin-delete").hide();
		$("#bitcoin-balance").show();
		$("#bitcoin-unconfirmbalance").show();
		$("#div-newaccount").hide();
		$("#div-home-delete").show();
		$("#div-yes").hide();
		$("#div-no").hide();
		$("#div-home-sign").show();
		$("#div-home-verify").show();
		$("#div-home-encrypt").show();
		$("#div-home-decrypt").show();
		$("#div-copy-clipboard").show();
		$("#div-send-toaddress").show();
		$("#div-send-amount").show();
		$("#div-send-send").show();
		
	}
	function WalletOff() {
		$("#bitcoin-address").show();
		$("#bitcoin-delete").hide();
		$("#bitcoin-balance").hide();
		$("#bitcoin-unconfirmbalance").hide();
		$("#div-newaccount").show();
		$("#div-home-delete").hide();
		$("#div-yes").hide();
		$("#div-no").hide();
		$("#div-home-sign").hide();
		$("#div-home-verify").hide();
		$("#div-home-encrypt").hide();
		$("#div-home-decrypt").hide();
		$("#div-copy-clipboard").hide();
		$("#div-send-toaddress").hide();
		$("#div-send-amount").hide();
		$("#div-send-send").hide();
		
	}
	function parseBitcoinURL(url) {
		var r = /^bitcoin:([a-zA-Z0-9]{27,34})(?:\?(.*))?$/;
		var match = r.exec(url);
		if (!match) return null;
		var parsed = { url: url }
		if (match[2]) {
			var queries = match[2].split('&');
			for (var i = 0; i < queries.length; i++) {
				var query = queries[i].split('=');
				if (query.length == 2) {
					parsed[query[0]] = decodeURIComponent(query[1].replace(/\+/g, '%20'));
				}
			}
		} 
		parsed.address = match[1];
		return parsed;
	}
});
