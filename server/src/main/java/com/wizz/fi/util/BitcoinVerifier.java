package com.wizz.fi.util;


import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Utils;

public class BitcoinVerifier {
    static {
        System.out.println("java.library.path is " + System.getProperty("java.library.path"));
        System.loadLibrary("sig_verifier_jni");
    }

//    public static native boolean verify(String userAddress, String publicKey, String signature, String message);

    public static native boolean verifyAddress(String publicKey, String address);

    public static boolean verify(String address, String publicKey, String signature, String message) {
        return verifyAddress(publicKey, address) && verifyMessage(publicKey, message, signature);
    }

    public static boolean verifyMessage(String publicKey, String message, String signature) {
        try {
            final ECKey key = ECKey.fromPublicOnly(Utils.HEX.decode(publicKey));
            key.verifyMessage(message, signature);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
