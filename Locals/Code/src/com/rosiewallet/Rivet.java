package com.rosiewallet;

public class Rivet {
	// Intent
	public static final String RIVET_INTENT		= "com.rivetz.adaptor.RIVETZ_ANDROID";
	//
	// Rivet.java Version
	//
	// The version number x.y.z is split into three parts.
	// Major, Minor, Patchlevel
	//
	public static final int RIVETJAVA_VERSION_MAJOR = 0;
	public static final int RIVETJAVA_VERSION_MINOR = 0;
	public static final int RIVETJAVA_VERSION_PATCH = 1;
	//
	// The single version number has the following structure:
	//    MMNNPP00
	//    Major version | Minor version | Patch version
	//
	public static final int    RIVETJAVA_VERSION_NUMBER     = 0x00000100;
	public static final String RIVETJAVA_VERSION		= "0.0.1";
	public static final String RIVETJAVA_VERSION_FULL	= "Rivet.java v0.0.1";
	//
	// Instructions
	//
	// Request Codes: Registration
	//
	public static final int INSTRUCT_GETPOINTER	  = 0001; // Get Pointer
	public static final int INSTRUCT_REGISTERPROVIDER = 0002; // Service provider pairs with device
	public static final int INSTRUCT_EXECUTE	  = 0003; // Execute a server signed instruction
	// Request Codes: Service Provider key functionality for storing keys in RivetAndroid
	public static final int INSTRUCT_ADDKEY		= 1001; // Add Key (not secure since key is loaded from Normal World)
	public static final int INSTRUCT_GETKEY		= 1002; // Get Key
	public static final int INSTRUCT_DELETEKEY	= 1003; // Delete a Key
	public static final int INSTRUCT_KEYENUM	= 1004; // Get Next Key Enumerating through them.
	// Request Codes: Crypto
	public static final int INSTRUCT_CREATEKEY	= 2001; // Create a ECDSA Key pair to keyname
	public static final int INSTRUCT_SIGN		= 2002; // Create ECDSA Signature from keyname
	public static final int INSTRUCT_VERIFY		= 2003; // Verify a signature from keyname
	public static final int INSTRUCT_ENCRYPT	= 2004; // Encrypted Data using ECDH
	public static final int INSTRUCT_DECRYPT	= 2005; // Decrypt Data using ECDH
	// Request Codes: Coin Specific Crypto
	// DEPRECIATED: public static final int INSTRUCT_GETPUBPRV = 3004; // Extract a public address out of a keyname
	public static final int INSTRUCT_GETADDRESS	= 3001; // Extract a coin public address out of keyname
	public static final int INSTRUCT_SIGNTXN	= 3002; // Sign a bitcoin transaction
	public static final int INSTRUCT_GETPUBSIG	= 3003; // Extract public key out of message & signature (SECP256K1 curve only)
	// Request Codes: ECDH
	public static final int INSTRUCT_ECDH_SHARED	= 4001; // Shared Key between similiar curve ECDH keys
	// Request Codes: HASH
	public static final int INSTRUCT_HASH		= 5001; // Get a hash result
	// Request Codes: AES
	public static final int INSTRUCT_AES_ENCRYPT	= 6001; // Encrypted Data using AES (not secure exposed key)
	public static final int INSTRUCT_AES_DECRYPT	= 6002; // Decrypt Data using AES (not secure exposed key)
	// Extra Strings
	public static final String EXTRA_INSTRUCT	= "EXTRA_INSTRUCT";
	public static final String EXTRA_SPID		= "EXTRA_SPID";
	public static final String EXTRA_CALLID		= "EXTRA_CALLID";
	public static final String EXTRA_KEYNAME	= "EXTRA_KEYNAME";
	public static final String EXTRA_KEYRECORD	= "EXTRA_KEYRECORD";
	public static final String EXTRA_DEVICEPOINTER	= "EXTRA_DEVICEPOINTER";
	public static final String EXTRA_PUB		= "EXTRA_PUB";
	public static final String EXTRA_PRV		= "EXTRA_PRV";
	public static final String EXTRA_TOPUB		= "EXTRA_TOPUB";
	public static final String EXTRA_AMT		= "EXTRA_AMT";
	public static final String EXTRA_FEE		= "EXTRA_FEE";
	public static final String EXTRA_TRANS		= "EXTRA_TRANS";
	public static final String EXTRA_SIGNED		= "EXTRA_SIGNED";
	public static final String EXTRA_SIGNDONE	= "EXTRA_SIGNDONE";
	public static final String EXTRA_PUBLICDATA	= "EXTRA_PUBLICDATA";
	public static final String EXTRA_SECUREDATA	= "EXTRA_SECUREDATA";
	public static final String EXTRA_KEYTYPE	= "EXTRA_KEYTYPE";
	public static final String EXTRA_COIN		= "EXTRA_COIN";
	public static final String EXTRA_COIN_ADDRESS	= "EXTRA_COIN_ADDRESS";
	public static final String EXTRA_PUBKEY		= "EXTRA_PUBKEY";
	public static final String EXTRA_PRVKEY		= "EXTRA_PRVKEY";
	public static final String EXTRA_MESSAGE	= "EXTRA_MESSAGE";   // Ascii String
	public static final String EXTRA_HEXSTRING	= "EXTRA_HEXSTRING"; // Hex String
	public static final String EXTRA_BLOB		= "EXTRA_BLOB";      // byte array
	public static final String EXTRA_SIGNATURE	= "EXTRA_SIGNATURE";
	public static final String EXTRA_VERIFIED	= "EXTRA_VERIFIED";
	public static final String EXTRA_SHAREDKEY	= "EXTRA_SHAREDKEY";
	public static final String EXTRA_KEY		= "EXTRA_KEY";
	public static final String EXTRA_HASH_ALGO	= "EXTRA_HASH_ALGO";
	// KEYTYPE
	public static final String KEYTYPE_COIN_BTC		= "COIN_BTC";		/* Bitcoin (alt coins beloow) */
	public static final String KEYTYPE_ECDSA_DEFAULT 	= "COIN_BTC";		/*!< bitcoin / 256-bits "Koblitz" curve */
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
	public static final String HASH_MD2		= "HASH_MD2";
	public static final String HASH_MD5		= "HASH_MD5";
	public static final String HASH_SHA1		= "HASH_SHA1";
	public static final String HASH_SHA256		= "HASH_SHA256";
	public static final String HASH_DOUBLESHA256	= "HASH_DOUBLESHA256";
	public static final String HASH_RIPEMD160	= "HASH_RIPEMD160";
	//
	//
	// ERROR CODES - Rivetz
	//
	//
	public static final int ERROR_NONE		= -0x00000001; // no error - 4 byte error code or use RESULT_OK
	public static final int ERROR_CANCELED		=  0x00000000; // user cancelled intent or use RESULT_CANCELED
	public static final int ERROR_UNKNOWN		=  0x00000001; // uknown - generic error result
	public static final int ERROR_INVALID_SPID	=  0x00000020; // Invalid Service Provider ID
	public static final int ERROR_INVALID_JSON	=  0x00000022; // Invalid JSON passed
	public static final int ERROR_INVALID_COIN	=  0x00000024; // Invalid Coin pased
	public static final int ERROR_INVALID_KEYTYPE	=  0x00000026; // Invalid KEYTYPE passed
	public static final int ERROR_INVALID_KEYNAME	=  0x00000028; // Invalid KEYNAME passed
	public static final int ERROR_KEYNAME_EXISTS	=  0x0000002A; // KEYNAME already exists when adding or creating a key
	//
	//
	// Alt Coins crypto currencies
	//
	// Alt Coins Supported:
	public static final String KEYTYPE_COIN_TBTC		= "COIN_TBTC";		/* Bitcoin testnet */
	public static final String KEYTYPE_COIN_LTC		= "COIN_LTC";		/* Litecoin */
	public static final String KEYTYPE_COIN_PPC		= "COIN_PPC";		/* Peercoin */
	// Alt Coins to support soon:
	public static final String KEYTYPE_COIN_FRC		= "COIN_FRC";		/* Freicoin */
	public static final String KEYTYPE_COIN_DOGE		= "COIN_DOGE";		/* Dogecoin */
	public static final String KEYTYPE_COIN_NMC		= "COIN_NMC";		/* Namecoin */
	public static final String KEYTYPE_COIN_IXC		= "COIN_IXC";		/* Ixcoin */
	public static final String KEYTYPE_COIN_UNO		= "COIN_UNO";		/* Unobtanium */
	public static final String KEYTYPE_COIN_NVC		= "COIN_NVC";		/* Novacoin */
	public static final String KEYTYPE_COIN_MZC		= "COIN_MZC";		/* MazaCoin */
	public static final String KEYTYPE_COIN_ANC		= "COIN_ANC";		/* Anoncoin */
	public static final String KEYTYPE_COIN_MAX		= "COIN_MAX";		/* MaxCoin */
	// Bitcoin 2.0 type coins not implemented yet / Reserved
	public static final String KEYTYPE_COIN_XRP		= "COIN_XRP";		/* Ripple */
	public static final String KEYTYPE_COIN_XCP		= "COIN_XCP";		/* Counterparty */
	public static final String KEYTYPE_COIN_MSC		= "COIN_MSC";		/* Mastercoin (Omni Layer) */
	public static final String KEYTYPE_COIN_NXT		= "COIN_NXT";		/* Nxt */
	public static final String KEYTYPE_COIN_NXTV		= "COIN_NXTV";		/* NXTventure */
	public static final String KEYTYPE_COIN_NXTP		= "COIN_NXTP";		/* NXTprivacy */
	public static final String KEYTYPE_COIN_NXTTY		= "COIN_NXTTY";		/* Nxttycoin */
	public static final String KEYTYPE_COIN_MMNXT		= "COIN_MMNXT";		/* MMNXT */
	// unknown type coins not implemented yet / Reserved
	public static final String KEYTYPE_COIN_BTS		= "COIN_BTS";		/* BitShares */
	public static final String KEYTYPE_COIN_XPY		= "COIN_XPY";		/* PayCoin */
	public static final String KEYTYPE_COIN_STR		= "COIN_STR";		/* Stellar */
	public static final String KEYTYPE_COIN_MAID		= "COIN_MAID";		/* MaidSafeCoin */
	public static final String KEYTYPE_COIN_DRK		= "COIN_DRK";		/* Darkcoin */
	public static final String KEYTYPE_COIN_NSR		= "COIN_NSR";		/* NuShares */
	public static final String KEYTYPE_COIN_BANX		= "COIN_BANX";		/* BanxShares */
	public static final String KEYTYPE_COIN_YBC		= "COIN_YBC";		/* YbCoin */
	public static final String KEYTYPE_COIN_UNITY		= "COIN_UNITY";		/* SuperNET */
	public static final String KEYTYPE_COIN_NBT		= "COIN_NBT";		/* NuBits */
	public static final String KEYTYPE_COIN_FC2		= "COIN_FC2";		/* FuelCoin */
	public static final String KEYTYPE_COIN_XMR		= "COIN_XMR";		/* Monero */
	public static final String KEYTYPE_COIN_GEMZ		= "COIN_GEMZ";		/* Gems */
	public static final String KEYTYPE_COIN_BLK		= "COIN_BLK";		/* BlackCoin */
	public static final String KEYTYPE_COIN_BCN		= "COIN_BCN";		/* Bytecoin */
	public static final String KEYTYPE_COIN_SWARM		= "COIN_SWARM";		/* Swarm */
	public static final String KEYTYPE_COIN_ETC		= "COIN_ETC";		/* Ethercoin */
	public static final String KEYTYPE_COIN_BTCD		= "COIN_BTCD";		/* BitcoinDark */
	public static final String KEYTYPE_COIN_SJCX		= "COIN_SJCX";		/* Storjcoin X */
	public static final String KEYTYPE_COIN_XTC		= "COIN_XTC";		/* TileCoins */
	public static final String KEYTYPE_COIN_QRK		= "COIN_QRK";		/* Quark */
	public static final String KEYTYPE_COIN_NOTE		= "COIN_NOTE";		/* DNotes */
	public static final String KEYTYPE_COIN_MONA		= "COIN_MONA";		/* MonaCoin */
	public static final String KEYTYPE_COIN_BITUSD		= "COIN_BITUSD";	/* BitUSD */
	public static final String KEYTYPE_COIN_RDD		= "COIN_RDD";		/* ReddCoin */
	public static final String KEYTYPE_COIN_DEX		= "COIN_DEX";		/* InstantDEX */
	public static final String KEYTYPE_COIN_CLAM		= "COIN_CLAM";		/* Clams */
	public static final String KEYTYPE_COIN_EMC		= "COIN_EMC";		/* EmerCoin */
	public static final String KEYTYPE_COIN_XDN		= "COIN_XDN";		/* DarkNote */
	public static final String KEYTYPE_COIN_PANGEA		= "COIN_PANGEA";	/* Pangea Poker */
	public static final String KEYTYPE_COIN_IFC		= "COIN_IFC";		/* Infinitecoin */
	public static final String KEYTYPE_COIN_WDC		= "COIN_WDC";		/* WorldCoin */
	public static final String KEYTYPE_COIN_MEC		= "COIN_MEC";		/* Megacoin */
	public static final String KEYTYPE_COIN_JINN		= "COIN_JINN";		/* Jinn */
	public static final String KEYTYPE_COIN_SDC		= "COIN_SDC";		/* ShadowCash */
	public static final String KEYTYPE_COIN_NAV		= "COIN_NAV";		/* Navajo */
	public static final String KEYTYPE_COIN_XPM		= "COIN_XPM";		/* Primecoin */
	public static final String KEYTYPE_COIN_JLH		= "COIN_JLH";		/* jl777hodl */
	public static final String KEYTYPE_COIN_FTC		= "COIN_FTC";		/* Feathercoin */
	public static final String KEYTYPE_COIN_VIA		= "COIN_VIA";		/* Viacoin */
	public static final String KEYTYPE_COIN_I0C		= "COIN_I0C";		/* I0Coin */
	public static final String KEYTYPE_COIN_PTS		= "COIN_PTS";		/* BitShares PTS */
	public static final String KEYTYPE_COIN_SF0		= "COIN_SF0";		/* sharkfund0 */
	public static final String KEYTYPE_COIN_FAIR		= "COIN_FAIR";		/* Faircoin */
	public static final String KEYTYPE_COIN_BILS		= "COIN_BILS";		/* BilShares */
	public static final String KEYTYPE_COIN_NLG		= "COIN_NLG";		/* Guldencoin */
	public static final String KEYTYPE_COIN_POT		= "COIN_POT";		/* PotCoin */
	public static final String KEYTYPE_COIN_HYPER		= "COIN_HYPER";		/* Hyper */
	public static final String KEYTYPE_COIN_ARCH		= "COIN_ARCH";		/* ARCHcoin */
	public static final String KEYTYPE_COIN_VRC		= "COIN_VRC";		/* VeriCoin */
	public static final String KEYTYPE_COIN_BURST		= "COIN_BURST";		/* Burst */
	public static final String KEYTYPE_COIN_UNB		= "COIN_UNB";		/* UnbreakableCoin */
	public static final String KEYTYPE_COIN_SKYNET		= "COIN_SKYNET";	/* SkyNET */
	public static final String KEYTYPE_COIN_XC		= "COIN_XC";		/* XCurrency */
	public static final String KEYTYPE_COIN_URO		= "COIN_URO";		/* Uro */
	public static final String KEYTYPE_COIN_DGB		= "COIN_DGB";		/* DigiByte */
	public static final String KEYTYPE_COIN_CURE		= "COIN_CURE";		/* CureCoin */
	public static final String KEYTYPE_COIN_ZET		= "COIN_ZET";		/* Zetacoin */
	public static final String KEYTYPE_COIN_APC		= "COIN_APC";		/* Applecoin */
	public static final String KEYTYPE_COIN_CANN		= "COIN_CANN";		/* CannabisCoin */
	public static final String KEYTYPE_COIN_SYS		= "COIN_SYS";		/* SysCoin */
	public static final String KEYTYPE_COIN_SPR		= "COIN_SPR";		/* SpreadCoin */
	public static final String KEYTYPE_COIN_CNMT		= "COIN_CNMT";		/* Coinomat */
	public static final String KEYTYPE_COIN_NXTC		= "COIN_NXTC";		/* NXTcoinsco / Tradebots */
	public static final String KEYTYPE_COIN_MTC		= "COIN_MTC";		/* Marinecoin */
	public static final String KEYTYPE_COIN_UTC		= "COIN_UTC";		/* UltraCoin */
	public static final String KEYTYPE_COIN_XST		= "COIN_XST";		/* Stealthcoin */
	public static final String KEYTYPE_COIN_DICE		= "COIN_DICE";		/* NeoDICE */
	public static final String KEYTYPE_COIN_PND		= "COIN_PND";		/* Pandacoin (PND) */
	public static final String KEYTYPE_COIN_SLR		= "COIN_SLR";		/* SolarCoin */
	public static final String KEYTYPE_COIN_QORA		= "COIN_QORA";		/* Qora */
	public static final String KEYTYPE_COIN_CCS		= "COIN_CCS";		/* CryptoCoins */
	public static final String KEYTYPE_COIN_GLD		= "COIN_GLD";		/* GoldCoin */
	public static final String KEYTYPE_COIN_BAY		= "COIN_BAY";		/* BitBay */
	public static final String KEYTYPE_COIN_BITCNY		= "COIN_BITCNY";	/* BitCNY */
	public static final String KEYTYPE_COIN_DGC		= "COIN_DGC";		/* Digitalcoin */
	public static final String KEYTYPE_COIN_DEM		= "COIN_DEM";		/* Deutsche eMark */
	public static final String KEYTYPE_COIN_XCPSCO		= "COIN_XCPSCO";	/* Scotcoin */
	public static final String KEYTYPE_COIN_BLU		= "COIN_BLU";		/* BlueCoin */
	public static final String KEYTYPE_COIN_BOST		= "COIN_BOST";		/* BoostCoin */
	public static final String KEYTYPE_COIN_MOON		= "COIN_MOON";		/* Mooncoin */
	public static final String KEYTYPE_COIN_VTC		= "COIN_VTC";		/* Vertcoin */
	public static final String KEYTYPE_COIN_NODE		= "COIN_NODE";		/* Node */
	public static final String KEYTYPE_COIN_MMXIV		= "COIN_MMXIV";		/* Maiuticoin */
	public static final String KEYTYPE_COIN_DVC		= "COIN_DVC";		/* Devcoin */
	public static final String KEYTYPE_COIN_OCUPY		= "COIN_OCUPY";		/* Ocupy */
	public static final String KEYTYPE_COIN_ZEIT		= "COIN_ZEIT";		/* Zeitcoin */
	public static final String KEYTYPE_COIN_SWIFT		= "COIN_SWIFT";		/* BitSwift */
	public static final String KEYTYPE_COIN_DMD		= "COIN_DMD";		/* Diamond */
	public static final String KEYTYPE_COIN_XDP		= "COIN_XDP";		/* Dogeparty */
	public static final String KEYTYPE_COIN_CZC		= "COIN_CZC";		/* CzechCrownCoin */
	public static final String KEYTYPE_COIN_PRVBET		= "COIN_PRVBET";	/* Privatebet */
	public static final String KEYTYPE_COIN_ZCC		= "COIN_ZCC";		/* ZcCoin */
	public static final String KEYTYPE_COIN_OPAL		= "COIN_OPAL";		/* Opal */
	public static final String KEYTYPE_COIN_START		= "COIN_START";		/* Startcoin */
	public static final String KEYTYPE_COIN_NTR		= "COIN_NTR";		/* Neutrino */
	public static final String KEYTYPE_COIN_MRKT		= "COIN_MRKT";		/* FreeMarket */
	public static final String KEYTYPE_COIN_NET		= "COIN_NET";		/* NetCoin */
	public static final String KEYTYPE_COIN_VPN		= "COIN_VPN";		/* VPNCoin */
	public static final String KEYTYPE_COIN_GSX		= "COIN_GSX";		/* GlowShares */
	public static final String KEYTYPE_COIN_LTBC		= "COIN_LTBC";		/* LTBcoin */
	public static final String KEYTYPE_COIN_GMC		= "COIN_GMC";		/* GamersCoin */
	public static final String KEYTYPE_COIN_SYNC		= "COIN_SYNC";		/* Sync */
	public static final String KEYTYPE_COIN_XWT		= "COIN_XWT";		/* World Trade Funds */
	public static final String KEYTYPE_COIN_CCN		= "COIN_CCN";		/* Cannacoin */
	public static final String KEYTYPE_COIN_NEM		= "COIN_NEM";		/* NEMstake */
	public static final String KEYTYPE_COIN_JPC		= "COIN_JPC";		/* JackpotCoin */
	public static final String KEYTYPE_COIN_PINK		= "COIN_PINK";		/* PinkCoin */
	public static final String KEYTYPE_COIN_ATOMIC		= "COIN_ATOMIC";	/* ATOMIC */
	public static final String KEYTYPE_COIN_AC		= "COIN_AC";		/* AsiaCoin */
	public static final String KEYTYPE_COIN_MGW		= "COIN_MGW";		/* MGW */
	public static final String KEYTYPE_COIN_MINT		= "COIN_MINT";		/* Mintcoin */
	public static final String KEYTYPE_COIN_NAUT		= "COIN_NAUT";		/* NautilusCoin */
	public static final String KEYTYPE_COIN_HBN		= "COIN_HBN";		/* HoboNickels */
	public static final String KEYTYPE_COIN_XCH		= "COIN_XCH";		/* ClearingHouse */
	public static final String KEYTYPE_COIN_KARM		= "COIN_KARM";		/* Karmacoin */
	public static final String KEYTYPE_COIN_WATER		= "COIN_WATER";		/* CleanWaterCoin */
	public static final String KEYTYPE_COIN_MYR		= "COIN_MYR";		/* Myriadcoin */
	public static final String KEYTYPE_COIN_TIPS		= "COIN_TIPS";		/* FedoraCoins */
	public static final String KEYTYPE_COIN_BBR		= "COIN_BBR";		/* Boolberry */
	public static final String KEYTYPE_COIN_USDE		= "COIN_USDE";		/* USDe */
	public static final String KEYTYPE_COIN_RIC		= "COIN_RIC";		/* Riecoin */
	public static final String KEYTYPE_COIN_VIOR		= "COIN_VIOR";		/* Viorcoin */
	public static final String KEYTYPE_COIN_BTM		= "COIN_BTM";		/* Bitmark */
	public static final String KEYTYPE_COIN_UNC		= "COIN_UNC";		/* UnionCoin */
	public static final String KEYTYPE_COIN_CGB		= "COIN_CGB";		/* Cryptogenic Bullion */
	public static final String KEYTYPE_COIN_ARI		= "COIN_ARI";		/* Aricoin */
	public static final String KEYTYPE_COIN_HYP		= "COIN_HYP";		/* HyperStake */
	public static final String KEYTYPE_COIN_HTML5		= "COIN_HTML5";		/* HTMLCOIN */
	public static final String KEYTYPE_COIN_FLT		= "COIN_FLT";		/* FlutterCoin */
	public static final String KEYTYPE_COIN_EAC		= "COIN_EAC";		/* EarthCoin */
	public static final String KEYTYPE_COIN_HZ		= "COIN_HZ";		/* Horizon */
	public static final String KEYTYPE_COIN_NOO		= "COIN_NOO";		/* NooCoin */
	public static final String KEYTYPE_COIN_EFL		= "COIN_EFL";		/* Electronic Guilden */
	public static final String KEYTYPE_COIN_TEST		= "COIN_TEST";		/* TEST */
	
	public static String FormatError(int ERROR) {
		return (ERROR < 0 ? "-" : "")+
			"0x"+
			("00000000" + Integer.toHexString(ERROR)).substring(Integer.toHexString(ERROR).length());
	}
}
