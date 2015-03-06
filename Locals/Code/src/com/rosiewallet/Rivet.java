package com.rosiewallet;

public class Rivet {
	// Intent
	public static final String RIVET_INTENT		= "com.rivetz.adaptor.RIVETZ_ANDROID";
	// Request Codes: Main
	public static final int INSTRUCT_GETPOINTER	= 0001; // Get Pointer
	public static final int INSTRUCT_REGISTERPROVIDER = 0002; // Service provider pairs with device
	public static final int INSTRUCT_EXECUTE	= 0003; // Execute a server signed instruction
	// Request Codes: Virtual Coin specific functionality
	public static final int INSTRUCT_SIGNTXN	= 1001; // Sign a bitcoin transaction
	// Request Codes: Service Provider key functionality for storing keys in RivetAndroid
	public static final int INSTRUCT_ADDKEY		= 2001; // Add Key (not secure since key is loaded from Normal World)
	public static final int INSTRUCT_GETKEY		= 2002; // Get Key
	public static final int INSTRUCT_DELETEKEY	= 2003; // Delete a Key
	public static final int INSTRUCT_KEYENUM	= 2004; // Get Next Key Enumerating through them.
	// Request Codes: Crypto
	public static final int INSTRUCT_CREATEKEY	= 3001; // Create a ECDSA Key pair
	public static final int INSTRUCT_SIGN		= 3002; // Create ECDSA Signature
	public static final int INSTRUCT_VERIFY		= 3003; // Verify a signature
	public static final int INSTRUCT_GETPUBPRV	= 3004; // Extract a public key out of a private key
	public static final int INSTRUCT_GETPUBSIG	= 3005; // Extract a public key out of a message, signature and curve.
	public static final int INSTRUCT_ENCRYPT	= 3006; // Encrypted Data using ECDH
	public static final int INSTRUCT_DECRYPT	= 3007; // Decrypt Data using ECDH
	// Request Codes: ECDH
	public static final int REQUEST_ECDH_SHARED	= 4000; // Shared Key using ECDH
	// Request Codes: AES
	public static final int REQUEST_AES_ENCRYPT	= 5001; // DEPRECIATING: Encrypted Data using AES
	public static final int REQUEST_AES_DECRYPT	= 5002; // DEPRECIATING: Decrypt Data using AES
	// Request Codes: HASH
	public static final int REQUEST_HASH		= 7001; // Get a hash result
	// Extra Strings
	public static final String EXTRA_INSTRUCT	= "requestCode";
	public static final String EXTRA_SPID		= "ProviderID";
	public static final String EXTRA_CALLID		= "CallId";
	public static final String EXTRA_KEYNAME	= "KeyName";
	public static final String EXTRA_KEYRECORD	= "KeyObject";
	public static final String EXTRA_DEVICEPOINTER	= "DevicePointer";
	public static final String EXTRA_PUB		= "PUB";
	public static final String EXTRA_PRV		= "PRV";
	public static final String EXTRA_TOPUB		= "TOPUB";
	public static final String EXTRA_AMT		= "AMT";
	public static final String EXTRA_FEE		= "FEE";
	public static final String EXTRA_TRANS		= "TRANS";
	public static final String EXTRA_SIGNED		= "SignedTrans";
	public static final String EXTRA_SIGNDONE	= "SignDone";
	public static final String EXTRA_PUBLICDATA	= "PublicData";
	public static final String EXTRA_SECUREDATA	= "SecureData";
	public static final String EXTRA_KEYTYPE	= "KeyType";
	public static final String EXTRA_VC		= "vc";
	public static final String EXTRA_VC_PUBADDR	= "PublicAddress";
	public static final String EXTRA_PUBKEY		= "PublicKey";
	public static final String EXTRA_PRVKEY		= "PrivateKey";
	public static final String EXTRA_MESSAGE	= "Message";    // Ascii String
	public static final String EXTRA_HEXSTRING	= "MessageHex"; // HexString
	public static final String EXTRA_BLOB		= "MessageBIN"; // byte array
	public static final String EXTRA_SIGNATURE	= "Signature";
	public static final String EXTRA_VERIFIED	= "Verified";
	public static final String EXTRA_SHAREDKEY	= "SharedKey";
	public static final String EXTRA_KEY		= "Key";
	public static final String EXTRA_HASH_ALGO	= "HashAlgo";
	// KEYTYPE
	public static final String KEYTYPE_VC_BTC		= "VC_BTC";		/* Bitcoin (see also Misc crypto currencies below for more currencies) */
	public static final String KEYTYPE_ECDSA_DEFAULT 	= "VC_BTC";		/*!< bitcoin / 256-bits "Koblitz" curve */
	public static final String KEYTYPE_ECDSA_SECP192R1	= "ECDSA_SECP192R1";	/*!< 192-bits NIST curve  */
	public static final String KEYTYPE_ECDSA_SECP224R1	= "ECDSA_SECP224R1";	/*!< 224-bits NIST curve  */
	public static final String KEYTYPE_ECDSA_SECP256R1	= "ECDSA_SECP256R1";	/*!< 256-bits NIST curve  */
	public static final String KEYTYPE_ECDSA_SECP384R1	= "ECDSA_SECP384R1";	/*!< 384-bits NIST curve  */
	public static final String KEYTYPE_ECDSA_SECP521R1	= "ECDSA_SECP521R1";	/*!< 521-bits NIST curve  */
	public static final String KEYTYPE_ECDSA_BP256R1	= "ECDSA_BP256R1";	/*!< 256-bits Brainpool curve */
	public static final String KEYTYPE_ECDSA_BP384R1	= "ECDSA_BP384R1";	/*!< 384-bits Brainpool curve */
	public static final String KEYTYPE_ECDSA_BP512R1	= "ECDSA_BP512R1";	/*!< 512-bits Brainpool curve */
	public static final String KEYTYPE_ECDSA_M221		= "ECDSA_M221";		/*!< (reserved / not implemented yet)    */
	public static final String KEYTYPE_ECDSA_M255		= "ECDSA_M255";		/*!< Curve25519               */
	public static final String KEYTYPE_ECDSA_M383		= "ECDSA_M383";		/*!< (reserved / not implemented yet)    */
	public static final String KEYTYPE_ECDSA_M511		= "ECDSA_M511";		/*!< (reserved / not implemented yet)    */
	public static final String KEYTYPE_ECDSA_SECP192K1	= "ECDSA_SECP192K1";	/*!< 192-bits "Koblitz" curve */
	public static final String KEYTYPE_ECDSA_SECP224K1	= "ECDSA_SECP224K1";	/*!< 224-bits "Koblitz" curve */
	public static final String KEYTYPE_ECDSA_SECP256K1	= "ECDSA_SECP256K1";	/*!< 256-bits "Koblitz" curve (Bitcoin) */
	// ALGOs
	public static final String ECC_ALGO_AES		= "AES";
	// HASHs
	public static final String HASH_SHA256		= "SHA256";
	public static final String HASH_SHA256x2	= "SHA256x2";	
	// ERROR CODES - Rivetz
	public static final int ERROR_NONE		= -0x00000001; // no error - 4 byte error code or use RESULT_OK
	public static final int ERROR_CANCELED		= 0x00000000; // user cancelled intent or use RESULT_CANCELED
	public static final int ERROR_UNKNOWN		= 0x00000001; // uknown - generic error result
	public static final int ERROR_INVALID_SPID	= 0x00000020; // Invalid Service Provider ID
	public static final int ERROR_INVALID_JSON	= 0x00000022; // Invalid JSON passed
	public static final int ERROR_INVALID_VC	= 0x00000024; // Invalid VC Virtual Coin pased
	public static final int ERROR_INVALID_KEYTYPE	= 0x00000026; // Invalid KEYTYPE passed
	public static final int ERROR_INVALID_KEYNAME	= 0x00000028; // Invalid KEYNAME passed
	public static final int ERROR_KEYNAME_EXISTS	= 0x0000002A; // KEYNAME already exists when adding or creating a key
	
