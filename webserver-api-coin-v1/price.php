<?php
include("includes/coinexplorer.php");
@$vc = GetCoin($_GET["coin"]);
@$response = GetPrice();
echo $response;
die();

function GetPrice() {
	global $vc;
	// http://btc.blockr.io/api/v1/exchangerate/current
	@$url = "http://".strtolower($vc).".blockr.io/api/v1/exchangerate/current";
	@$response = ExecuteURL($url);
	return $response;
}
/*
{
	"status":"success",
	"data":[{
		"base":"USD",
		"updated_utc":"2015-02-27T04:20:00Z",
		"rates":{
			"BTC":"0.0040300074353637",
			"EUR":"0.883626",
			"ZAR":"11.432447",
			"THB":"32.314218",
			"SGD":"1.351330",
			"PHP":"43.975435",
			"NZD":"1.318282",
			"MYR":"3.577185",
			"MXN":"14.882478",
			"KRW":"1096.226915",
			"INR":"61.751171",
			"ILS":"3.936732",
			"IDR":"12846.001591",
			"HKD":"7.754705",
			"CNY":"6.258814",
			"CAD":"1.246974",
			"BRL":"2.850932",
			"AUD":"1.268622",
			"TRY":"2.481665",
			"RUB":"60.808076",
			"HRK":"6.800831",
			"NOK":"7.567377",
			"CHF":"0.949457",
			"SEK":"8.318989",
			"RON":"3.912521",
			"PLN":"3.670142",
			"HUF":"267.632765",
			"GBP":"0.645136",
			"DKK":"6.598834",
			"CZK":"24.312980",
			"BGN":"1.728197",
			"JPY":"118.883096",
			"USD":"1.000000"
		}
	}],
	"code":200,
	"message":""
}

0.01234 * (1 / rates.BTC) = USD value
0.01234 * (1 / 0.0040300074353637) = $3.06 USD
*/
?>

