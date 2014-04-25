package Crypto;

import java.security.Security;

import javax.crypto.SecretKey;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.google.common.io.BaseEncoding;

public class TestingAES {

    public static void main(String [] args) throws Exception {
        Security.insertProviderAt(new BouncyCastleProvider(), 1);

        byte[] keyBytes = CryptoUtils.generateKey();

        System.out.println("keyBytes.length: " + keyBytes.length);
        
        
        String key = BaseEncoding.base64().encode(keyBytes);
        System.out.println("Key is: " + key);
        System.out.println("key.length: " + keyBytes.length);
        
        String message = "This is my message. Plus some other stuff";
        
        String cipherText = CryptoUtils.encrypt(keyBytes, message);
        System.out.println("Cipher Text: " + cipherText);
        
        
        byte[] decodedKey = BaseEncoding.base64().decode(key);
        String plainText = CryptoUtils.decrypt(decodedKey, cipherText);
        System.out.println("Plain Text: " + plainText);
//        String passphrase = "The quick brown fox jumped over the lazy brown dog";
//        String plaintext = "hello world";
//        byte [] ciphertext = encrypt(passphrase, plaintext);
//        String recoveredPlaintext = decrypt(passphrase, ciphertext);
//
//        System.out.println(recoveredPlaintext);
    }
}

