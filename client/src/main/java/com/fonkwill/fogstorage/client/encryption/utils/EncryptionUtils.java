package com.fonkwill.fogstorage.client.encryption.utils;

import com.fonkwill.fogstorage.client.encryption.exception.EncryptionException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

import java.nio.charset.StandardCharsets;
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
            keyGenerator.init(length);
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] byteKey = secretKey.getEncoded();
            return  DatatypeConverter.printHexBinary(byteKey);
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionException("Wrong Algorithm used");
        }

    }



}
