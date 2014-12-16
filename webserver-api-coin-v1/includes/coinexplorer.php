<?php

function ExecuteURL($url) {
	/*$response = "";
	// Initialize session and set URL.
	$ch = curl_init();
	curl_setopt($ch, CURLOPT_URL, $url);
	// Set so curl_exec returns the result instead of outputting it.
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
	// Get the response and close the channel.
	$response = curl_exec($ch);
	curl_close($ch);
	return $response;*/
	return @file_get_contents($url);
}

function ParseUnspentTransList($vc,$text) {
	$response = "";
	$phpArray = json_decode($text,true);
	foreach ($phpArray as $key => $value) { 
    	//echo "<h2>key=$key</h2>";
    	if (is_array($value)) {
    		foreach ($value as $k => $v) {
    			if ($k=="unspent") {
    				//echo "<h2>key1=$k</h2><br />";
    				$isFirst = false;
    				foreach ($v as $k2 => $v2) {
    					$tx = "";
						$amt = 0.00;
						$n = 0;
						$confirms = 0;
						$script = "";
    					//echo "key2 = $k2 | $v2 <br />";
    					foreach ($v2 as $k3 => $v3) {
    						//echo "key3 = $k3 | $v3 <br />";
    						     if ($k3=="tx")            $tx       = trim($v3);
							else if ($k3=="amount")        $amt      = floatval(trim($v3));
    						else if ($k3=="n")             $n        = intval(trim($v3));
    						else if ($k3=="confirmations") $confirms = trim(intval($v3));
							else if ($k3=="script")        $script   = trim($v3);
    					}
    					if ($isFirst == 0)
    						$isFirst = true;
    					else
    						$response .= ","; // <br>\n";
    					$response .= "tx=$tx&amt=$amt&n=$n&confirms=$confirms&script=$script";
    					//echo "<br>response=$response<br>";
    				}
    			}
        		// else echo "$k | $v <br />"; 
    		}
    	}
	}
	return $response;
}
function ParseUnspentBalance($vc,$text) {
	// {
	//	"status":"success","data": {
	//		"address":"12ZAHPjLReScC5EkCMq9nEeFmXbHSixMmX","unspent":[{
	//			"tx":"e0796816481a014fe78bcda8b4d5d7e0a699d1266c92a672b078923601635a2e",
	//			"amount":"0.01000000",
	//			"n":0,
	//			"confirmations":1447,
	//			"script":"76a914110d2f6fe3c3db337e64243c0263581baae6df5088ac"
	//		}]
	//	},
	//	"code":200,"message":""}
	$balance = 0.00;
	$phpArray = json_decode($text,true);
	foreach ($phpArray as $key => $value) { 
    	// echo "<h2>$key</h2>";
    	if (is_array($value)) {
    		foreach ($value as $k => $v) {
    			if ($k=="unspent") {
    				// echo "<h2>$k</h2><br />"; s
    				foreach ($v as $k2 => $v2) {
    					//echo "$k2 | $v2 <br />";
    					foreach ($v2 as $k3 => $v3) {
    						// echo "$k3 | $v3 <br />";
    						if ($k3=="amount") {
    							$balance += floatval($v3);
    						}
    					}
    				}
    			}
        		// else echo "$k | $v <br />"; 
    		}
    	}
	}
	return $balance;
	/*if (strpos($response,'Invalid address') !== false) {
    preg_match_all(
      '/<tr><td valign="top" align="right"><font size="2"><a href=".*?">(.*?):<\/font><\/a><\/td><td><font size="2">(.*?)<\/font><\/td><\/tr>/s', // <\/td><\/tr>
      $response,
      $outputs,
      PREG_SET_ORDER
    );
    foreach ($outputs as $rowdata) {
      //echo "1=".$rowdata[1]."\n";
      //echo "2=".$rowdata[2]."\n";
 	}
	}*/
}

function GetCoin($get) {
	$coin = "TBTC";
	if (isset($get)) {
    	$coin = strtoupper(trim($get));
	}
	if (!IsValidVC($coin)) $coin = "TBTC"; // default to BTC testnet
	return $coin;
}

function GetAddress($get) {
	$address = "";
	if (isset($get)) {
    	$address = trim($get);
	}
	$address = preg_replace("/[^a-zA-Z0-9]+/", "", $address); // only alpha numeric
	return $address;
}

function GetConfirms($get) {
	$confirms = 0;
	if (isset($get)) {
    	if ($get == "") $confirms = 1;
		else $confirms = intval($get);
	}
	if ($confirms < 0) $confirms *= -1; // stay positive bro
	return $confirms;
}

function GetListAll($get) {
	$listall = false;
	if (isset($get)) {
		if (strtolower(trim($get)) == "true") $listall = true;
	}
	return $listall;
}

function GetTestnetByVC($vc) {
	$usetestnet = false;
	if (isset($vc)) {
		if ($vc == "TBTC") $usetestnet = true;
	}
	return $usetestnet;
}

function GetTestnetByAddress($address) {
	$usetestnet = false;
	if (isset($vc)) {
		if (strlen($vc)>1) {
			if ($address[0]=='m' || $address[0]=='n') $usetestnet = true;
		}
	}
	return $usetestnet;
}

function GetUnspentURL($vc,$address,$confirms) {
	//$url = "http://blockexplorer.com/testnet/address/$address";
	//$url = "http://blockexplorer.com/address/$address";
	//$url = "https://blockchain.info/unspent?active=$address&format=json";
	$url = "http://".strtolower($vc).".blockr.io/api/v1/address/unspent/$address?confirmations=$confirms";
	if ($confirms=="0") $url = "http://".strtolower($vc).".blockr.io/api/v1/address/unspent/$address?unconfirmed=1";
	return $url;
}

function IsValidVC($vc) {
	if ($vc != "TBTC" && 
		$vc != "BTC" && 
		$vc != "LTC" && 
		$vc != "PPC") return false;
	return true;
}

?>
