package com.fonkwill.fogstorage.client.encryption.impl;

import com.fonkwill.fogstorage.client.encryption.Decrypter;
import com.fonkwill.fogstorage.client.encryption.exception.EncryptionException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class RsaDecryptionService extends  AbstractRsaService implements Decrypter {

    PrivateKey privateKey;

    public RsaDecryptionService(String privateKey) throws EncryptionException {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey.getBytes()));

        try {
            KeyFactory keyFactory = KeyFactory.getInstance(this.algorithm);
            this.privateKey = keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new EncryptionException("Could not instantiate RsaDecryptionService",e );
        }

    }

    @Override
    public byte[] decrypt(byte[] content) throws EncryptionException {
        try {
            Cipher cipher = Cipher.getInstance(this.algorithmAndPadding);
            cipher.init(Cipher.DECRYPT_MODE, this.privateKey);
            return cipher.doFinal(content);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new EncryptionException("Could not encrypt data with public key", e);
        }
    }
}
