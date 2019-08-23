package com.fonkwill.fogstorage.client.encryption.impl;

import com.fonkwill.fogstorage.client.encryption.Encrypter;
import com.fonkwill.fogstorage.client.encryption.exception.EncryptionException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RsaEncryptionService extends AbstractRsaService implements Encrypter {

    PublicKey publicKey;

    public RsaEncryptionService(String publicKey) throws EncryptionException {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey.getBytes()));

        try {
            KeyFactory keyFactory = KeyFactory.getInstance(this.algorithm);
            this.publicKey = keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new EncryptionException("Could not instantiate RsaEncryptionService", e);
        }

    }

    @Override
    public byte[] encrypt(byte[] content) throws EncryptionException {

        try {
            Cipher cipher = Cipher.getInstance(this.algorithmAndPadding);
            cipher.init(Cipher.ENCRYPT_MODE, this.publicKey);
            return cipher.doFinal(content);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new EncryptionException("Could not encrypt data with public key", e);
        }
    }
}
