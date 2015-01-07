package com.rosiewallet;

public class Rivet {
	// Intent
	public static final String RIVET_INTENT		= "com.rivetz.RivetzAndroid";
	// Request Codes: Virtual Coin specific functionality
	public static final int REQUEST_VC_SIGNTRANS	= 1001; // Sign a bitcoin transaction
	public static final int REQUEST_VC_GETPUBPRV	= 1999; // Depreciating: Get Public key from Private no service provider
	// Request Codes: Service Provider key functionality for storing keys in RivetAndroid
	public static final int REQUEST_ADDKEY		= 2001; // Can also be used to update a key
	public static final int REQUEST_GETKEY		= 2002; // Get Key
	public static final int REQUEST_DELETEKEY	= 2003; // Delete a Key
	public static final int REQUEST_ENUMKEYS	= 2004; // Get Next Key Enumerating through them.
	// Request Codes: ECDSA
	public static final int REQUEST_ECDSA_CREATE	= 3001; // Create a ECDSA Key pair
	public static final int REQUEST_ECDSA_SIGN	= 3002; // Create ECDSA Signature
	public static final int REQUEST_ECDSA_VERIFY	= 3003; // Verify an ECDSA Signature
	public static final int REQUEST_ECDSA_GETPUBPRV	= 3004; // Extract a public key out of a private key
	public static final int REQUEST_ECDSA_GETPUBSIG	= 3005; // Extract a public key out of a message, signature and curve.
	// Request Codes: ECDH
	public static final int REQUEST_ECDH_SHARED	= 4000; // Shared Key using ECDH
	public static final int REQUEST_ECDH_ENCRYPT	= 4001; // Encrypted Data using ECDH
	public static final int REQUEST_ECDH_DECRYPT	= 4002; // Decrypt Data using ECDH
	// Request Codes: AES
	public static final int REQUEST_AES_ENCRYPT	= 5001; // Encrypted Data using AES
	public static final int REQUEST_AES_DECRYPT	= 5002; // Decrypt Data using AES
	// Request Codes: RSA
	public static final int REQUEST_RSA_CREATE	= 6001; // Create a ECDSA Key pair
	public static final int REQUEST_RSA_SIGN	= 6002; // Create ECDSA Signature
	public static final int REQUEST_RSA_VERIFY	= 6003; // Verify an ECDSA Signature
	public static final int REQUEST_RSA_ENCRYPT	= 6004; // Encrypted Data using AES
	public static final int REQUEST_RSA_DECRYPT	= 6005; // Decrypt Data using AES
	// Request Codes: HASH
	public static final int REQUEST_HASH		= 7001; // Get a hash result
	// Extra Strings
	public static final String EXTRA_REQUEST	= "requestCode";
	public static final String EXTRA_PUB		= "PUB";
	public static final String EXTRA_PRV		= "PRV";
	public static final String EXTRA_TOPUB		= "TOPUB";
	public static final String EXTRA_AMT		= "AMT";
	public static final String EXTRA_FEE		= "FEE";
	public static final String EXTRA_TRANS		= "TRANS";
	public static final String EXTRA_SIGNED		= "SignedTrans";
	public static final String EXTRA_PROVIDER	= "ProviderID";
	public static final String EXTRA_KEYNAME	= "KeyName";
	public static final String EXTRA_PUBLICDATA	= "PublicData";
	public static final String EXTRA_SECUREDATA	= "SecureData";
	public static final String EXTRA_ECC_CURVE	= "Curve";
	public static final String EXTRA_VC		= "vc";
	public static final String EXTRA_VC_PUBADDR	= "PublicAddress";
	public static final String EXTRA_PUBKEY		= "PublicKey";
	public static final String EXTRA_PRVKEY		= "PrivateKey";
	public static final String EXTRA_MESSAGE	= "Message";
	public static final String EXTRA_SIGNATURE	= "Signature";
	public static final String EXTRA_VERIFIED	= "Verified";
	public static final String EXTRA_SHAREDKEY	= "SharedKey";
	public static final String EXTRA_KEY		= "Key";
	// ECC Curves
	public static final String CURVE_SECP192R1	= "SECP192R1";	/*!< 192-bits NIST curve  */
	public static final String CURVE_SECP224R1	= "SECP224R1";	/*!< 224-bits NIST curve  */
	public static final String CURVE_SECP256R1	= "SECP256R1";	/*!< 256-bits NIST curve  */
	public static final String CURVE_SECP384R1	= "SECP384R1";	/*!< 384-bits NIST curve  */
	public static final String CURVE_SECP521R1	= "SECP521R1";	/*!< 521-bits NIST curve  */
	public static final String CURVE_BP256R1	= "BP256R1";	/*!< 256-bits Brainpool curve */
	public static final String CURVE_BP384R1	= "BP384R1";	/*!< 384-bits Brainpool curve */
	public static final String CURVE_BP512R1	= "BP512R1";	/*!< 512-bits Brainpool curve */
	public static final String CURVE_M221		= "M221";	/*!< (not implemented yet)    */
	public static final String CURVE_M255		= "M255";	/*!< Curve25519               */
	public static final String CURVE_M383		= "M383";	/*!< (not implemented yet)    */
	public static final String CURVE_M511		= "M511";	/*!< (not implemented yet)    */
	public static final String CURVE_SECP192K1	= "SECP192K1";	/*!< 192-bits "Koblitz" curve */
	public static final String CURVE_SECP224K1	= "SECP224K1";	/*!< 224-bits "Koblitz" curve */
	public static final String CURVE_SECP256K1	= "SECP256K1";	/*!< 256-bits "Koblitz" curve */
	// ALGOs
	public static final String ECC_ALGO_AES		= "AES";
	// HASHs
	public static final String ECC_HASH_SHA256	= "SHA256";
	public static final String ECC_HASH_SHA256x2	= "SHA256x2";
	
}
