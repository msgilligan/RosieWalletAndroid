<?php
include("includes/coinexplorer.php");
@$vc = GetCoin($_GET["coin"]);
@$trans = GetAddress($_GET["trans"]);
$hex = "{\"hex\":\"$trans\"}";
$url = "http://".strtolower($vc).".blockr.io/api/v1/tx/push/";
$ch = curl_init($url);
curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "POST");
curl_setopt($ch, CURLOPT_POSTFIELDS, $hex);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_HTTPHEADER, array(                             
    'Content-Type: application/json',                                  
    'Content-Length: ' . strlen($hex))                          
);
$response = curl_exec($ch);
if (strpos($response, "success") !== FALSE) echo "OK";
else echo"Result = ".$response;
die();
/*
Result = {"status":"success","data":{"tx":{"txid":"f65a1de9947c641fb98dc2bc989fbf832d23b8732c3498048640c545e266656d","version":1,"locktime":0,"vin":[{"txid":"904755f8f3e26eac2f89c7c7e7ab65f06d930ed4de68181075af6832ad76038b","vout":0,"scriptSig":{"asm":"3045022100b857f787c23c49d17fdfdb6a8f3c3250b3a43cf55b91a1bbae285be58ce86a4f02207f0e9486cade88ac3e6c0ea27fd779da71b58aff3299643f3407272c90a0707201 0441b8d9d0ee947d228fd4a8526a246951497d128fee42b00ff8c0c56bbc252723c2d778787a205084b633ebc55dc740746c91a47d71adb7131ecd2334b993ba8a","hex":"483045022100b857f787c23c49d17fdfdb6a8f3c3250b3a43cf55b91a1bbae285be58ce86a4f02207f0e9486cade88ac3e6c0ea27fd779da71b58aff3299643f3407272c90a0707201410441b8d9d0ee947d228fd4a8526a246951497d128fee42b00ff8c0c56bbc252723c2d778787a205084b633ebc55dc740746c91a47d71adb7131ecd2334b993ba8a"},"sequence":4294967295}],"vout":[{"value":2.59999,"n":0,"scriptPubKey":{"asm":"OP_DUP OP_HASH160 6134e5605f887928338e7607d6ee80017bec403b OP_EQUALVERIFY OP_CHECKSIG","hex":"76a9146134e5605f887928338e7607d6ee80017bec403b88ac","reqSigs":1,"type":"pubkeyhash","addresses":["mpNwCwV7WUpPVQSFsBVeJgcRGMuTi4ehEZ"]}},{"value":1,"n":1,"scriptPubKey":{"asm":"OP_DUP OP_HASH160 f1c7ae1222a7715d22719aee2bffd95e4a7c9d38 OP_EQUALVERIFY OP_CHECKSIG","hex":"76a914f1c7ae1222a7715d22719aee2bffd95e4a7c9d3888ac","reqSigs":1,"type":"pubkeyhash","addresses":["n3ZNMHuXkQMnpDSko31fPNNx5PUivipWzX"]}}]},"statistics":{"vins_sum":"3.59999000","vouts_sum":"3.59999000","fee":"0.00000000"}},"code":200,"message":""}
*/
?>
