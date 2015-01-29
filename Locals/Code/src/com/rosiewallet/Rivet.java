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
	public static final int REQUEST_VC_GETPUBPRV	= 1999; // DEPRECIATING: Get Public key from Private no service provider
	// Request Codes: Service Provider key functionality for storing keys in RivetAndroid
	public static final int REQUEST_ADDKEY		= 2001; // DEPRECIATING: Can also be used to update a key
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
	public static final String EXTRA_RESULTCODE	= "ErrorMessage";
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
	public static final String EXTRA_ECC_CURVE = "Curve"; // DEPRECIATED
	public static final String EXTRA_KEYTYPE	= "KeyType";
	public static final String EXTRA_VC		= "vc";
	public static final String EXTRA_VC_PUBADDR	= "PublicAddress";
	public static final String EXTRA_PUBKEY		= "PublicKey";
	public static final String EXTRA_PRVKEY		= "PrivateKey";
	public static final String EXTRA_MESSAGE	= "Message";
	public static final String EXTRA_SIGNATURE	= "Signature";
	public static final String EXTRA_VERIFIED	= "Verified";
	public static final String EXTRA_SHAREDKEY	= "SharedKey";
	public static final String EXTRA_KEY		= "Key";
	public static final String EXTRA_HASH_ALGO	= "HashAlgo";
	// KEYTYPE
	public static final String KEYTYPE_ECDSA_DEFAULT 	= "ECDSA_SECP256K1";	/*!< 256-bits "Koblitz" curve (Bitcoin) */
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
	public static final String CURVE_SECP256K1 = "SECP256K1"; // DEPRECIATED
	// ALGOs
	public static final String ECC_ALGO_AES		= "AES";
	// HASHs
	public static final String HASH_SHA256		= "SHA256";
	public static final String HASH_SHA256x2	= "SHA256x2";
	
}