	// Misc crypto currencies support and not supported
	public static final String KEYTYPE_VC_TBTC		= "VC_TBTC";		/* Bitcoin testnet */
	public static final String KEYTYPE_VC_LTC		= "VC_LTC";		/* Litecoin */
	public static final String KEYTYPE_VC_PPC		= "VC_PPC";		/* Peercoin */
	// Easy Coins to implement not implemented yet / Reserved
	public static final String KEYTYPE_VC_FRC		= "VC_FRC";		/* Freicoin */
	public static final String KEYTYPE_VC_DOGE		= "VC_DOGE";		/* Dogecoin */
	public static final String KEYTYPE_VC_NMC		= "VC_NMC";		/* Namecoin */
	public static final String KEYTYPE_VC_IXC		= "VC_IXC";		/* Ixcoin */
	public static final String KEYTYPE_VC_UNO		= "VC_UNO";		/* Unobtanium */
	public static final String KEYTYPE_VC_NVC		= "VC_NVC";		/* Novacoin */
	public static final String KEYTYPE_VC_MZC		= "VC_MZC";		/* MazaCoin */
	public static final String KEYTYPE_VC_ANC		= "VC_ANC";		/* Anoncoin */
	public static final String KEYTYPE_VC_MAX		= "VC_MAX";		/* MaxCoin */
	// Bitcoin 2.0 type coins not implemented yet / Reserved
	public static final String KEYTYPE_VC_XRP		= "VC_XRP";		/* Ripple */
	public static final String KEYTYPE_VC_XCP		= "VC_XCP";		/* Counterparty */
	public static final String KEYTYPE_VC_MSC		= "VC_MSC";		/* Mastercoin (Omni Layer) */
	public static final String KEYTYPE_VC_NXT		= "VC_NXT";		/* Nxt */
	public static final String KEYTYPE_VC_NXTV		= "VC_NXTV";		/* NXTventure */
	public static final String KEYTYPE_VC_NXTP		= "VC_NXTP";		/* NXTprivacy */
	public static final String KEYTYPE_VC_NXTTY		= "VC_NXTTY";		/* Nxttycoin */
	public static final String KEYTYPE_VC_MMNXT		= "VC_MMNXT";		/* MMNXT */
	// unknown type coins not implemented yet / Reserved
	public static final String KEYTYPE_VC_BTS		= "VC_BTS";		/* BitShares */
	public static final String KEYTYPE_VC_XPY		= "VC_XPY";		/* PayCoin */
	public static final String KEYTYPE_VC_STR		= "VC_STR";		/* Stellar */
	public static final String KEYTYPE_VC_MAID		= "VC_MAID";		/* MaidSafeCoin */
	public static final String KEYTYPE_VC_DRK		= "VC_DRK";		/* Darkcoin */
	public static final String KEYTYPE_VC_NSR		= "VC_NSR";		/* NuShares */
	public static final String KEYTYPE_VC_BANX		= "VC_BANX";		/* BanxShares */
	public static final String KEYTYPE_VC_YBC		= "VC_YBC";		/* YbCoin */
	public static final String KEYTYPE_VC_UNITY		= "VC_UNITY";		/* SuperNET */
	public static final String KEYTYPE_VC_NBT		= "VC_NBT";		/* NuBits */
	public static final String KEYTYPE_VC_FC2		= "VC_FC2";		/* FuelCoin */
	public static final String KEYTYPE_VC_XMR		= "VC_XMR";		/* Monero */
	public static final String KEYTYPE_VC_GEMZ		= "VC_GEMZ";		/* Gems */
	public static final String KEYTYPE_VC_BLK		= "VC_BLK";		/* BlackCoin */
	public static final String KEYTYPE_VC_BCN		= "VC_BCN";		/* Bytecoin */
	public static final String KEYTYPE_VC_SWARM		= "VC_SWARM";		/* Swarm */
	public static final String KEYTYPE_VC_ETC		= "VC_ETC";		/* Ethercoin */
	public static final String KEYTYPE_VC_BTCD		= "VC_BTCD";		/* BitcoinDark */
	public static final String KEYTYPE_VC_SJCX		= "VC_SJCX";		/* Storjcoin X */
	public static final String KEYTYPE_VC_XTC		= "VC_XTC";		/* TileCoins */
	public static final String KEYTYPE_VC_QRK		= "VC_QRK";		/* Quark */
	public static final String KEYTYPE_VC_NOTE		= "VC_NOTE";		/* DNotes */
	public static final String KEYTYPE_VC_MONA		= "VC_MONA";		/* MonaCoin */
	public static final String KEYTYPE_VC_BITUSD		= "VC_BITUSD";		/* BitUSD */
	public static final String KEYTYPE_VC_RDD		= "VC_RDD";		/* ReddCoin */
	public static final String KEYTYPE_VC_DEX		= "VC_DEX";		/* InstantDEX */
	public static final String KEYTYPE_VC_CLAM		= "VC_CLAM";		/* Clams */
	public static final String KEYTYPE_VC_EMC		= "VC_EMC";		/* EmerCoin */
	public static final String KEYTYPE_VC_XDN		= "VC_XDN";		/* DarkNote */
	public static final String KEYTYPE_VC_PANGEA		= "VC_PANGEA";		/* Pangea Poker */
	public static final String KEYTYPE_VC_IFC		= "VC_IFC";		/* Infinitecoin */
	public static final String KEYTYPE_VC_WDC		= "VC_WDC";		/* WorldCoin */
	public static final String KEYTYPE_VC_MEC		= "VC_MEC";		/* Megacoin */
	public static final String KEYTYPE_VC_JINN		= "VC_JINN";		/* Jinn */
	public static final String KEYTYPE_VC_SDC		= "VC_SDC";		/* ShadowCash */
	public static final String KEYTYPE_VC_NAV		= "VC_NAV";		/* Navajo */
	public static final String KEYTYPE_VC_XPM		= "VC_XPM";		/* Primecoin */
	public static final String KEYTYPE_VC_JLH		= "VC_JLH";		/* jl777hodl */
	public static final String KEYTYPE_VC_FTC		= "VC_FTC";		/* Feathercoin */
	public static final String KEYTYPE_VC_VIA		= "VC_VIA";		/* Viacoin */
	public static final String KEYTYPE_VC_I0C		= "VC_I0C";		/* I0Coin */
	public static final String KEYTYPE_VC_PTS		= "VC_PTS";		/* BitShares PTS */
	public static final String KEYTYPE_VC_SF0		= "VC_SF0";		/* sharkfund0 */
	public static final String KEYTYPE_VC_FAIR		= "VC_FAIR";		/* Faircoin */
	public static final String KEYTYPE_VC_BILS		= "VC_BILS";		/* BilShares */
	public static final String KEYTYPE_VC_NLG		= "VC_NLG";		/* Guldencoin */
	public static final String KEYTYPE_VC_POT		= "VC_POT";		/* PotCoin */
	public static final String KEYTYPE_VC_HYPER		= "VC_HYPER";		/* Hyper */
	public static final String KEYTYPE_VC_ARCH		= "VC_ARCH";		/* ARCHcoin */
	public static final String KEYTYPE_VC_VRC		= "VC_VRC";		/* VeriCoin */
	public static final String KEYTYPE_VC_BURST		= "VC_BURST";		/* Burst */
	public static final String KEYTYPE_VC_UNB		= "VC_UNB";		/* UnbreakableCoin */
	public static final String KEYTYPE_VC_SKYNET		= "VC_SKYNET";		/* SkyNET */
	// ? public static final String KEYTYPE_VC_ANC		= "VC_ANC";		/* Anoncoin */
	public static final String KEYTYPE_VC_XC		= "VC_XC";		/* XCurrency */
	public static final String KEYTYPE_VC_URO		= "VC_URO";		/* Uro */
	public static final String KEYTYPE_VC_DGB		= "VC_DGB";		/* DigiByte */
	public static final String KEYTYPE_VC_CURE		= "VC_CURE";		/* CureCoin */
	public static final String KEYTYPE_VC_ZET		= "VC_ZET";		/* Zetacoin */
	public static final String KEYTYPE_VC_APC		= "VC_APC";		/* Applecoin */
	public static final String KEYTYPE_VC_CANN		= "VC_CANN";		/* CannabisCoin */
	public static final String KEYTYPE_VC_SYS		= "VC_SYS";		/* SysCoin */
	public static final String KEYTYPE_VC_SPR		= "VC_SPR";		/* SpreadCoin */
	public static final String KEYTYPE_VC_CNMT		= "VC_CNMT";		/* Coinomat */
	public static final String KEYTYPE_VC_NXTC		= "VC_NXTC";		/* NXTcoinsco / Tradebots */
	public static final String KEYTYPE_VC_MTC		= "VC_MTC";		/* Marinecoin */
	public static final String KEYTYPE_VC_UTC		= "VC_UTC";		/* UltraCoin */
	public static final String KEYTYPE_VC_XST		= "VC_XST";		/* Stealthcoin */
	public static final String KEYTYPE_VC_DICE		= "VC_DICE";		/* NeoDICE */
	public static final String KEYTYPE_VC_PND		= "VC_PND";		/* Pandacoin (PND) */
	public static final String KEYTYPE_VC_SLR		= "VC_SLR";		/* SolarCoin */
	public static final String KEYTYPE_VC_QORA		= "VC_QORA";		/* Qora */
	public static final String KEYTYPE_VC_CCS		= "VC_CCS";		/* CryptoCoins */
	public static final String KEYTYPE_VC_GLD		= "VC_GLD";		/* GoldCoin */
	public static final String KEYTYPE_VC_BAY		= "VC_BAY";		/* BitBay */
	public static final String KEYTYPE_VC_BITCNY		= "VC_BITCNY";		/* BitCNY */
	public static final String KEYTYPE_VC_DGC		= "VC_DGC";		/* Digitalcoin */
	public static final String KEYTYPE_VC_DEM		= "VC_DEM";		/* Deutsche eMark */
	public static final String KEYTYPE_VC_XCPSCO		= "VC_XCPSCO";		/* Scotcoin */
	public static final String KEYTYPE_VC_BLU		= "VC_BLU";		/* BlueCoin */
	public static final String KEYTYPE_VC_BOST		= "VC_BOST";		/* BoostCoin */
	public static final String KEYTYPE_VC_MOON		= "VC_MOON";		/* Mooncoin */
	public static final String KEYTYPE_VC_VTC		= "VC_VTC";		/* Vertcoin */
	public static final String KEYTYPE_VC_NODE		= "VC_NODE";		/* Node */
	public static final String KEYTYPE_VC_MMXIV		= "VC_MMXIV";		/* Maiuticoin */
	public static final String KEYTYPE_VC_DVC		= "VC_DVC";		/* Devcoin */
	public static final String KEYTYPE_VC_OCUPY		= "VC_OCUPY";		/* Ocupy */
	public static final String KEYTYPE_VC_ZEIT		= "VC_ZEIT";		/* Zeitcoin */
	public static final String KEYTYPE_VC_SWIFT		= "VC_SWIFT";		/* BitSwift */
	public static final String KEYTYPE_VC_DMD		= "VC_DMD";		/* Diamond */
	public static final String KEYTYPE_VC_XDP		= "VC_XDP";		/* Dogeparty */
	public static final String KEYTYPE_VC_CZC		= "VC_CZC";		/* CzechCrownCoin */
	public static final String KEYTYPE_VC_PRVBET		= "VC_PRVBET";		/* Privatebet */
	public static final String KEYTYPE_VC_ZCC		= "VC_ZCC";		/* ZcCoin */
	public static final String KEYTYPE_VC_OPAL		= "VC_OPAL";		/* Opal */
	public static final String KEYTYPE_VC_START		= "VC_START";		/* Startcoin */
	public static final String KEYTYPE_VC_NTR		= "VC_NTR";		/* Neutrino */
	public static final String KEYTYPE_VC_MRKT		= "VC_MRKT";		/* FreeMarket */
	public static final String KEYTYPE_VC_NET		= "VC_NET";		/* NetCoin */
	public static final String KEYTYPE_VC_VPN		= "VC_VPN";		/* VPNCoin */
	public static final String KEYTYPE_VC_GSX		= "VC_GSX";		/* GlowShares */
	public static final String KEYTYPE_VC_LTBC		= "VC_LTBC";		/* LTBcoin */
	public static final String KEYTYPE_VC_GMC		= "VC_GMC";		/* GamersCoin */
	public static final String KEYTYPE_VC_SYNC		= "VC_SYNC";		/* Sync */
	public static final String KEYTYPE_VC_XWT		= "VC_XWT";		/* World Trade Funds */
	public static final String KEYTYPE_VC_CCN		= "VC_CCN";		/* Cannacoin */
	public static final String KEYTYPE_VC_NEM		= "VC_NEM";		/* NEMstake */
	public static final String KEYTYPE_VC_JPC		= "VC_JPC";		/* JackpotCoin */
	public static final String KEYTYPE_VC_PINK		= "VC_PINK";		/* PinkCoin */
	public static final String KEYTYPE_VC_ATOMIC		= "VC_ATOMIC";		/* ATOMIC */
	public static final String KEYTYPE_VC_AC		= "VC_AC";		/* AsiaCoin */
	public static final String KEYTYPE_VC_MGW		= "VC_MGW";		/* MGW */
	public static final String KEYTYPE_VC_MINT		= "VC_MINT";		/* Mintcoin */
	public static final String KEYTYPE_VC_NAUT		= "VC_NAUT";		/* NautilusCoin */
	public static final String KEYTYPE_VC_HBN		= "VC_HBN";		/* HoboNickels */
	public static final String KEYTYPE_VC_XCH		= "VC_XCH";		/* ClearingHouse */
	public static final String KEYTYPE_VC_KARM		= "VC_KARM";		/* Karmacoin */
	public static final String KEYTYPE_VC_WATER		= "VC_WATER";		/* CleanWaterCoin */
	public static final String KEYTYPE_VC_MYR		= "VC_MYR";		/* Myriadcoin */
	public static final String KEYTYPE_VC_TIPS		= "VC_TIPS";		/* FedoraCoins */
	public static final String KEYTYPE_VC_BBR		= "VC_BBR";		/* Boolberry */
	public static final String KEYTYPE_VC_USDE		= "VC_USDE";		/* USDe */
	public static final String KEYTYPE_VC_RIC		= "VC_RIC";		/* Riecoin */
	public static final String KEYTYPE_VC_VIOR		= "VC_VIOR";		/* Viorcoin */
	public static final String KEYTYPE_VC_BTM		= "VC_BTM";		/* Bitmark */
	public static final String KEYTYPE_VC_UNC		= "VC_UNC";		/* UnionCoin */
	public static final String KEYTYPE_VC_CGB		= "VC_CGB";		/* Cryptogenic Bullion */
	public static final String KEYTYPE_VC_ARI		= "VC_ARI";		/* Aricoin */
	public static final String KEYTYPE_VC_HYP		= "VC_HYP";		/* HyperStake */
	public static final String KEYTYPE_VC_HTML5		= "VC_HTML5";		/* HTMLCOIN */
	public static final String KEYTYPE_VC_FLT		= "VC_FLT";		/* FlutterCoin */
	public static final String KEYTYPE_VC_EAC		= "VC_EAC";		/* EarthCoin */
	public static final String KEYTYPE_VC_HZ		= "VC_HZ";		/* Horizon */
	public static final String KEYTYPE_VC_NOO		= "VC_NOO";		/* NooCoin */
	public static final String KEYTYPE_VC_EFL		= "VC_EFL";		/* Electronic Guilden */
	// public static final String KEYTYPE_VC_TEST		= "VC_TEST";		/* TEST */	

	public static String FormatError(int ERROR) {
		return (ERROR < 0 ? "-" : "")+
			"0x"+
			("00000000" + Integer.toHexString(ERROR)).substring(Integer.toHexString(ERROR).length());
	}
}
