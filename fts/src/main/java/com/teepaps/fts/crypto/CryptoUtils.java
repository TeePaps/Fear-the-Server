package com.teepaps.fts.crypto;

import com.google.common.io.BaseEncoding;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by ted on 4/11/14.
 */
public class CryptoUtils {

    /**
     * Block Encryption algorithm.
     */
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    /**
     * Delimited to separate parts of cipher text
     */
    private static final String DELIMITER = "]";

    /**
     * Standard max key length for AES
     */
    private static final int KEY_LENGTH = 128;

    /**
     * Random number generator
     */
    private static SecureRandom random = new SecureRandom();

    /**
     * Adds the new security provider
     */
    static {
        Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());
    }

    /**
     * Wrapper for encrypting a message using the key retrieved from the database
     * @param key
     * @param plainText
     * @return
     * @throws Exception
     */
    public static String encrypt(byte[] key, String plainText)
            throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, 0, key.length, "AES");
        return encrypt(secretKey, plainText);
    }

    /**
     * Encrypts a string of cipher text that has an IV using the key provided.
     * @param key
     * @param plaintext
     * @return
     * @throws Exception
     */
    private static String encrypt(SecretKey key, String plaintext)
            throws Exception {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");

            byte[] iv = generateIv(cipher.getBlockSize());
            IvParameterSpec ivParams = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);
            byte[] cipherText = cipher.doFinal(plaintext.getBytes("UTF-8"));

            return String.format("%s%s%s", new String(BaseEncoding.base64().encode(iv)),
                    DELIMITER, new String(BaseEncoding.base64().encode(cipherText)));
        } catch (Throwable e) {
            throw new Exception("Error while encryption", e);
        }
    }

    /**
     * Wrapper for decrypting a message using the key retrieved from the database
     * @param key
     * @param key
     * @param cipherText
     * @return
     * @throws Exception
     */
    public static String decrypt(byte[] key, String cipherText)
            throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, 0, key.length, "AES");
        return decrypt(secretKey, cipherText);
    }

    /**
     * Decrypts a string of cipher text that has an IV using the key provided.
     * @param key byte array stored in database
     * @param cipherText
     * @return
     * @throws Exception
     */
    private static String decrypt(SecretKey key, String cipherText)
            throws Exception {
        String[] fields = cipherText.split(DELIMITER);
        if (fields.length != 2) {
            throw new IllegalArgumentException("Invalid encrypted text format");
        }
        try {
            byte[] iv = BaseEncoding.base64().decode(fields[0]);
            byte[] cipherBytes = BaseEncoding.base64().decode(fields[1]);

            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
            IvParameterSpec ivParams = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, key, ivParams);
            byte[] plaintext = cipher.doFinal(cipherBytes);
            String plainrStr = new String(plaintext, "UTF-8");

            return plainrStr;
        } catch (Throwable e) {
            throw new Exception("Error while decryption", e);
        }
    }

    /**
     * <a href="android-developers.blogspot.pt/2013/02/using-cryptography-to-store-credentials.html>Android Blog</a>
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        // Generate a 256-bit key
        final int outputKeyLength = KEY_LENGTH;

        SecureRandom secureRandom = new SecureRandom();
        // Do *not* seed secureRandom! Automatically seeded from system entropy.
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(outputKeyLength, secureRandom);
        SecretKey key = keyGenerator.generateKey();
        return key;
    }

    /**
     * Wrapper to get the bytes of the secret key generated
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static byte[] generateKey() throws NoSuchAlgorithmException {
        return generateSecretKey().getEncoded();
    }

    /**
     * Generates a random initialization vector
     * @param length
     * @return
     */
    private static byte[] generateIv(int length) {
        byte[] b = new byte[length];
        random.nextBytes(b);

        return b;
    }
}
