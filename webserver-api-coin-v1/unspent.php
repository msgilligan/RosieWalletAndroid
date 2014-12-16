<?php
include("includes/coinexplorer.php");
@$vc = GetCoin($_GET["coin"]);
@$address = GetAddress($_GET["address"]);
@$response = Unspent();
echo $response;
die();

function Unspent() {
 global $vc,$address;
 $trans = "";
 //if ($vc=="BTC") { // Try BlockChain.info First for BTC
//	$url = "https://blockchain.info/unspent?active=$address";
//	@$response = ExecuteURL($url);
//	@$trans = ParseBlockChain($response);
//	if ($trans!="") return $trans;
 //}
 // Try BlockR
 @$url = "http://".strtolower($vc).".blockr.io/api/v1/address/unspent/$address";
 @$response = ExecuteURL($url);
 @$trans = ParseBlockR($response);
 return $trans;
}

function ParseBlockChain($text) {
 $trans = "";
 if (!empty($text)) {
	$R = array();
	$BC = json_decode($text,true);
	foreach ($BC["unspent_outputs"] as $key => $value) {
		//die(print_r($BC,true));
		// echo "Key = $key value = $value <br>";
		$T["tx"]=$value["tx_hash_big_endian"];
		$T["amount"]=number_format(intval($value["value"])/100000000,8,'.','');
		$T["n"]=intval($value["tx_output_n"]);
		$T["confirmations"]=intval($value["confirmations"]);
		$T["script"]=$value["script"];
		array_push($R,$T);
	}
	usort($R,"SortConfirms");
	$J["unspent"]=$R;
	$trans = json_encode($J);
	if (!empty($trans)) return $trans;
	$trans = "";
 }
 return $trans;
}

function ParseBlockR($text) {
 global $vc,$address;
 $trans = "";
 if (!empty($text)) {
        $BC = json_decode($text,true);
	$R = $BC["data"]["unspent"];
        $url = "http://".strtolower($vc).".blockr.io/api/v1/address/unconfirmed/$address";
        @$response = ExecuteURL($url);
        if (!empty($response)) {
                $BC = json_decode($response,true);
                // die(print_r($BC,true));
                foreach ($BC["data"]["unconfirmed"] as $key => $value) {
                        $T["tx"]=$value["tx"];
                        $T["amount"]=number_format(floatval($value["amount"]),8,'.','');
                        $T["n"]=$value["n"];
                        $T["confirmations"]=0;
                        $T["script"]="NotRedeemable";
                        array_push($R,$T);
                }
        }
	usort($R,"SortConfirms");
	$J["unspent"]=$R;
        $trans = json_encode($J);
        if (!empty($trans)) return $trans;
        $trans = "";
 }
 return $trans;
}

function SortConfirms($item1,$item2) {
        if ($item1['confirmations'] == $item2['confirmations']) return 0;
        return ($item1['confirmations'] < $item2['confirmations']) ? 1 : -1;
}


?>
