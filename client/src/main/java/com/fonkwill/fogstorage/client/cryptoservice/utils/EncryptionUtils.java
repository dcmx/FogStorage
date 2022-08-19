package com.fonkwill.fogstorage.client.cryptoservice.utils;

import com.fonkwill.fogstorage.client.cryptoservice.exception.EncryptionException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class EncryptionUtils {

    /**
     * Generates a key with the given length in bit
     * @param length The length of the random key in bit (should be a multiple of 8)
     * @return A random string with the given length in bit
     */
    public static String getKey(int length, String algorithm) throws EncryptionException {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
            keyGenerator.init(length, SecureRandomUtil.getSecureRandomInstance());
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] byteKey = secretKey.getEncoded();
            return  DatatypeConverter.printHexBinary(byteKey);
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionException("Wrong Algorithm used");
        }

    }

    public static KeyPair getKeyPair(int length) throws EncryptionException {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(length);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionException("Wrong algorithm used");
        }

    }

    public static String getPrivateKey(KeyPair keyPair) {
        return Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
    }

    public static String getPublicKey(KeyPair keyPair) {
        return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());

    }





}
